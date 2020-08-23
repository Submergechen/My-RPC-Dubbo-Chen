package transport;

import entity.RPCRequest;
import entity.RPCResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.dc.pr.PRError;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * @author submerge
 * @date 2020/8/20 18:12
 */
public class WorkerThread implements Runnable {
    private Socket socket;
    private Object service;
    private static final Logger logger = LoggerFactory.getLogger(RPCServer.class);
    public WorkerThread(Socket socket, Object service) {
        this.socket = socket;
        this.service = service;
    }

    @Override
    public void run() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            RPCRequest rpcRequest = (RPCRequest) objectInputStream.readObject();
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            Object returnObject = method.invoke(service, rpcRequest.getParameters());
            objectOutputStream.writeObject(RPCResponse.success(returnObject,"ssss"));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error("调用或发送时有错误发生：", e);
        }
    }
}
