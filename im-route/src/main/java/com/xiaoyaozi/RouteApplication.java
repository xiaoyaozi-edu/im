package com.xiaoyaozi;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.charset.StandardCharsets;

/**
 * tip: 路由
 *
 * @author xiaoyaozi
 * createTime: 2021-03-22 16:00
 */
@SpringBootApplication
public class RouteApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(RouteApplication.class);
    }

    @Autowired
    private CuratorFramework client;

    @Override
    public void run(String... args) throws Exception {
        for (int i = 10; i > 0; i--) {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/xxxx/" + i, String.valueOf(i).getBytes(StandardCharsets.UTF_8));
        }
        System.out.println("jiessldjalfksad");
    }
}
