package com.hiekn.boot.autoconfigure.base.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

/**
 * 集合一些常用的工具
 *
 * @author DingHao
 * @date 2018/12/22 13:23
 */
public abstract class McnUtils {

    private static final String FILE_PREFIX = "file:";

    private static Cache<String, Object> cache = CacheBuilder.newBuilder().build();

    public static void setCache(String key,Object value){
        cache.put(key, value);
    }

    public static String getCacheToString(String key){
        return String.valueOf(getCache(key));
    }

    public static Integer getCacheToInt(String key){
        return Integer.valueOf(getCacheToString(key));
    }

    public static Long getCacheToLong(String key){
        return Long.valueOf(getCacheToString(key));
    }

    public static Object getCache(String key){
        return cache.getIfPresent(key);
    }

    public static void clearAllCache(){
        cache.invalidateAll();
    }

    public static void clearCache(String key){
        cache.invalidate(key);
    }

    public static void clearCache(List<String> keys){
        cache.invalidateAll(keys);
    }

    /**
     * 判断字符串为null或者为空
     * @param value
     * @return null or empty is true
     */
    public static boolean isNullOrEmpty(String value){
        return Objects.isNull(value) || value.isEmpty();
    }

    /**
     *
     * @param path
     * @return
     */
    public static String parsePath(String path) {
        return path.startsWith("/") ? path : "/" + path;
    }

    /**
     * 获取文件扩展名
     * @param fileName
     * @return
     */
    public static String getExtName(String fileName){
        return fileName.substring(fileName.lastIndexOf(".")+1);
    }

    /**
     * 默认加载classpath下属性文件，如果加载文件系统的以file:开头
     * @param fileName
     * @return
     */
    public static Properties loadProperties(String fileName){
        return loadProperties(fileName,McnUtils.class.getClassLoader());
    }

    private static Properties loadProperties(String fileName,ClassLoader cls){
        Properties properties = new Properties();
        if(fileName.startsWith(FILE_PREFIX)){
            try(InputStream in = new FileInputStream(fileName.substring(FILE_PREFIX.length()))){
                properties.load(in);
            }catch (IOException e) {
                //ignore not found file
            }
        }else{
            try(InputStream in = cls.getResourceAsStream(fileName)){
                properties.load(in);
            }catch (IOException e) {
                //ignore not found file
            }
        }
        return properties;
    }

    /**
     * 获取一个uuid
     * @return
     */
    public static String randomUUID(){
        return UUID.randomUUID().toString();
    }

    /**
     * 获取一个无中划线的uuid
     * @return
     */
    public static String simpleUUID(){
        return randomUUID().replace("-","");
    }



}
