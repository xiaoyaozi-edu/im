package com.xiaoyaozi.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
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
     * 32位编码，移除 0，1，I，O
     */
    private static final List<Character> CHARS = Arrays.asList('2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
                                                               'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P',
                                                               'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z');
    /**
     * 取模用 2~13 - 1
     */
    private static final int SEQUENCE_MASK = ((1 << 13) - 1);
    /**
     * 取模用 2~5 - 1
     */
    private static final int FORMAT_MASK = ((1 << 5) - 1);

    /**
     * tip: 消息Id生成规则，64 userId + 42 timestamp + 13 sequence 最终5bit一次转成32进制字符串
     *
     * @param userId userId
     * @return String message_id，长度定为25位
     * @author xiaoyaozi
     * createTime: 2021-03-29 16:18
     */
    public static String generate(long userId) {
        StringBuilder buffer = new StringBuilder();
        // 42 timestamp + 13 sequence
        long timestamp = System.currentTimeMillis();
        timestamp = (timestamp << 13) | (SEQUENCE.getAndIncrement() & SEQUENCE_MASK);
        for (int i = 0; i < 11; i++) {
            buffer.append(CHARS.get(Math.toIntExact(timestamp & FORMAT_MASK)));
            timestamp = timestamp >> 5;
        }
        buffer.append('-');
        // 64 userId
        for (int i = 0; i < 13; i++) {
            buffer.append(CHARS.get(Math.toIntExact(userId & FORMAT_MASK)));
            userId = userId >> 5;
        }
        return buffer.reverse().toString();
    }

    public static void main(String[] args) {
        System.out.println(generate(1355433084287389697L));
    }
}
