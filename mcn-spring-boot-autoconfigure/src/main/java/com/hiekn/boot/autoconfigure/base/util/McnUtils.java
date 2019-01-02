package com.hiekn.boot.autoconfigure.base.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

/**
 * 封装一些常用的工具
 *
 * @author DingHao
 * @date 2018/12/22 13:23
 */
public abstract class McnUtils {

    private static final Logger logger = LoggerFactory.getLogger(McnUtils.class);

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
     *判断集合为null或者为空集合
     * @param value
     * @return
     */
    public static boolean isNullOrEmpty(Collection<?> value){
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
     * 默认先从文件系统加载，找不到尝试从classpath下加载
     * @param fileName
     * @return
     */
    public static Properties loadProperties(String fileName){
        return loadProperties(fileName,McnUtils.class.getClassLoader());
    }

    /**
     * 从系统环境或者系统属性获取指定key的值，前者优先级高，然后解析(如果是/结尾，则自动拼上fileName)
     * @param fileName
     * @param key
     * @return
     *
     */
    public static Properties loadProperties(String fileName,String key){
        String value = getValueFromSystemEnvOrProp(key, fileName);
        if(value.endsWith("/")){
            value += fileName;
        }
        return loadProperties(value);
    }

    private static InputStream getInputStream(String fileName,ClassLoader cls){
        InputStream in;
        try {
            in = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            in = cls.getResourceAsStream(fileName);
        }
        return in;
    }

    private static Properties loadProperties(String fileName,ClassLoader cls){
        Properties properties = new Properties();
        try(InputStream in = getInputStream(fileName,cls)){
            properties.load(in);
        }catch (Exception e) {
            //ignore not found file

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

    /**
     * 从系统环境或者系统属性获取指定key的值，前者优先级高
     * @param key
     * @return
     */
    public static String getValueFromSystemEnvOrProp(String key){
        String value = System.getenv(key);
        if(isNullOrEmpty(value)){
            value = System.getProperty(key);
        }
        return value;
    }

    public static String getValueFromSystemEnvOrProp(String key,String defaultValue){
        String value = getValueFromSystemEnvOrProp(key);
        return isNullOrEmpty(value) ? defaultValue : value;
    }

    /**
     * 检查字符串是否为数字
     * @param value
     * @return
     */
    public static boolean isDigital(String value){
        if(isNullOrEmpty(value)){
            return false;
        }
        for (char c : value.toCharArray()) {
            if(c < 48 || c > 57){
                return false;
            }
        }
        return true;
    }


}
