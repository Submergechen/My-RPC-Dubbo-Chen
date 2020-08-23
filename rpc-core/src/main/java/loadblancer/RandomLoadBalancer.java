package loadblancer;



import java.util.List;
import java.util.Random;

/**
 * @author submerge
 */
public class RandomLoadBalancer extends AbstractLoadBalance {

    @Override
    protected String doSelect(List<String> serviceAddresses) {
        Random random = new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }

}
