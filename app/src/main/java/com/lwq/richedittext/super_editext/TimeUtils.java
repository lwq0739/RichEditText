package com.lwq.richedittext.super_editext;

/**
 * User:lwq
 * Date:2017-06-27
 * Time:18:28
 * introduction:
 */
public class TimeUtils {


    public static String getTime() {

        long time = System.currentTimeMillis() / 1000;//获取系统时间的10位的时间戳

        String str = String.valueOf(time);

        return str;

    }
}
