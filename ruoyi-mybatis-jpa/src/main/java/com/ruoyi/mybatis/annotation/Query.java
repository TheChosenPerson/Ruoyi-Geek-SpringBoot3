package com.ruoyi.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ruoyi.mybatis.enums.QueryEnum;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Query {
    QueryEnum operation() default QueryEnum.eq; // 操作符，如 eq, like, gt 等

    String[] section() default { "begin", "end" };

    boolean params() default false;

    String sql() default "";
}
