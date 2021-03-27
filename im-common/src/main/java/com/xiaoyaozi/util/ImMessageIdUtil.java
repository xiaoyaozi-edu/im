package com.xiaoyaozi.util;

import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicLong;

/**
 * tip: 消息id生成器
 *
 * @author xiaoyaozi
 * createTime: 2021-03-27 15:10
 */
public class ImMessageIdUtil {

    /**
     * 末尾序列数
     */
    private static final AtomicLong SEQUENCE = new AtomicLong(0L);
    /**
     * 格式化时间，含毫秒
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSS");
//    private static final int SCALE = 64;
    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz<>";

    /**
     * 取模用 2~12 - 1
     */
    private static final int SEQUENCE_MASK = ((1 << 12) - 1);
    /**
     * 取模用 2~6 - 1
     */
    private static final int FORMAT_MASK = ((1 << 6) - 1);

    public static String generate(long userId) {
        StringBuilder buffer = new StringBuilder();
        // long类型至多64bit，循环10次
        for (int i = 0; i < 10; i++) {
            buffer.append(CHARS.charAt(Math.toIntExact(userId & FORMAT_MASK)));
            userId = userId >> 6;
        }
        long timestamp = System.currentTimeMillis();
        System.out.println(timestamp);
        timestamp = (timestamp << 12) | (SEQUENCE.getAndIncrement() & SEQUENCE_MASK);
        timestamp = (timestamp << 2) | 1;
        // 左移4位，接受userId多出的4位
        timestamp = (timestamp << 4) | userId;
        for (int i = 0; i < 10; i++) {
            buffer.append(CHARS.charAt(Math.toIntExact(timestamp & FORMAT_MASK)));
            timestamp = timestamp >> 6;
        }
        System.out.println(buffer.reverse().toString());
        return "";
    }

    public static void main(String[] args) {
        generate(1355433084287389697L);
    }
}
