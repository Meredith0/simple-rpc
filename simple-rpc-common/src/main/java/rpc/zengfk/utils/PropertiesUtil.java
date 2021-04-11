package rpc.zengfk.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import rpc.zengfk.extension.ExtensionLoader;

import java.util.Properties;
import java.util.Stack;

/**
 * 配置文件工具类
 * @author zeng.fk
 * 2021-04-11 14:02
 */
@Slf4j
public class PropertiesUtil {

    public static final String PROPERTIES_LOCATION = "application.properties";
    public static final String ZOOKEEPER_PATH = "rpc.registry.zookeeper.url";


    @SneakyThrows
    public static String getZkUrl() {
        ClassPathResource resource = new ClassPathResource(PROPERTIES_LOCATION);
        Properties properties = PropertiesLoaderUtils.loadProperties(resource);
        return properties.getProperty(ZOOKEEPER_PATH, "127.0.0.1:2181");
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
