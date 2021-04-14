package rpc.zengfk.annotation.loader;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import rpc.zengfk.annotation.FailStrategy;
import rpc.zengfk.annotation.RpcFilter;
import rpc.zengfk.annotation.RpcReference;
import rpc.zengfk.annotation.RpcService;
import rpc.zengfk.filter.Filter;
import rpc.zengfk.filter.FilterCache;
import rpc.zengfk.model.Service;
import rpc.zengfk.provider.ServiceProvider;
import rpc.zengfk.proxy.RpcRequestProxy;
import rpc.zengfk.remoting.transport.RpcTransport;
import rpc.zengfk.router.tag.model.Tag;
import rpc.zengfk.support.FailStrategyCache;

import java.lang.reflect.Field;

/**
 * 加载注解，在bean初始化完成后
 *
 * @author zeng.fk
 * 2021-04-04 11:27
 */
@Slf4j
@Component
public class AnnotationLoader implements BeanPostProcessor {

    @Autowired
    private ServiceProvider provider;
    @Autowired
    private RpcTransport transport;


    @SneakyThrows
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            registerService(bean);
        }

        else if (bean.getClass().isAnnotationPresent(RpcFilter.class) && bean instanceof Filter) {
            registerFilter((Filter<?,?>) bean);
        }

        else if (bean.getClass().isAnnotationPresent(FailStrategy.class)) {
            registerFailStrategy(bean);
        }

        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(RpcReference.class)) {
                Object proxy = getProxy(field);
                field.setAccessible(true);
                field.set(bean, proxy);
            }
        }
        return bean;
    }

    private void registerFailStrategy(Object bean) {
        Class<?> strategyClass = bean.getClass();
        if (bean instanceof FailStrategy) {
            FailStrategyCache.add(strategyClass, (rpc.zengfk.support.FailStrategy) bean);
        }
    }

    private void registerFilter(Filter<?, ?> bean) {
        Class<?> lifecycleFilterClass = bean.getClass().getSuperclass();
        RpcFilter annotation = bean.getClass().getAnnotation(RpcFilter.class);
        int priority = annotation.priority();
        FilterCache.add(lifecycleFilterClass, bean, priority);
    }

    @SneakyThrows
    private void registerService(Object bean) {
        RpcService annotation = bean.getClass().getAnnotation(RpcService.class);
        String serviceName = annotation.name();
        if (StringUtils.isEmpty(serviceName)) {
            serviceName = bean.getClass().getName();
        }
        provider.publish(bean, serviceName, annotation.version(), annotation.tag());
    }

    @SneakyThrows
    private Object getProxy(Field field) {
        RpcReference annotation = field.getAnnotation(RpcReference.class);

        Service service = new Service(annotation.name(), annotation.version(), new Tag(annotation.tag()));

        //动态代理
        RpcRequestProxy proxy = new RpcRequestProxy(transport, service);
        return proxy.newProxyInstance(field.getType());
    }
}
