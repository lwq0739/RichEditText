package com.lwq.richedittext.super_editext.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * User:lwq
 * Date:2017-06-27
 * Time:17:23
 * introduction:
 */
public class FileUtils {

    public static String getSDPath() {
        File sdDir;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取根目录
            return sdDir.getAbsolutePath();
        } else {
            return "";
        }
    }

    public static String createSDNewFile(String path, String fileName) throws IOException {
        //获取SD卡路径
        String sdDir = getSDPath();
        if (TextUtils.isEmpty(sdDir)) {
            return null;
        }
        File dirFile = new File(sdDir + File.separator + path);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

        File dataFile = new File(dirFile + File.separator + fileName);
        if (!dataFile.exists()) {
            return dataFile.createNewFile() ? dataFile.getAbsolutePath() : null;
        } else {
            return dataFile.getAbsolutePath();
        }
    }

    public static File[] getSDFilesPath(String path) {
        //获取SD卡路径
        String sdDir = getSDPath();
        if (TextUtils.isEmpty(sdDir)) {
            return null;
        }
        File dirFile = new File(sdDir + File.separator + path);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

        return dirFile.listFiles();


    }


    //向已创建的文件中写入数据
    public static void print(String path, String str) throws IOException {
        FileWriter fw = null;
        BufferedWriter bw = null;
        fw = new FileWriter(path);//
        // 创建FileWriter对象，用来写入字符流
        bw = new BufferedWriter(fw); // 将缓冲对文件的输出

        bw.write(str); // 写入文件
        bw.flush(); // 刷新该流的缓冲
        bw.close();
        fw.close();
    }

    public static String readSDFile(String fileName) throws IOException {
        FileInputStream fis;
        String s = "";
        //打开文件并得到InputStream对象
        fis = new FileInputStream(fileName);
        //available()返回估算需要的空间长度
        byte[] buffer = new byte[fis.available()];
        //把数据流的内容写入buffer中
        fis.read(buffer);
        s = new String(buffer);
        fis.close();

        return s;
    }
}
