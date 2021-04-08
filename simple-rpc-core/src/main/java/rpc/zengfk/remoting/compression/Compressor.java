package rpc.zengfk.remoting.compression;

import lombok.extern.slf4j.Slf4j;
import rpc.zengfk.annotation.SPI;

/**
 * @author zeng.fk
 * 2021-04-06 20:04
 */
@SPI
public interface Compressor {
    byte[] compress(byte[] bytes);


    byte[] decompress(byte[] bytes);
}
