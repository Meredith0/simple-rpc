package rpc.simple.annotation.loader;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import rpc.simple.annotation.*;
import rpc.simple.cache.system.FailStrategyCache;
import rpc.simple.cache.system.FilterCache;
import rpc.simple.filter.Filter;
import rpc.simple.model.Service;
import rpc.simple.model.Tag;
import rpc.simple.provider.ServiceProvider;
import rpc.simple.proxy.RpcRequestProxy;
import rpc.simple.remoting.transport.RpcTransport;
import rpc.simple.support.FailTolerate;
import rpc.simple.support.strategy.FailMock;

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

        if (bean.getClass().isAnnotationPresent(RpcFilter.class) && bean instanceof Filter) {
            registerFilter((Filter<?, ?>) bean);
        }

        if (bean.getClass().isAnnotationPresent(FailStrategy.class)) {
            registerFailStrategy(bean);
        }

        if (bean.getClass().isAnnotationPresent(MockService.class)) {
            registerMockService(bean);
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
        if (bean instanceof FailTolerate) {
            FailStrategyCache.put(strategyClass, (FailTolerate) bean);
        }
    }

    private void registerMockService(Object bean) {
        MockService annotation = bean.getClass().getAnnotation(MockService.class);
        String serviceName = annotation.name();
        if (StringUtils.isEmpty(serviceName)) {
            throw new IllegalArgumentException("service name cannot be empty");
        }
        provider.provideLocally(bean, serviceName);
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

        FailStrategyCache.put(service, annotation.failStrategy());
        //动态代理
        RpcRequestProxy proxy = new RpcRequestProxy(transport, service);
        return proxy.newProxyInstance(field.getType());
    }
}