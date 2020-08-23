package util;

import entity.RPCRequest;
import entity.RPCResponse;
import enumeration.RPCError;
import enumeration.ResponseCode;
import exception.RPCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 检查响应与请求
 *
 * @author submerge
 */
public class RPCMessageChecker {

    public static final String INTERFACE_NAME = "interfaceName";
    private static final Logger logger = LoggerFactory.getLogger(RPCMessageChecker.class);

    private RPCMessageChecker() {
    }

    public static void check(RPCRequest rpcRequest, RPCResponse rpcResponse) {
        if (rpcResponse == null) {
            logger.error("调用服务失败,serviceName:{}", rpcRequest.getInterfaceName());
            throw new RPCException(RPCError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RPCException(RPCError.RESPONSE_NOT_MATCH, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (rpcResponse.getStatusCode() == null || !rpcResponse.getStatusCode().equals(ResponseCode.SUCCESS.getCode())) {
            logger.error("调用服务失败,serviceName:{},RpcResponse:{}", rpcRequest.getInterfaceName(), rpcResponse);
            throw new RPCException(RPCError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }

}
