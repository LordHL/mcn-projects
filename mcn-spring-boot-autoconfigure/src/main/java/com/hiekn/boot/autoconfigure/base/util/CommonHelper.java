package com.hiekn.boot.autoconfigure.base.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class CommonHelper {

    public static String parsePath(String path) {
        return path.startsWith("/") ? path : "/" + path;
    }

    public static Properties parseProperties(String fileName){
        Properties properties = new Properties();
        InputStream in = null;
        try {
            in = CommonHelper.class.getClassLoader().getResourceAsStream(fileName);
            properties.load(in);
        } catch (IOException e) {

        }finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return properties;
    }

}
