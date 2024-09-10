package com.ruoyi.mybatisinterceptor.sql;

public interface MybatisAfterHandler {

   Object handleObject(Object object) throws Throwable;

}
