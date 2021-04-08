package rpc.zengfk.invoker;

import rpc.zengfk.annotation.SPI;
import rpc.zengfk.model.RpcRequest;

/**
 * 用于服务端RequestHandler调用所暴露的服务
 * @author zeng.fk
 * 2021-04-07 19:50
 */
@SPI
public interface Invoker {

     Object invoke(RpcRequest req, Object serviceBean);

}
