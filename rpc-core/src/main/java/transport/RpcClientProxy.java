package transport;

import entity.RPCRequest;
import entity.RPCResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import transport.netty.client.NettyClient;
import transport.socket.client.SocketClient;
import util.RPCMessageChecker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * RPC客户端动态代理
 *
 * @author submerge
 */
public class RpcClientProxy implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);

    private final RPCClient client;

    public RpcClientProxy(RPCClient client) {
        this.client = client;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        logger.info("调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());
        RPCRequest rpcRequest = new RPCRequest(UUID.randomUUID().toString(), method.getDeclaringClass().getName(),
                method.getName(), args, method.getParameterTypes(), false);
        RPCResponse rpcResponse = null;
        if (client instanceof NettyClient) {
            try {
                CompletableFuture<RPCResponse> completableFuture = (CompletableFuture<RPCResponse>) client.sendRequest(rpcRequest);
                rpcResponse = completableFuture.get();
            } catch (Exception e) {
                logger.error("方法调用请求发送失败", e);
                return null;
            }
        }
        if (client instanceof SocketClient) {
            rpcResponse = (RPCResponse) client.sendRequest(rpcRequest);
        }
        RPCMessageChecker.check(rpcRequest, rpcResponse);
        return rpcResponse.getData();
    }
}
