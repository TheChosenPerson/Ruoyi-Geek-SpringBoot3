package com.ruoyi.mybatisinterceptor.annotation;

import com.ruoyi.mybatisinterceptor.enums.DataSecurityStrategy;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSecurity {
   public DataSecurityStrategy strategy() default DataSecurityStrategy.CREEATE_BY;

   public String table() default "";

   public String joinTableAlise() default "";
}
