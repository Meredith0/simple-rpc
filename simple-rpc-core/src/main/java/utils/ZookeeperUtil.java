package utils;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.stereotype.Component;
import registry.zookeeper.ZkServiceRegistry;

/**
 * @author zeng.fk
 *     2021-03-30 15:12
 */
@Slf4j
@Component
public final class ZookeeperUtil {

    private static final int RETRY_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;
    private static CuratorFramework zkClient;
    private static String zookeeperUrl = "127.0.0.1:2181";

    // todo 从配置文件中获取
    // @Value("${rpc.zookeeper.url}")
    // public void setZookeeperUrl(String zookeeperUrl) {
    //     ZookeeperUtil.zookeeperUrl = zookeeperUrl;
    // }

    public static CuratorFramework getZkClient() {
        // check if user has set zk address
        // Properties properties = PropertiesFileUtil.readPropertiesFile(RpcConfigEnum.RPC_CONFIG_PATH
        // .getPropertyValue());
        // String zookeeperAddress = properties != null && properties.getProperty(RpcConfigEnum.ZK_ADDRESS
        // .getPropertyValue()) != null ? properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyValue()) :
        // DEFAULT_ZOOKEEPER_ADDRESS;
        // if zkClient has been started, return directly
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }
        //单例
        synchronized (ZookeeperUtil.class) {
            if (zkClient == null || zkClient.getState() == CuratorFrameworkState.STARTED) {
                //
                RetryPolicy retryPolicy = new ExponentialBackoffRetry(RETRY_SLEEP_TIME, MAX_RETRIES);
                zkClient = CuratorFrameworkFactory.builder()
                                                  .connectString(zookeeperUrl)
                                                  .retryPolicy(retryPolicy)
                                                  .build();
                zkClient.start();
                try {
                    if (!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)) {
                        throw new RuntimeException("Time out waiting to connect to zookeeper! url:" + zookeeperUrl);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return zkClient;
        }
    }

    /**
     * 创建临时节点
     */
    public static void createNode(String servicePath,boolean isPersistent) {
        CuratorFramework zkClient = getZkClient();
        //eg: /simple-rpc/github.meredith0.HelloService/127.0.0.1:8001
        String path = ZkServiceRegistry.PATH_PREFIX + servicePath;
        try {
            CreateMode createMode = isPersistent ? CreateMode.PERSISTENT : CreateMode.EPHEMERAL;
            zkClient.create().creatingParentsIfNeeded().withMode(createMode).forPath(path);
            log.info("zookeeper创建临时节点成功, node:{}", path);
        } catch (Exception e) {
            log.error("zookeeper创建临时节点失败, path:{}", path);
        }
    }

}
