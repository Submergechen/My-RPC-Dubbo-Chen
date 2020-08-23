package transport;

import handler.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registry.ServiceRegistry;
import transport.socket.server.RequestHandlerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @author submerge
 * @date 2020/8/20 18:06
 */
public interface RPCServer {

    void start();
    <T> void publishService(T service, String serviceName);
}

