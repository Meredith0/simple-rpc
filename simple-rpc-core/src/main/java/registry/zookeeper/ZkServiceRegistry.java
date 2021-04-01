package registry.zookeeper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import model.ServiceInstance;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import registry.ServiceRegistry;
import utils.ZookeeperUtil;

/**
 * @author zeng.fk
 *     2021-03-30 15:10
 */
@Slf4j
public class ZkServiceRegistry implements ServiceRegistry {
    public static final String PATH_PREFIX = "/simple-rpc/provider/";

    /**
     * 注册服务
     *  /simple-rpc/provider/{service}#{version}#{host}:{port}
     *  前2段为providerPath, 系持久节点, 后面为临时节点
     */
    @Override
    @SneakyThrows
    public void register(ServiceInstance serviceInstance) {

        String providerPath = PATH_PREFIX + serviceInstance;
        CuratorFramework zkClient = ZookeeperUtil.getZkClient();
        //检查服务路径是否存在
        Stat stat = zkClient.checkExists().forPath(providerPath);
        //不存在则创建
        if (stat == null) {
            zkClient.create().withMode(CreateMode.PERSISTENT).forPath(providerPath);
        }

        //注册临时节点
        String fullPath = providerPath + "/" + serviceInstance.toServicePath();
        zkClient.create().withMode(CreateMode.EPHEMERAL).forPath(fullPath);
        log.info("服务注册成功, servicePath:{}", fullPath);

    }

    /**
     * 取消注册服务, 优雅停机时调用
     */
    @Override
    public void unRegister(ServiceInstance serviceInstance) {
        //删除该ip下的所有服务
        ZookeeperUtil.deleteNodeEndWith(serviceInstance.toServicePath());
    }

}
