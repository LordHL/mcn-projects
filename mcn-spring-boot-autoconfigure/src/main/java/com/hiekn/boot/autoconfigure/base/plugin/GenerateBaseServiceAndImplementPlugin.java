package com.hiekn.boot.autoconfigure.base.plugin;

import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.beans.Introspector;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

public class GenerateBaseServiceAndImplementPlugin extends PluginAdapter {
    private String serviceTargetDir;
    private String serviceTargetPackage;
    private String service;

    private ShellCallback shellCallback = null;

    public GenerateBaseServiceAndImplementPlugin() {
        shellCallback = new DefaultShellCallback(false);
    }

    @Override
    public boolean validate(List<String> warnings) {
        serviceTargetDir = properties.getProperty("targetProject");
        serviceTargetPackage = properties.getProperty("targetPackage");
        service = properties.getProperty("service");
        return stringHasValue(serviceTargetDir) && stringHasValue(serviceTargetPackage) && stringHasValue(service);
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        List<GeneratedJavaFile> javaFiles = new ArrayList<>();
        for (GeneratedJavaFile javaFile : introspectedTable.getGeneratedJavaFiles()) {
            CompilationUnit unit = javaFile.getCompilationUnit();
            FullyQualifiedJavaType baseModelJavaType = unit.getType();
            String shortName = baseModelJavaType.getShortName();

            if (shortName.endsWith("Mapper")) {
                //create interface XxxService FullName
                String serviceInterfaceFullName = serviceTargetPackage + "." + shortName.replace("Mapper", "Service");
                Interface serviceInterface = new Interface(serviceInterfaceFullName);
                serviceInterface.setVisibility(JavaVisibility.PUBLIC);

                String pk = "Object";//default PK Object
                List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
                if(primaryKeyColumns != null && primaryKeyColumns.size() == 1){
                    for (IntrospectedColumn keyColumn : primaryKeyColumns) {
                        pk = keyColumn.getFullyQualifiedJavaType().getShortName();
                    }
                }

                String rootInterface = properties.getProperty("rootInterface");

                TopLevelClass topLevelClass = new TopLevelClass(new FullyQualifiedJavaType(serviceTargetPackage+".impl."+shortName.replace("Mapper", "ServiceImpl")));
                topLevelClass.setVisibility(JavaVisibility.PUBLIC);
                topLevelClass.addImportedType(service);
                topLevelClass.addImportedType(serviceInterfaceFullName);
                topLevelClass.addSuperInterface(new FullyQualifiedJavaType(serviceInterfaceFullName));
                topLevelClass.getAnnotations().add("@Service");

                if(rootInterface != null){
                    //create super interface BaseService
                    FullyQualifiedJavaType baseService = new FullyQualifiedJavaType("BaseService<"
                            + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + ","
                            + pk+ ">");
                    serviceInterface.addImportedType(new FullyQualifiedJavaType(rootInterface));
                    serviceInterface.addImportedType(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
                    serviceInterface.addSuperInterface(baseService);

                    //create super class BaseServiceImpl
                    FullyQualifiedJavaType baseServiceImpl = new FullyQualifiedJavaType("BaseServiceImpl<"
                            + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + ","
                            + pk+ ">");
                    topLevelClass.addImportedType(introspectedTable.getBaseRecordType());
                    topLevelClass.addImportedType(rootInterface.replace("BaseService","BaseServiceImpl"));
                    topLevelClass.setSuperClass(baseServiceImpl);
                }

                TopLevelClass restClass = new TopLevelClass(new FullyQualifiedJavaType(serviceTargetPackage.replace(".service",".rest.")+shortName.replace("Mapper", "RestApi")));
                restClass.setVisibility(JavaVisibility.PUBLIC);
                restClass.addImportedType("org.springframework.stereotype.Controller");
                restClass.addImportedType("javax.ws.rs.*");
                restClass.addImportedType("javax.ws.rs.core.MediaType");
                restClass.addImportedType("org.springframework.beans.factory.annotation.Autowired");
                restClass.addImportedType("javax.ws.rs.core.MediaType");
                restClass.addImportedType("io.swagger.annotations.*");
                restClass.addImportedType("com.hiekn.boot.autoconfigure.base.model.result.RestData");
                restClass.addImportedType("com.hiekn.boot.autoconfigure.base.model.result.RestResp");
                restClass.addImportedType("com.hiekn.boot.autoconfigure.base.util.BeanValidator");
                restClass.addImportedType("com.hiekn.boot.autoconfigure.base.util.JsonUtils");
                restClass.addImportedType("com.hiekn.boot.autoconfigure.base.model.PageModel");
                restClass.addImportedType("com.google.common.collect.Maps");
                restClass.addImportedType("java.util.Map");
                restClass.addImportedType(serviceInterfaceFullName);
                restClass.addImportedType(introspectedTable.getBaseRecordType());
                restClass.getAnnotations().add("@Controller");
                String path = Introspector.decapitalize(shortName.replace("Mapper", ""));
                restClass.getAnnotations().add("@Path(\"/"+path+"\")");
                restClass.getAnnotations().add("@Produces(MediaType.APPLICATION_JSON)");
                restClass.getAnnotations().add("@Api(\""+shortName.replace("Mapper", "RestApi")+"\")");
                restClass.getAnnotations().add("@ApiImplicitParams({@ApiImplicitParam(paramType = \"header\", dataType = \"string\", name = \"Authorization\",required = true)})\n");
                String xService = Introspector.decapitalize(shortName.replace("Mapper", "Service"));
                Field field = new Field(xService, new FullyQualifiedJavaType(serviceInterfaceFullName));
                field.getAnnotations().add("@Autowired");
                field.setVisibility(JavaVisibility.PRIVATE);
                restClass.getFields().add(field);
                addBaseMethod(introspectedTable,restClass,xService,pk);

                try {
                    JavaFormatter javaFormatter = context.getJavaFormatter();
                    //gen XxxService
                    checkAndAddJavaFile(new GeneratedJavaFile(serviceInterface, serviceTargetDir, javaFormatter),javaFiles);
                    //gen XxxServiceImpl
                    checkAndAddJavaFile(new GeneratedJavaFile(topLevelClass, serviceTargetDir, javaFormatter),javaFiles);
                    //gen XxxRestApi
                    checkAndAddJavaFile(new GeneratedJavaFile(restClass, serviceTargetDir, javaFormatter),javaFiles);
                } catch (ShellException e) {
                    e.printStackTrace();
                }
            }
        }
        return javaFiles;
    }

    private void checkAndAddJavaFile(GeneratedJavaFile javaFile,List<GeneratedJavaFile> javaFiles) throws ShellException {
        File dir = shellCallback.getDirectory(serviceTargetDir, serviceTargetPackage);
        File file = new File(dir, javaFile.getFileName());
        if (file.exists()) {
            file.delete();
        }
        javaFiles.add(javaFile);
    }


    private void addBaseMethod(IntrospectedTable introspectedTable,TopLevelClass restClass,String xService,String pk){
        String bean = Introspector.decapitalize(introspectedTable.getFullyQualifiedTable().getDomainObjectName());
        String Bean = introspectedTable.getFullyQualifiedTable().getDomainObjectName();

        Method listByPage = new Method("listByPage");
        listByPage.getAnnotations().add("@GET");
        listByPage.getAnnotations().add("@Path(\"/list/page\")");
        listByPage.getAnnotations().add("@ApiOperation(\"分页\")");
        listByPage.setVisibility(JavaVisibility.PUBLIC);
        listByPage.setReturnType(new FullyQualifiedJavaType("RestResp<RestData<"
                + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + ">>"));
        Parameter parameter = new Parameter(new FullyQualifiedJavaType("@BeanParam PageModel"),"pageModel");
        listByPage.getParameters().add(parameter);
        listByPage.getBodyLines().add("Map<String,Object> params = Maps.newHashMap();");
        listByPage.getBodyLines().add("return new RestResp<>("+xService+".listByPage(pageModel,params));");
        restClass.getMethods().add(listByPage);

        Method get = new Method("get");
        get.getAnnotations().add("@GET");
        get.getAnnotations().add("@Path(\"/get\")");
        get.getAnnotations().add("@ApiOperation(\"详情\")");
        get.setVisibility(JavaVisibility.PUBLIC);
        get.setReturnType(new FullyQualifiedJavaType("RestResp<"
                + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + ">"));
        parameter = new Parameter(new FullyQualifiedJavaType("@ApiParam(required = true)@QueryParam(\"id\") "+pk+""),"id");
        get.getParameters().add(parameter);
        get.getBodyLines().add("return new RestResp<>("+xService+".getByPrimaryKey(id));");
        restClass.getMethods().add(get);

        Method add = new Method("add");
        add.getAnnotations().add("@POST");
        add.getAnnotations().add("@Path(\"/add\")");
        add.getAnnotations().add("@ApiOperation(\"新增\")");
        add.setVisibility(JavaVisibility.PUBLIC);
        add.setReturnType(new FullyQualifiedJavaType("RestResp<"
                + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + ">"));
        parameter = new Parameter(new FullyQualifiedJavaType("@ApiParam(required = true)@FormParam(\"bean\") String"),"bean");
        add.getParameters().add(parameter);

        add.getBodyLines().add(""+Bean+" "+bean+" = JsonUtils.fromJson(bean, "+Bean+".class);");
        add.getBodyLines().add("BeanValidator.validate("+bean+");");
        add.getBodyLines().add(""+xService+".save("+bean+");");
        add.getBodyLines().add("return new RestResp<>("+bean+");");
        restClass.getMethods().add(add);

        Method delete = new Method("delete");
        delete.getAnnotations().add("@GET");
        delete.getAnnotations().add("@Path(\"/delete\")");
        delete.getAnnotations().add("@ApiOperation(\"删除\")");
        delete.setVisibility(JavaVisibility.PUBLIC);
        delete.setReturnType(new FullyQualifiedJavaType("RestResp"));
        parameter = new Parameter(new FullyQualifiedJavaType("@ApiParam(required = true)@QueryParam(\"id\") "+pk+""),"id");
        delete.getParameters().add(parameter);
        delete.getBodyLines().add(""+xService+".deleteByPrimaryKey(id);");
        delete.getBodyLines().add("return new RestResp<>();");
        restClass.getMethods().add(delete);

        Method update = new Method("update");
        update.getAnnotations().add("@POST");
        update.getAnnotations().add("@Path(\"/update\")");
        update.getAnnotations().add("@ApiOperation(\"修改\")");
        update.setVisibility(JavaVisibility.PUBLIC);
        update.setReturnType(new FullyQualifiedJavaType("RestResp"));
        parameter = new Parameter(new FullyQualifiedJavaType("@ApiParam(required = true)@QueryParam(\"id\") "+pk+""),"id");
        update.getParameters().add(parameter);
        parameter = new Parameter(new FullyQualifiedJavaType("@ApiParam(required = true)@FormParam(\"bean\") String"),"bean");
        update.getParameters().add(parameter);
        update.getBodyLines().add(""+Bean+" "+bean+" = JsonUtils.fromJson(bean, "+Bean+".class);");
        update.getBodyLines().add(""+bean+".setId(id);");
        update.getBodyLines().add(""+xService+".updateByPrimaryKeySelective("+bean+");");
        update.getBodyLines().add("return new RestResp<>();");
        restClass.getMethods().add(update);
    }

}