package rpc.zengfk.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author zeng.fk
 * 2021-04-07 23:58
 */
@Slf4j
@Configuration
public class ExtensionLoaderConfig implements InitializingBean {

    public static String REGISTRY = "registry-zk";
    public static String DISCOVERY = "discovery-zk";
    public static String LOAD_BALANCE = "consistentHash";
    public static String COMPRESSOR = "gzip";
    public static String TRANSPORT = "netty";
    public static String SERIALIZER = "protostuff";


    @Value("${rpc.extension.registry:registry-zk}")
    private String registry;
    @Value("${rpc.extension.discovery:discovery-zk}")
    private String discovery;
    @Value("${rpc.extension.loadBalance:consistentHash}")
    private String loadBalance;
    @Value("${rpc.extension.compressor:gzip}")
    private String compressor;
    @Value("${rpc.extension.transport:netty}")
    private String transport;
    @Value("${rpc.extension.serializer:protostuff}")
    private String serializer;

    @Override
    public void afterPropertiesSet() throws Exception {
        REGISTRY = registry;
        DISCOVERY = discovery;
        LOAD_BALANCE = loadBalance;
        COMPRESSOR = compressor;
        TRANSPORT = transport;
        SERIALIZER = serializer;
    }
}
