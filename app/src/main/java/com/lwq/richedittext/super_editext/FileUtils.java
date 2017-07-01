package com.lwq.richedittext.super_editext;

import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
//      bw.newLine();
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


    /**
     * @author chenzheng_Java
     * 保存用户输入的内容到文件
     */
    public static void save(String path, String data) {

        try {
            /* 根据用户提供的文件名，以及文件的应用模式，打开一个输出流.文件不存系统会为你创建一个的，
             * 至于为什么这个地方还有FileNotFoundException抛出，我也比较纳闷。在Context中是这样定义的
             *   public abstract FileOutputStream openFileOutput(String name, int mode)
             *   throws FileNotFoundException;
             * openFileOutput(String name, int mode);
             * 第一个参数，代表文件名称，注意这里的文件名称不能包括任何的/或者/这种分隔符，只能是文件名
             *          该文件会被保存在/data/data/应用名称/files/chenzheng_java.txt
             * 第二个参数，代表文件的操作模式
             *          MODE_PRIVATE 私有（只能创建它的应用访问） 重复写入时会文件覆盖
             *          MODE_APPEND  私有   重复写入时会在文件的末尾进行追加，而不是覆盖掉原来的文件
             *          MODE_WORLD_READABLE 公用  可读
             *          MODE_WORLD_WRITEABLE 公用 可读写
             *  */
            FileOutputStream outputStream = new FileOutputStream(path);
            outputStream.write(data.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @author chenzheng_java
     * 读取刚才用户保存的内容
     */
    public static String read(String path) {
        String data = "";
        try {
            FileInputStream inputStream = new FileInputStream(path);
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            while (inputStream.read(bytes) != -1) {
                arrayOutputStream.write(bytes, 0, bytes.length);
            }
            inputStream.close();
            arrayOutputStream.close();
            data = new String(arrayOutputStream.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;

    }
}
