package com.hiekn.boot.autoconfigure.base.util;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MybatisGenUtil {

    public static void genMapperAndXml(){
        Properties prop = new Properties();
        try {
            prop.load(MybatisGenUtil.class.getClassLoader().getResourceAsStream("generator.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        genMapperAndXml(Boolean.valueOf(prop.getProperty("overwrite","false")));//默认不进行覆盖
    }

    /**
     * @param overwrite 如果已经生成过了是否进行覆盖
     */
    private static void genMapperAndXml(boolean overwrite){
        List<String> warnings = new ArrayList<>();
        ConfigurationParser cfgParser = new ConfigurationParser(warnings);//配置文件解析器
        Configuration config = null;
        try {
            config = cfgParser.parseConfiguration(MybatisGenUtil.class.getClassLoader().getResourceAsStream("generatorConfig.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMLParserException e) {
            e.printStackTrace();
        }
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator generator = null;
        try {
            generator = new MyBatisGenerator(config, callback, warnings);
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        try {
            generator.generate(null);
            System.out.println("mybatis generator success");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
