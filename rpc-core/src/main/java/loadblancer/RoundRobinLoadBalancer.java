package loadblancer;


import java.util.List;

/**
 * @author submerge
 */
public class RoundRobinLoadBalancer extends AbstractLoadBalance {

    private int index = 0;

    @Override
    protected String doSelect(List<String> serviceAddresses) {
        if(index >= serviceAddresses.size()) {
            index %= serviceAddresses.size();
        }
        return serviceAddresses.get(index++);
    }

}
