package com.ruoyi.mybatisinterceptor.model;

import com.ruoyi.common.utils.StringUtils;

public class JoinTableModel {
   private String joinTable;

   private String joinTableAlise;

   private String fromTable;

   private String fromTableAlise;

   private String joinTableColumn;

   private String fromTableColumn;

   public String getJoinTable() {
      return joinTable;
   }

   public void setJoinTable(String joinTable) {
      this.joinTable = joinTable;
   }

   public String getJoinTableAlise() {
      if (StringUtils.isEmpty(this.joinTableAlise)) {
         return this.joinTable;
      }
      return joinTableAlise;
   }

   public void setJoinTableAlise(String joinTableAlise) {

      this.joinTableAlise = joinTableAlise;
   }

   public String getFromTable() {
      return fromTable;
   }

   public void setFromTable(String fromTable) {
      this.fromTable = fromTable;
   }

   public String getFromTableAlise() {
      if (StringUtils.isEmpty(this.fromTableAlise)) {
         return this.fromTable;
      }
      return fromTableAlise;
   }

   public void setFromTableAlise(String fromTableAlise) {
      this.fromTableAlise = fromTableAlise;
   }

   public String getJoinTableColumn() {

      return joinTableColumn;
   }

   public void setJoinTableColumn(String joinTableColumn) {
      this.joinTableColumn = joinTableColumn;
   }

   public String getFromTableColumn() {
      return fromTableColumn;
   }

   public void setFromTableColumn(String fromTableColumn) {
      this.fromTableColumn = fromTableColumn;
   }

   public String getJoinTableColumnString() {
      return this.getJoinTableAlise() + "." + this.joinTableColumn;
   }

   public String getFromTableColumnString() {
      if (StringUtils.isEmpty(this.getFromTableAlise())) {
         return this.fromTableColumn;
      }
      return this.getFromTableAlise() + "." + this.fromTableColumn;
   }

}
