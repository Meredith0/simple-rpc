package rpc.simple.extension;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import rpc.simple.annotation.SPI;

/**
 * 扩展类加载器, 参考了dubbo, 提供了对@SPI的接口的自定义实现类的加载
 * eg: LoadBalance接口, 本框架仅实现了一致性哈希和随机
 * 如果用户需要其他负载均衡算法, 可以自行实现LoadBalance, 然后在META-INF/extensions/下添加
 * 文件名:rpc.zengfk.loadBalance.LoadBalance
 * 内容:${ExtensionNameEnum.xxx.getName}=xxx.xxx.xxx.LoadBalanceImpl
 * @author zeng.fk
 *     2021-04-04 22:31
 */
@Slf4j
public final class ExtensionLoader<T> {

    public static final String EXTENSION_DIR = "META-INF/extensions/";
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();

    private final Class<?> type;
    private final Map<String, Holder<Object>> INSTANCE_CACHE = new ConcurrentHashMap<>();
    private final Holder<Map<String, Class<?>>> CLASS_CACHE = new Holder<>();

    private ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    public static <T> ExtensionLoader<T> ofType(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Extension type should not be null.");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type (" + type + ") should be an interface!");
        }
        if (type.getAnnotation(SPI.class) == null) {
            throw new IllegalArgumentException("Extension type (" + type + ") should be annotated with @SPI");
        }
        //先从缓存中拿
        ExtensionLoader<T> extensionLoader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        //没有才创建
        if (extensionLoader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<T>(type));
            extensionLoader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        }
        return extensionLoader;
    }

    /**
     * 获取扩展类实例
     * @param name extension name
     * @return object of extension type
     */
    @SuppressWarnings("unchecked")
    public T getExtension(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Extension name should not be empty!");
        }
        //先从缓存中拿
        Holder<Object> holder = INSTANCE_CACHE.get(name);
        if (holder == null) {
            INSTANCE_CACHE.putIfAbsent(name, new Holder<>());
            holder = INSTANCE_CACHE.get(name);
        }
        //单例
        Object instance = holder.get();
        if (instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return (T) instance;
    }

    @SuppressWarnings("unchecked")
    private T createExtension(String name) {
        //加载该接口下的所有实现
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
            throw new RuntimeException("No such extension of name " + name);
        }
        T instance = (T) EXTENSION_INSTANCES.get(clazz);
        if (instance == null) {
            try {
                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return instance;
    }

    private Map<String, Class<?>> getExtensionClasses() {
        //先从缓存拿
        Map<String, Class<?>> classes = CLASS_CACHE.get();

        if (classes == null) {
            synchronized (CLASS_CACHE) {
                classes = CLASS_CACHE.get();
                if (classes == null) {
                    classes = new HashMap<>();
                    //读配置文件, 加载类
                    loadDirectory(classes);
                    CLASS_CACHE.set(classes);
                }
            }
        }
        return classes;
    }

    private void loadDirectory(Map<String, Class<?>> extensionClasses) {
        String fileName = EXTENSION_DIR + type.getName();
        try {
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            Enumeration<URL> urls = classLoader.getResources(fileName);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL resourceUrl = urls.nextElement();
                    loadResource(extensionClasses, classLoader, resourceUrl);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, URL resourceUrl) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream(), UTF_8))) {
            String line;
            // 读配置文件的每一行
            while ((line = reader.readLine()) != null) {
                // 忽略注释后面的东西
                final int ci = line.indexOf('#');
                if (ci >= 0) {
                    line = line.substring(0, ci);
                }

                line = line.trim();
                if (line.length() > 0) {
                    try {
                        final int ei = line.indexOf('=');
                        String name = line.substring(0, ei).trim();
                        String clazzName = line.substring(ei + 1).trim();
                        // 读取配置 key: extension name, value: className
                        if (name.length() > 0 && clazzName.length() > 0) {
                            Class<?> clazz = classLoader.loadClass(clazzName);
                            extensionClasses.put(name, clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        log.error(e.getMessage());
                    }
                }

            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
