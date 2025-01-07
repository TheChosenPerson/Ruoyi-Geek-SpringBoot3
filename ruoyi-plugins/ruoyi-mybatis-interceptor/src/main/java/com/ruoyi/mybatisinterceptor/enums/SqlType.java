package com.ruoyi.mybatisinterceptor.enums;

public enum SqlType {
   /** where */
   WHERE("where"),
   /** join */
   JOIN("join"),
   /** select */
   SELECT("select"),
   /** limit */
   LIMIT("limit");

   private String sqlType;

   public String getSqlType() {
      return sqlType;
   }

   private SqlType(String sqlType) {
      this.sqlType = sqlType;
   }
}
