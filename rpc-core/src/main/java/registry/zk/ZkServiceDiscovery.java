package registry.zk;



import enumeration.RPCError;
import exception.RPCException;

import loadblancer.LoadBalance;
import loadblancer.RandomLoadBalancer;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import registry.ServiceDiscovery;
import registry.zk.util.CuratorUtils;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * service discovery based on zookeeper
 *

 */

public class ZkServiceDiscovery implements ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ZkServiceDiscovery.class);
    private final LoadBalance loadBalance;

    public ZkServiceDiscovery(LoadBalance loadBalance) {
        if(loadBalance == null) this.loadBalance = new RandomLoadBalancer();
        else this.loadBalance = new RandomLoadBalancer();
    }

    @Override
    public InetSocketAddress lookupService(String rpcServiceName) {
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (serviceUrlList.size() == 0) {
            throw new RPCException(RPCError.SERVICE_NOT_FOUND, rpcServiceName);
        }
        // load balancing
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList);

        logger.info("Successfully found the service address:[{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
