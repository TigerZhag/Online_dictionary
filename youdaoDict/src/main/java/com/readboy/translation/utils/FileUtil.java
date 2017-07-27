package com.readboy.translation.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author Zhang shixin
 * @date 16-8-3.
 */
public class FileUtil {

    public static void closeStream(Closeable stream){
        try {
            if (stream != null)
                stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
