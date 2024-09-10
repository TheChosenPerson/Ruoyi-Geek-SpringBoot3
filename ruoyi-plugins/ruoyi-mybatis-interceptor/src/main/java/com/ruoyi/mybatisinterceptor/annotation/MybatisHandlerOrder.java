package com.ruoyi.mybatisinterceptor.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MybatisHandlerOrder {
   public int value() default 0;
}
