package com.ruoyi.mybatisinterceptor.enums;

public enum SqlType {
   WHERE("where"),
   JOIN("join"),
   SELECT("select"),
   LIMIT("limit");

   private String sqlType;

   public String getSqlType() {
      return sqlType;
   }

   private SqlType(String sqlType) {
      this.sqlType = sqlType;
   }
}
