package com.xz.partnerbackend;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @Author: 阿庆
 * @Date: 2024/3/6 17:40
 * @ToDo:
 */

public class TestAbc {

    @Test
    void test1() {
        int[] a = {10, 6, 4, 2, 9, 6, 4, 8, 2, 3};
        for (int i = 0; i < a.length - 1; i++) {
            for (int j = 0; j < a.length -1 - i; j++) {
                if (a[j] > a[j + 1]) {
                    int t = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = t;
                }
            }
        }

        System.out.println(Arrays.toString(a));
    }
}
