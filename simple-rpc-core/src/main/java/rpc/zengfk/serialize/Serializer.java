package rpc.zengfk.serialize;

import rpc.zengfk.annotation.SPI;

/**
 * @author zeng.fk
 * 2021-04-06 20:58
 */
@SPI
public interface Serializer {

    <T> byte[] serialize(Object obj);

    <T> T deserialize(byte[] bytes, Class<T> clazz);

}
