package registry.zk;


import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import registry.ServiceRegistry;
import registry.zk.util.CuratorUtils;

import java.net.InetSocketAddress;

/**
 * service registration  based on zookeeper
 *

 */
@Slf4j
public class ZkServiceRegistry implements ServiceRegistry {

    @Override
    public void registerService(String serviceName, InetSocketAddress inetSocketAddress) {
        String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + serviceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient, servicePath);
    }
}
