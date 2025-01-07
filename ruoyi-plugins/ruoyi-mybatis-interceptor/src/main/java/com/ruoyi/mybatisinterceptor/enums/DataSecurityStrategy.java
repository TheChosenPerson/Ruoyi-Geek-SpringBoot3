package com.ruoyi.mybatisinterceptor.enums;

public enum DataSecurityStrategy {
   /** 通过创建人字段关联表 */
   JOINTABLE_CREATE_BY,
   /** 通过用户ID字段关联表 */
   JOINTABLE_USER_ID,
   /** 通过创建人字段 */
   CREEATE_BY,
   /** 通过用户ID字段 */
   USER_ID;
}
