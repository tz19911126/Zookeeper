package zookeeper.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ConfigCenter {

    private final static  String CONNECT_STR="192.168.3.35:2181";

    private final static Integer  SESSION_TIMEOUT=30*1000;

    private static ZooKeeper zooKeeper=null;


    private static CountDownLatch countDownLatch=new CountDownLatch(1);

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {


        //异步过程，SendThread和eventThread都是守护线程， this.setDaemon(true);
        //当没有业务线程时，都会主动结束，阻塞式
        zooKeeper=new ZooKeeper(CONNECT_STR, SESSION_TIMEOUT, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                    if (event.getType()== Event.EventType.None
                            && event.getState() == Event.KeeperState.SyncConnected){
                        log.info("连接已建立");
                        countDownLatch.countDown();
                    }
            }
        });
        System.out.println("准备卡住");
        countDownLatch.await();
        System.out.println("1111");

        MyConfig myConfig = new MyConfig();
        myConfig.setKey("anykey");
        myConfig.setName("anyName");

        ObjectMapper objectMapper=new ObjectMapper();

        byte[] bytes = objectMapper.writeValueAsBytes(myConfig);
        byte[] data = zooKeeper.getData("/myconfig", null, null);
        if(data.length==0){
            String s = zooKeeper.create("/myconfig", bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }





        Watcher watcher = new Watcher() {
            @SneakyThrows
            @Override
            public void process(WatchedEvent event) {
                if (event.getType()== Event.EventType.NodeDataChanged
                        && event.getPath()!=null && event.getPath().equals("/myconfig")){
                    log.info(" PATH:{}  发生了数据变化" ,event.getPath());

                    byte[] data = zooKeeper.getData("/myconfig", this, null);

                    MyConfig newConfig = objectMapper.readValue(new String(data), MyConfig.class);

                    log.info("数据发生变化: {}",newConfig);

                }


            }
        };
        data = zooKeeper.getData("/myconfig", watcher, null);
        MyConfig originalMyConfig = objectMapper.readValue(new String(data), MyConfig.class);
        log.info("原始数据: {}", originalMyConfig);

        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }




}
