package rpc.zengfk.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author zeng.fk
 * 2021-03-30 15:12
 */
@Slf4j
@Configuration
public class ZookeeperUtil {

    private static final int RETRY_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;
    private static CuratorFramework zkClient;
    private static final String zookeeperUrl;

    static {
        zookeeperUrl = PropertiesUtil.getZkUrl();
    }

    public static CuratorFramework getZkClient() {

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

}
