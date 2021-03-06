package rpc.simple.remoting.transport.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import rpc.simple.enums.CompressorEnum;
import rpc.simple.enums.SerializerEnum;
import rpc.simple.extension.ExtensionLoader;
import rpc.simple.protocol.RpcProtocol;
import rpc.simple.remoting.compression.Compressor;
import rpc.simple.serialize.Serializer;
import rpc.simple.utils.SnowFlakeUtil;

/**
 * @author zeng.fk
 * 2021-04-06 20:36
 */
@Slf4j
public class ProtocolEncoder extends MessageToByteEncoder<RpcProtocol> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProtocol protocol, ByteBuf out) {
        log.debug("encoding protocol... {}", protocol);
        try {
            long rpcSeqNo = SnowFlakeUtil.nextId();
            if (protocol.getTraceId() == 0) {
                protocol.setTraceId(rpcSeqNo);
            }

            out.writeBytes(RpcProtocol.MAGIC_CODE);
            out.writeByte(RpcProtocol.VERSION);
            //空出 4 Byte 给 length 段
            out.writerIndex(out.writerIndex() + 4);
            byte type = protocol.getType();
            out.writeByte(type);
            out.writeByte(protocol.getCompressor());
            out.writeByte(protocol.getSerializer());
            out.writeByte(protocol.getFailStrategy());
            out.writeLong(protocol.getTraceId());

            byte[] body;
            int length = RpcProtocol.HEADER_LENGTH;
            // 非心跳请求才有 body
            if (type != RpcProtocol.TYPE_HEARTBEAT_PING && type != RpcProtocol.TYPE_HEARTBEAT_PONG) {
                String serializerName = SerializerEnum.getName(protocol.getSerializer());
                log.debug("using serializer: {}", serializerName);
                Serializer serializer = ExtensionLoader.ofType(Serializer.class).getExtension(serializerName);
                body = serializer.serialize(protocol.getBody());

                String compressorName = CompressorEnum.getName(protocol.getCompressor());
                log.debug("using compressor: {}", compressorName);
                Compressor compressor = ExtensionLoader.ofType(Compressor.class).getExtension(compressorName);
                body = compressor.compress(body);

                length += body.length;
                //写 body
                out.writeBytes(body);
            }

            //记录 body 段的末尾
            int bodyIndex = out.writerIndex();
            //回到 length 段写 length
            out.writerIndex(bodyIndex - length + RpcProtocol.MAGIC_CODE.length + 1);
            out.writeInt(length);
            //回到 body 段末尾
            out.writerIndex(bodyIndex);
            log.debug("protocol encoded! {}", protocol);
        } catch (Exception e) {
            log.error("error occurs when encoding protocol!", e);
        }
    }
}
