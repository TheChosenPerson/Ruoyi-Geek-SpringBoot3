package com.ruoyi.mybatisinterceptor.aspectj;

import com.ruoyi.mybatisinterceptor.annotation.DataSecurity;
import com.ruoyi.mybatisinterceptor.context.sqlContext.SqlContextHolder;
import com.ruoyi.mybatisinterceptor.model.JoinTableModel;
import com.ruoyi.mybatisinterceptor.model.WhereModel;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;

@Aspect
@Component
public class DataSecurityAspect {

   @Before(value = "@annotation(dataSecurity)")
   public void doBefore(final JoinPoint point, DataSecurity dataSecurity) throws Throwable {
      SqlContextHolder.startDataSecurity();
      switch (dataSecurity.strategy()) {
         case CREEATE_BY:
            WhereModel createByModel = new WhereModel();
            createByModel.setTable(dataSecurity.table());
            createByModel.setValue("\"" + SecurityUtils.getUsername() + "\"");
            createByModel.setWhereColumn("create_by");
            createByModel.setMethod(WhereModel.METHOD_EQUAS);
            createByModel.setConnectType(WhereModel.CONNECT_AND);
            SqlContextHolder.addWhereParam(createByModel);
            break;
         case USER_ID:
            WhereModel userIdModel = new WhereModel();
            userIdModel.setTable(dataSecurity.table());
            userIdModel.setTable("user_id");
            userIdModel.setValue(SecurityUtils.getUserId());
            userIdModel.setConnectType(WhereModel.CONNECT_AND);
            userIdModel.setMethod(WhereModel.METHOD_EQUAS);
            SqlContextHolder.addWhereParam(userIdModel);
            break;
         case JOINTABLE_CREATE_BY:
            JoinTableModel createByTableModel = new JoinTableModel();
            createByTableModel.setFromTable(dataSecurity.table());
            createByTableModel.setFromTableAlise(dataSecurity.table());
            createByTableModel.setJoinTable("sys_user");
            if (!StringUtils.isEmpty(dataSecurity.joinTableAlise())) {
               createByTableModel.setJoinTableAlise(dataSecurity.joinTableAlise());
            }
            createByTableModel.setFromTableColumn("create_by");
            createByTableModel.setJoinTableColumn("user_name");
            SqlContextHolder.addJoinTable(createByTableModel);
            break;
         case JOINTABLE_USER_ID:
            JoinTableModel userIdTableModel = new JoinTableModel();
            userIdTableModel.setFromTable(dataSecurity.table());
            userIdTableModel.setFromTableAlise(dataSecurity.table());
            userIdTableModel.setJoinTable("sys_user");
            if (!StringUtils.isEmpty(dataSecurity.joinTableAlise())) {
               userIdTableModel.setJoinTableAlise(dataSecurity.joinTableAlise());
            }
            userIdTableModel.setFromTableColumn("user_id");
            userIdTableModel.setJoinTableColumn("user_id");
            SqlContextHolder.addJoinTable(userIdTableModel);
            break;
         default:
            break;
      }

   }

   @After(value = " @annotation(dataSecurity)")
   public void doAfter(final JoinPoint point, DataSecurity dataSecurity) {
      SqlContextHolder.clearCache();
   }
}
