package com.xiaoyaozi;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * tip: 服务端启动
 *
 * @author xiaoyaozi
 * createTime: 2021-03-19 17:36
 */
@SpringBootApplication
public class ImServerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ImServerApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {
        Thread.currentThread().join();
    }
}
