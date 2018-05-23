package com.hiekn.boot.autoconfigure.mybatis;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Conditional(OnMultiplyDatasourceCondition.class)
public @interface ConditionOnMultiplyDatasource {
    String prefix() default "";
    String name() default "";
}
