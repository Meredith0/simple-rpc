package rpc.simple.remoting.transport.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import rpc.simple.enums.CompressorEnum;
import rpc.simple.enums.SerializerEnum;
import rpc.simple.exception.RpcException;
import rpc.simple.extension.ExtensionLoader;
import rpc.simple.model.RpcRequest;
import rpc.simple.model.RpcResponse;
import rpc.simple.protocol.RpcProtocol;
import rpc.simple.remoting.compression.Compressor;
import rpc.simple.serialize.Serializer;

import java.util.Arrays;

/**
 * rpc协议
 * 0   1   2   3   4       5   6   7   8   9    10        11    12   13   14   15   16
 * +---+---+---+---+-------+---+---+---+---+----+---------+-----+----+----+----+----+
 * |   magic code  |version|     length    |type|compress|serial|        seqNo      |
 * +---------------+-------+---------------+----+--------+------+-------------------+
 * |                                                                                |
 * |                                body                                            |
 * |                              ... ...                                           |
 * |                              ... ...                                           |
 * +--------------------------------------------------------------------------------+
 * 4B  magic code（魔法数: srpc）   1B ver（版本: 1）   4B length（消息长度）    1B type（消息类型）
 * 1B compress（压缩类型） 1B serial（序列化类型）    4B  seqNo（rpc请求序列号）
 * body（object类型数据）
 *
 * @author zeng.fk
 * 2021-04-06 19:08
 * @see <a href="https://zhuanlan.zhihu.com/p/95621344">LengthFieldBasedFrameDecoder解码器</a>
 */
@Slf4j
public class ProtocolDecoder extends LengthFieldBasedFrameDecoder {

    public ProtocolDecoder() {
        /*
          lengthFieldOffset: 魔法数: 'srpc' 占 4B, 版本号: 1 占 1B, 后面就是 length 段, 所以是 5
          lengthFieldLength: length 段长度是 4
          lengthAdjustment: length 段前面有 9B, 所以是 -9
          initialBytesToStrip: 后面会手动检查 magic code 和 version, 不需要 strip header, 所以是 0
         */
        this(RpcProtocol.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    public ProtocolDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                           int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        log.debug("protocol decoding... ctx:{}, ByteBuf:{}",ctx,in);
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decoded;
            if (frame.readableBytes() >= RpcProtocol.FRAME_LENGTH) {
                try {
                    log.debug("decoding frame...  frame:{}",frame);
                    return decodeFrame(frame);
                } catch (Exception e) {
                    log.error("Decode frame error!", e);
                    throw e;
                } finally {
                    frame.release();
                }
            }
        }
        log.debug("protocol decoded!  protocol: {}",decoded);
        return decoded;
    }

    private Object decodeFrame(ByteBuf in) {
        checkMagicCode(in);
        checkVersion(in);
        //读入ByteBuf, 并转换成RpcProtocol
        int length = in.readInt();
        byte type = in.readByte();
        byte compressCode = in.readByte();
        byte serializeCode = in.readByte();
        byte failStrategy = in.readByte();
        long seqNo = in.readLong();
        RpcProtocol protocol = RpcProtocol.builder()
            .type(type)
            .serializer(serializeCode)
            .compressor(compressCode)
            .failStrategy(failStrategy)
            .seqNo(seqNo).build();

        //是心跳包就直接返回
        if (type == RpcProtocol.TYPE_HEARTBEAT_PING) {
            protocol.setData(RpcProtocol.TYPE_HEARTBEAT_PONG);
            return protocol;
        }
        if (type == RpcProtocol.TYPE_HEARTBEAT_PONG) {
            protocol.setData(RpcProtocol.TYPE_HEARTBEAT_PING);
            return protocol;
        }
        //解析body
        int bodyLength = length - RpcProtocol.HEADER_LENGTH;
        if (bodyLength > 0) {
            byte[] bs = new byte[bodyLength];
            in.readBytes(bs);
            //解压缩
            String compressorName = CompressorEnum.getName(compressCode);
            Compressor compressor = ExtensionLoader.ofType(Compressor.class).getExtension(compressorName);
            bs = compressor.decompress(bs);
            //反序列化
            String serializerName = SerializerEnum.getName(protocol.getSerializer());
            Serializer serializer = ExtensionLoader.ofType(Serializer.class).getExtension(serializerName);
            //判断请求类型
            if (type == RpcProtocol.TYPE_REQ) {
                protocol.setData(serializer.deserialize(bs, RpcRequest.class));
            }
            else if (type == RpcProtocol.TYPE_RESP) {
                protocol.setData(serializer.deserialize(bs, RpcResponse.class));
            }
            else {
                throw new RpcException("未知 RpcProtocol type, type:" + type);
            }
        }
        return protocol;
    }

    private void checkVersion(ByteBuf in) {
        byte version = in.readByte();
        if (version != RpcProtocol.VERSION) {
            throw new RpcException("未知版本号, version: " + version);
        }
    }

    private void checkMagicCode(ByteBuf in) {
        int len = RpcProtocol.MAGIC_CODE.length;
        byte[] tmp = new byte[len];
        in.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (tmp[i] != RpcProtocol.MAGIC_CODE[i]) {
                log.error("RpcProtocol.MAGIC_CODE:{} vs received MAGIC_CODE:{}", RpcProtocol.MAGIC_CODE, Arrays.toString(tmp));
                throw new RpcException("未知 MAGIC CODE");
            }
        }
    }
}
