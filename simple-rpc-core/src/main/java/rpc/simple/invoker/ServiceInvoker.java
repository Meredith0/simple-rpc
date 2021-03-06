package rpc.simple.invoker;

import lombok.extern.slf4j.Slf4j;
import rpc.simple.exception.BusinessException;
import rpc.simple.exception.RpcException;
import rpc.simple.cache.system.FilterCache;
import rpc.simple.filter.FilterChain;
import rpc.simple.filter.lifecycle.ServerInvokedFilter;
import rpc.simple.model.RpcRequest;
import rpc.simple.provider.ServiceProvider;
import rpc.simple.utils.SpringContextUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author zeng.fk
 * 2021-04-07 19:02
 */
@Slf4j
public class ServiceInvoker implements Invoker {

    public static volatile ServiceInvoker instance;

    private ServiceInvoker() {
    }

    public static ServiceInvoker getInstance() {
        if (instance == null) {
            synchronized (ServiceInvoker.class) {
                if (instance == null) {
                    instance = new ServiceInvoker();
                }
            }
        }
        return instance;
    }

    public Object accept(RpcRequest request) {
        ServiceProvider serviceProvider = SpringContextUtil.getBean(ServiceProvider.class);
        Object serviceBean = serviceProvider.get(request.getService());
        return invoke(request, serviceBean);
    }

    @Override
    public Object invoke(RpcRequest req, Object serviceBean) {
        Object res;
        try {
            Method method = serviceBean.getClass().getMethod(req.getMethodName(), req.getParamTypes());
            res = method.invoke(serviceBean, req.getParameters());
            //过滤器
            FilterChain chain = FilterCache.get(ServerInvokedFilter.class);
            chain.invokeChain(req, serviceBean);

            log.debug("成功调用serviceBean! {}", serviceBean);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            if (e.getCause() instanceof BusinessException) {
                throw new BusinessException(e.getCause());
            } else {
                throw new RpcException(e.getMessage(), e);
            }
        }
        return res;
    }
}
