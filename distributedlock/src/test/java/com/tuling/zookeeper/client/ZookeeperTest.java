package com.tuling.zookeeper.client;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZookeeperTest {

    private static final String ZK_ADDRESS= "192.168.109.200:2181";

    private static final int SESSION_TIME_OUT=5000;

    private static final CountDownLatch countDownLatch = new CountDownLatch(1);

    private static final Watcher defaultWatch = event -> {
          if (event.getType()== Watcher.Event.EventType.None
                  && event.getState()== Watcher.Event.KeeperState.SyncConnected){
              countDownLatch.countDown();
          }
    };
    @Test
    public void sessionIdTest() throws IOException, InterruptedException {

        ZooKeeper zooKeeper=new ZooKeeper(ZK_ADDRESS,SESSION_TIME_OUT,defaultWatch);
        countDownLatch.await();



    }
}
