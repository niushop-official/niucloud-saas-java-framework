package com.niu.boot.utils;

import java.util.concurrent.TimeUnit;

public class ThreadUtils {

    /**
     * @param second
     */
    public static void sleepSeconds(int second) {
        try {
            TimeUnit.SECONDS.sleep(second);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
