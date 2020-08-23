package transport.netty.client;

import entity.RPCRequest;
import entity.RPCResponse;
import enumeration.RPCError;
import exception.RPCException;
import factory.SingletonFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import loadblancer.LoadBalance;
import loadblancer.RandomLoadBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import registry.ServiceDiscovery;
import registry.zk.ZkServiceDiscovery;
import serialize.CommonSerializer;
import transport.RPCClient;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * NIO方式消费侧客户端类
 *
 * @author submerge
 */
public class NettyClient implements RPCClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private static final EventLoopGroup group;
    private static final Bootstrap bootstrap;

    static {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class);
    }

    private final ServiceDiscovery serviceDiscovery;
    private final CommonSerializer serializer;

    private final UnprocessedRequests unprocessedRequests;

    public NettyClient() {
        this(DEFAULT_SERIALIZER, new RandomLoadBalancer());
    }
    public NettyClient(LoadBalance loadBalancer) {
        this(DEFAULT_SERIALIZER, loadBalancer);
    }
    public NettyClient(Integer serializer) {
        this(serializer, new RandomLoadBalancer());
    }
    public NettyClient(Integer serializer, LoadBalance loadBalancer) {
        this.serviceDiscovery = new ZkServiceDiscovery(loadBalancer);
        this.serializer = CommonSerializer.getByCode(serializer);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    public CompletableFuture<RPCResponse> sendRequest(RPCRequest rpcRequest) {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RPCException(RPCError.SERIALIZER_NOT_FOUND);
        }
        CompletableFuture<RPCResponse> resultFuture = new CompletableFuture<>();
        try {
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if (!channel.isActive()) {
                group.shutdownGracefully();
                return null;
            }
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()) {
                    logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                } else {
                    future1.channel().close();
                    resultFuture.completeExceptionally(future1.cause());
                    logger.error("发送消息时有错误发生: ", future1.cause());
                }
            });
        } catch (InterruptedException e) {
            unprocessedRequests.remove(rpcRequest.getRequestId());
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }

}
