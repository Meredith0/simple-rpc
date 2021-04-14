package rpc.simple.extension;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import rpc.simple.utils.PropertiesUtil;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author zeng.fk
 * 2021-04-11 18:05
 */
@Slf4j
public class ExtensionName {

    public static String REGISTRY;
    public static String DISCOVERY;
    public static String LOAD_BALANCE;
    public static String COMPRESSOR;
    public static String TRANSPORT;
    public static String SERIALIZER;
    public static String ROUTER;
    /**
     * key: classname, value: name
     */
    private static final Map<String, String> CONFIG_MAP = Maps.newConcurrentMap();

    //从配置文件中加载启用的扩展点名称
    static {
        Properties extensionProperties = PropertiesUtil.getExtensionProperties();
        Enumeration<?> extensionEnumeration = extensionProperties.propertyNames();
        //遍历META-INF/extensions 下的所有配置文件
        while (extensionEnumeration.hasMoreElements()) {
            String filename = extensionEnumeration.nextElement().toString();
            ClassPathResource fileResource = new ClassPathResource(ExtensionLoader.EXTENSION_DIR + filename);

            //遍历每一个配置文件
            try {
                Properties configProperties = PropertiesLoaderUtils.loadProperties(fileResource);
                Set<String> configKeySet = configProperties.stringPropertyNames();
                if (configKeySet.size() > 1) {
                    String msg = String.format("SPI扩展点:{%s}只能配置一个", filename);
                    throw new IllegalArgumentException(msg);
                }
                CONFIG_MAP.put(filename, configKeySet.iterator().next());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        printLog();
        REGISTRY = CONFIG_MAP.getOrDefault("rpc.zengfk.registry.ServiceRegistry", "registry-zk");
        DISCOVERY = CONFIG_MAP.getOrDefault("rpc.zengfk.registry.ServiceDiscovery", "discovery-zk");
        LOAD_BALANCE = CONFIG_MAP.getOrDefault("rpc.zengfk.loadBalance.LoadBalance", "consistentHash");
        COMPRESSOR = CONFIG_MAP.getOrDefault("rpc.zengfk.remoting.compression.Compressor", "gzip");
        TRANSPORT = CONFIG_MAP.getOrDefault("rpc.zengfk.remoting.transport.RpcTransport", "netty");
        SERIALIZER = CONFIG_MAP.getOrDefault("rpc.zengfk.serialize.Serializer", "protostuff");
        ROUTER = CONFIG_MAP.getOrDefault("rpc.zengfk.router.Router", "tagRouter");
    }

    public static void printLog() {
        log.info("启用配置文件:{}", CONFIG_MAP);
    }
}
