package exception;


import enumeration.RPCError;

/**
 * RPC调用异常
 *
 *
 */
public class RPCException extends RuntimeException {

    public RPCException(RPCError error, String detail) {
        super(error.getMessage() + ": " + detail);
    }

    public RPCException(String message, Throwable cause) {
        super(message, cause);
    }

    public RPCException(RPCError error) {
        super(error.getMessage());
    }

}
