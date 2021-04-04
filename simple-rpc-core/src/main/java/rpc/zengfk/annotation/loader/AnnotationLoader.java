package rpc.zengfk.annotation.loader;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import rpc.zengfk.annotation.RpcReference;
import rpc.zengfk.annotation.RpcService;
import rpc.zengfk.provider.ServiceProvider;

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
    ServiceProvider serviceProvider;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            registerService(bean);
        }

        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (declaredField.getAnnotation(RpcReference.class) != null) {
                proxy();
            }
        }
        return bean;
    }

    @SneakyThrows
    private void registerService(Object bean) {
        RpcService annotation = bean.getClass().getAnnotation(RpcService.class);
        String serviceName = annotation.name();
        if (StringUtils.isEmpty(serviceName)) {
            serviceName = bean.getClass().getName();
        }
        serviceProvider.publish(bean, serviceName, annotation.version());
    }

    private void proxy() {

    }
}
