package com.hiekn.generator;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * MybatisGeneratorMojo
 *
 * @author DingHao
 * @date 2018/12/24 12:50
 */
@Mojo(name = "generate")
public class MybatisGeneratorMojo extends AbstractMojo {

    @Parameter(defaultValue="${project.basedir}/src/main/resources/generatorConfig.xml", required=true)
    private File configurationFile;

    @Parameter(required = true)
    private Properties propConfig;


    public void execute() throws MojoExecutionException {
        String basePkg = propConfig.getProperty("basePkg");
        if(Objects.nonNull(basePkg) && !basePkg.isEmpty()){
            propConfig.setProperty("bean-package",basePkg+".bean");
            propConfig.setProperty("service-package",basePkg+".service");
            propConfig.setProperty("dao-package",basePkg+".dao");
        }
        boolean overwrite = Boolean.valueOf(propConfig.getProperty("overwrite","false"));
        List<String> warnings = new ArrayList<>();
        ConfigurationParser cfgParser = new ConfigurationParser(propConfig,warnings);
        Configuration config = null;
        try {
            config = cfgParser.parseConfiguration(configurationFile);
        } catch (IOException e) {
            getLog().error(e);
        } catch (XMLParserException e) {
            getLog().error(e);
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
            getLog().info("code generate success");
        } catch (SQLException e) {
            getLog().error(e);
        } catch (IOException e) {
            getLog().error(e);
        } catch (InterruptedException e) {
            getLog().error(e);
        }
    }

}
