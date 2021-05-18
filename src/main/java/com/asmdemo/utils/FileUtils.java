package com.asmdemo.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    public static String saveFile(String name, byte[] bytes) {
        FileOutputStream stream = null;
        try {
            File file = new File(name);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            stream = new FileOutputStream(file);
            stream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "文件路径：" + name;
    }
}
