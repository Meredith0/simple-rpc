package rpc.simple.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import rpc.simple.extension.ExtensionLoader;

import java.io.IOException;
import java.util.Properties;

/**
 * 配置文件工具类
 * @author zeng.fk
 * 2021-04-11 14:02
 */
@Slf4j
public class PropertiesUtil {

    public static final String PROPERTIES_LOCATION = "application.properties";
    public static final String ZOOKEEPER_PATH = "rpc.registry.zookeeper.url";
    private static final String ZOOKEEPER_URL = "127.0.0.1:2181";
    private static final String TIMEOUT = "rpc.remoting.timeout";
    private static final String DEFAULT_TIMEOUT = "10000";
    private static Properties properties = null;

    static {
        ClassPathResource resource = new ClassPathResource(PROPERTIES_LOCATION);
        try {
            properties = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public static String getZkUrl() {
        return properties.getProperty(ZOOKEEPER_PATH, ZOOKEEPER_URL);
    }

    @SneakyThrows
    public static String getTimeoutMillis() {
        return properties.getProperty(TIMEOUT, DEFAULT_TIMEOUT);
    }

    @SneakyThrows
    public static Properties getExtensionProperties() {
        ClassPathResource resource = new ClassPathResource(ExtensionLoader.EXTENSION_DIR);
        return PropertiesLoaderUtils.loadProperties(resource);
    }

    /**
     * 解析一行配置文件
     * @param configStr name=xxx.xxx.xxx.classname
     * @return {name,classname}
     */
    public static String[] parseProperties(String configStr) {
        //忽略注释
        final int commentIndex = configStr.indexOf('#');
        if (commentIndex >= 0) {
            configStr = configStr.substring(0, commentIndex);
        }
        final int separate = configStr.indexOf('=');
        String name = configStr.substring(0, separate).trim();
        String classname = configStr.substring(separate + 1).trim();

        return new String[]{name, classname};
    }

}
