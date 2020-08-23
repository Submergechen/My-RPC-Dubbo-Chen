

/**
 * @author submerge
 * @date 2020/8/20 17:23
 */
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(HelloObject object) {
        return "这是掉用的返回值，id=" + object.getId();
    }
}
