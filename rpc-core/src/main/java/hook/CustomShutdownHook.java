package hook;


import lombok.extern.slf4j.Slf4j;
import registry.zk.util.CuratorUtils;
import util.concurrent.threadpool.ThreadPoolFactoryUtils;

/**
 * When the server  is closed, do something such as unregister all services
 *
 */
@Slf4j
public class CustomShutdownHook {
    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

    public static CustomShutdownHook getCustomShutdownHook() {
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public void clearAll() {
        log.info("addShutdownHook for clearAll");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            CuratorUtils.clearRegistry(CuratorUtils.getZkClient());
            ThreadPoolFactoryUtils.shutDownAllThreadPool();
        }));
    }
}
