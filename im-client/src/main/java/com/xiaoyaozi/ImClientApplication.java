package com.xiaoyaozi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * tip: 客户端启动
 *
 * @author xiaoyaozi
 * createTime: 2021-03-20 23:38
 */
@Slf4j
@SpringBootApplication
public class ImClientApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ImClientApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {
        Thread.currentThread().join();
    }
}
