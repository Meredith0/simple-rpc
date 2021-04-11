package rpc.zengfk.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * rpc协议
 * 0   1   2   3   4       5   6   7   8   9    10        11    12   13   14   15   16
 * +---+---+---+---+-------+---+---+---+---+----+---------+-----+----+----+----+----+    header
 * |   magic code  |version|     length    |type|compress|serial|       trace id  ---
 * --- trace id    |sup-span id|span id|
 * +---------------+-------+---------------+----+--------+------+-------------------+    body
 * |                                                                                |
 * |                               data                                             |
 * |                             ... ...                                            |
 * |                             ... ...                                            |
 * +--------------------------------------------------------------------------------+
 * header
 * 4Byte  magic code（魔法数: srpc）,  1Byte ver（版本: 1）, 4Byte length（消息长度）, 1Byte type（消息类型),
 * 1Byte compress（压缩类型）, 1Byte serial（序列化类型）, 8Byte trace id（跟踪号）, 2Byte sup-span id(父span id)
 * 2Byte span id(表示一次请求来回)
 *
 * body
 * data（object）
 *
 * @author zeng.fk
 * 2021-04-06 20:33
 * @see <a href="https://zhuanlan.zhihu.com/p/95621344">LengthFieldBasedFrameDecoder解码器</a>
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcProtocol {

    public static final byte[] MAGIC_CODE = {(byte) 's', (byte) 'r', (byte) 'p', (byte) 'c'};
    public static final byte VERSION = 0x01;
    public static final int HEADER_LENGTH = 16;
    public static final byte TYPE_REQ = 0x01;
    public static final byte TYPE_RESP = 0x02;
    public static final byte TYPE_HEARTBEAT_PING = 0x03;
    public static final byte TYPE_HEARTBEAT_PONG = 0x04;
    public static final byte COMPRESSION_GZIP = 0x01;
    public static final byte SERIALIZER_PROTOSTUFF = 0x01;
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;
    public static final int FRAME_LENGTH = 16;
    private byte type;
    private byte serializer;
    private byte compressor;
    //FIXME requestId 暂未启用, 暂以 data 中的 uuid 代替
    private int seqNo;
    //request data
    private Object data;

    public boolean isHeartbeat() {
        return type == TYPE_HEARTBEAT_PING || type == TYPE_HEARTBEAT_PONG;
    }

    public void answerHeartbeat() {
        if (isHeartbeat()) {
            if (type == TYPE_HEARTBEAT_PING) {
                type = TYPE_HEARTBEAT_PONG;
            }
            else {
                type = TYPE_HEARTBEAT_PING;
            }
        }
    }
}
