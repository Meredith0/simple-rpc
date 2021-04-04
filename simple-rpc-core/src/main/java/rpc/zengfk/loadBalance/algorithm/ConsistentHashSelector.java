package rpc.zengfk.loadBalance.algorithm;
import com.esotericsoftware.minlog.Log;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 一致性哈希算法, 参考dubbo
 *
 * @author zeng.fk
 *     2021-04-02 14:41
 */
@Slf4j
public final class ConsistentHashSelector<T> {

    private final TreeMap<Long, T> virtualNode;

    @Getter
    private final int identityHashCode;

    public ConsistentHashSelector(List<T> serviceInstances, int replicaNumber, int identityHashCode) {
        this.virtualNode = new TreeMap<>();
        this.identityHashCode = identityHashCode;

        for (T s : serviceInstances) {
            for (int i = 0; i < replicaNumber / 4; i++) {
                byte[] digest = md5(s.toString() + i);
                for (int h = 0; h < 4; h++) {
                    long m = hash(digest, h);
                    virtualNode.put(m, s);
                }
            }
        }
    }

    static byte[] md5(String key) {
        MessageDigest md; try {
            md = MessageDigest.getInstance("MD5");
            byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
            md.update(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return md.digest();
    }

    static long hash(byte[] digest, int idx) {
        return ((long) (digest[3 + idx * 4] & 0xFF) << 24 | (long) (digest[2 + idx * 4] & 0xFF) << 16 |
                    (long) (digest[1 + idx * 4] & 0xFF) << 8 | (long) (digest[idx * 4] & 0xFF)) & 4294967295L;
    }

    public T select(String rpcServiceName) {
        byte[] digest = md5(rpcServiceName);
        log.debug("select hash:{}",hash(digest, 0) + "");
        return selectForKey(hash(digest, 0));
    }

    public T selectForKey(long hashCode) {
        Entry<Long, T> entry = virtualNode.tailMap(hashCode, true).firstEntry();

        if (entry == null) {
            entry = virtualNode.firstEntry();
        }

        return entry.getValue();
    }

}

