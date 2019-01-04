package com.hiekn.boot.autoconfigure.base.util;

import com.hiekn.boot.autoconfigure.base.exception.BaseException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 封装一些常用的工具
 *
 * @author DingHao
 * @date 2018/12/22 13:23
 */
public abstract class McnUtils {


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

    /**
     * 读取文件所有行
     * @param filePath
     * @return
     */
    public static List<String> readAllLine(String filePath) {
        try {
            return Files.readAllLines(buildPath(filePath));
        } catch (IOException e) {
           throw new BaseException(e);
        }
    }

    /**
     * 返回文件所有字节
     * @param filePath 文件路径
     * @return
     */
    public static byte[] readAllBytes(String filePath) {
        try {
            return Files.readAllBytes(buildPath(filePath));
        } catch (IOException e) {
            throw new BaseException(e);
        }
    }

    /**
     * 复制文件
     * @param source 源文件
     * @param target 目标文件
     * @return
     */
    public static long copyFile(String source,String target) {
        try {
            return Files.copy(buildPath(source),Files.newOutputStream(buildPath(target)));
        } catch (IOException e) {
            throw new BaseException(e);
        }
    }

    /**
     * 复制文件
     * @param source 源文件
     * @param target 目标文件
     * @return
     */
    public static long copyFile(File source,File target) {
        return copyFile(source.getAbsolutePath(),target.getAbsolutePath());
    }

    private static Path buildPath(String filePath){
        return Paths.get(filePath);
    }


}
