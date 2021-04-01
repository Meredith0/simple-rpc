package cache;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import model.ServiceInstance;

/**
 * @author zeng.fk
 *     2021-04-01 23:07
 */
@Data
public class RegisteredServiceCache {
    /**
     *  key: serviceName
     *  value: servicePath
     */
    public static final Map<String, List<String>> CACHE_MAP = new ConcurrentHashMap<>();

}
