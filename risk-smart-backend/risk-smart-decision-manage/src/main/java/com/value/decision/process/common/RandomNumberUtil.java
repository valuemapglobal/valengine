package com.value.decision.process.common;

import java.util.Random;

public class RandomNumberUtil {
    private RandomNumberUtil() {
    }
    public static String createRandomNumber(int length) {
        StringBuilder strBuffer = new StringBuilder();
        Random rd = new Random();
        for (int i = 0; i < length; i++) {
            strBuffer.append(rd.nextInt(10));
        }
        return strBuffer.toString();
    }
}

