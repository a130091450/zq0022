package utils;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.Properties;

public class SettingCenter extends PropertyPlaceholderConfigurer implements ApplicationContextAware {

    private AbstractApplicationContext applicationContext;
    Boolean flag = true;

    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
        loadFromZK(props);
        if (flag){
            addWatch();
            flag = false;
        }
        super.processProperties(beanFactoryToProcess,props);
    }

    private void addWatch(){
// 重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3,3000);
        // 创建客户端
        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", 3000, 3000, retryPolicy);
        // 启动
        client.start();

        TreeCache treeCache = new TreeCache(client, "/config");
        try {
            treeCache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                if(event.getType() == TreeCacheEvent.Type.NODE_UPDATED){
                    String path = event.getData().getPath();
                    System.out.println(path + "节点修改");
                    if(path.startsWith("/config/jdbc")){
                        // 修改了数据库配置
                        // 刷新spring容器
                        applicationContext.refresh();
                    }

                }
            }
        });
    }

    private void loadFromZK(Properties props){
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000,3,3000);
        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", 3000, 3000, retryPolicy);
        // 启动客户端
        client.start();
        try {
            String username = new String(client.getData().forPath("/config/jdbc.username"));
            String password = new String(client.getData().forPath("/config/jdbc.password"));
            String url = new String(client.getData().forPath("/config/jdbc.url"));
            String driverClassName = new String(client.getData().forPath("/config/jdbc.driver"));

            props.setProperty("jdbc.username",username);
            props.setProperty("jdbc.password",password);
            props.setProperty("jdbc.url",url);
            props.setProperty("jdbc.driver",driverClassName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.close();

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (AbstractApplicationContext)applicationContext;
    }
}
