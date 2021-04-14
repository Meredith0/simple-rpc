package rpc.simple.remoting.compression;

import rpc.simple.annotation.SPI;

/**
 * @author zeng.fk
 * 2021-04-06 20:04
 */
@SPI
public interface Compressor {
    byte[] compress(byte[] bytes);


    byte[] decompress(byte[] bytes);
}
