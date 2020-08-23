package transport;

import entity.RPCRequest;
import serialize.CommonSerializer;


/**
 * @author submerge
 * @date 2020/8/20 17:57
 */
public interface RPCClient {

    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;
    Object sendRequest(RPCRequest rpcRequest);


}
