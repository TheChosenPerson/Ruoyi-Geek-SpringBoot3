package com.ruoyi.auth.common.utils;

import java.util.Random;

public class RandomCodeUtil {

    public static String randomString(String characters, int length) {
        StringBuilder result = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            result.append(characters.charAt(index));
        }

        return result.toString();
    }

    public static String numberCode(int length) {
        String characters = "0123456789";
        return randomString(characters, length);
    }

    public static String code(int length) {
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        return randomString(characters, length);
    }
}
