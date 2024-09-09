package com.ruoyi.common.annotation.sql;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ruoyi.common.enums.DataSecurityStrategy;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSecurity {
   public DataSecurityStrategy strategy() default DataSecurityStrategy.CREEATE_BY;

   public String table() default "";

   public String joinTableAlise() default "";
}
