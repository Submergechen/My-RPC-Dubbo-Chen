import serialize.CommonSerializer;

import transport.RPCClient;
import transport.RpcClientProxy;
import transport.netty.client.NettyClient;

/**
 * 测试用Netty消费者
 *
 * @author submerge
 */
public class NettyTestClient {

    public static void main(String[] args) {
        RPCClient client = new NettyClient(CommonSerializer.PROTOBUF_SERIALIZER);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);
        ByeService byeService = rpcClientProxy.getProxy(ByeService.class);
        System.out.println(byeService.bye("Netty"));
    }

}
