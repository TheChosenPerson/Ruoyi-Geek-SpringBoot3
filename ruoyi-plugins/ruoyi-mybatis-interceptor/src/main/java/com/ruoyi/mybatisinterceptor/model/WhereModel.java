package com.ruoyi.mybatisinterceptor.model;

import com.ruoyi.common.utils.StringUtils;

public class WhereModel {
   private String whereColumn;
   private String table;
   private Object value;
   private String connectType;
   private String method;

   public static final String METHOD_EQUAS = "=";
   public static final String METHOD_LIKE = "like";
   public static final String CONNECT_AND = "AND";
   public static final String CONNECT_OR = "OR";

   public String getWhereColumn() {
      return whereColumn;
   }

   public void setWhereColumn(String whereColumn) {
      this.whereColumn = whereColumn;
   }

   public String getTable() {
      return table;
   }

   public void setTable(String table) {
      this.table = table;
   }

   public Object getValue() {
      return value;
   }

   public void setValue(Object value) {
      this.value = value;
   }

   public String getFullTableColumn() {
      if (StringUtils.isEmpty(this.table)) {
         return this.whereColumn;
      }
      return this.table + "." + this.whereColumn;
   }

   public String getConnectType() {
      return connectType;
   }

   public void setConnectType(String connectType) {
      this.connectType = connectType;
   }

   public String getMethod() {
      return method;
   }

   public void setMethod(String method) {
      this.method = method;
   }

   public String getSqlString() {
      return String.format(" %s %s %s %s ", this.getConnectType(), this.getFullTableColumn(), this.method, this.value);
   }
}
