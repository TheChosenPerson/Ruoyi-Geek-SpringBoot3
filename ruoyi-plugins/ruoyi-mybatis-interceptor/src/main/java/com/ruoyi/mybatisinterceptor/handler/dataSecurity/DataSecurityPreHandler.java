package com.ruoyi.mybatisinterceptor.handler.dataSecurity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.alibaba.fastjson2.JSONArray;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.sql.SqlUtil;
import com.ruoyi.mybatisinterceptor.annotation.MybatisHandlerOrder;
import com.ruoyi.mybatisinterceptor.context.sqlContext.SqlContextHolder;
import com.ruoyi.mybatisinterceptor.handler.MybatisPreHandler;
import com.ruoyi.mybatisinterceptor.model.JoinTableModel;
import com.ruoyi.mybatisinterceptor.model.WhereModel;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;

@MybatisHandlerOrder(1)
@Component
public class DataSecurityPreHandler implements MybatisPreHandler {

   private static final Field sqlFiled = ReflectionUtils.findField(BoundSql.class, "sql");
   static {
      sqlFiled.setAccessible(true);
   }

   @Override
   public void preHandle(Executor executor, MappedStatement mappedStatement, Object params, RowBounds rowBounds,
         ResultHandler<?> resultHandler, CacheKey cacheKey, BoundSql boundSql) throws Throwable {
      if (SqlContextHolder.isSecurity()) {
         Statement sql = parseSql(SqlUtil.parseSql(boundSql.getSql()));
         sqlFiled.set(boundSql, sql.toString());
      }
   }

   private static Statement parseSql(Statement statement) throws JSQLParserException {
      if (statement instanceof Select) {
         Select select = (Select) statement;
         // plain.setWhere(CCJSqlParserUtil.parseCondExpression(handleWhere(expWhere)));
         handleWhere(select);
         handleJoin(select);
         return select;
      } else {
         return statement;
      }
   }

   private static void handleWhere(Statement statement) throws JSQLParserException {
      if (statement instanceof Select) {
         Select select = (Select) statement;
         PlainSelect plainSelect = select.getPlainSelect();
         plainSelect.setWhere(getConfigedWhereExpression(plainSelect.getWhere()));
      } else if (statement instanceof Update) {
         Update update = (Update) statement;
         update.setWhere(getConfigedWhereExpression(update.getWhere()));
      } else if (statement instanceof Delete) {
         Delete delete = (Delete) statement;
         delete.setWhere(getConfigedWhereExpression(delete.getWhere()));
      }

   }

   private static Expression getConfigedWhereExpression(Expression expWhere) throws JSQLParserException {
      StringBuilder whereParam = new StringBuilder(" ");
      String where = expWhere != null ? expWhere.toString() : null;
      if (SqlContextHolder.getWhere() == null || SqlContextHolder.getWhere().size() <= 0) {
         return expWhere;
      }
      JSONArray wehreArray = SqlContextHolder.getWhere();
      wehreArray.forEach(item -> {
         whereParam.append(((WhereModel) item).getSqlString());
      });
      WhereModel whereModel = (WhereModel) wehreArray.get(0);
      where = StringUtils.isEmpty(where)
            ? whereParam.toString().substring(whereModel.getConnectType().length() + 2, whereParam.length())
            : where + " " + whereParam.toString();
      return CCJSqlParserUtil.parseCondExpression(where);
   }

   private static void handleJoin(Statement statement) {
      if (SqlContextHolder.getJoinTables() == null || SqlContextHolder.getJoinTables().size() <= 0) {
         return;
      }
      if (statement instanceof Select) {
         Select select = (Select) statement;
         select.getPlainSelect().addJoins(getConfigedJoinExpression());
      } else if (statement instanceof Update) {
         Update update = (Update) statement;
         update.addJoins(getConfigedJoinExpression());
      } else if (statement instanceof Delete) {
         Delete delete = (Delete) statement;
         delete.addJoins(getConfigedJoinExpression());
      }
   }

   private static List<Join> getConfigedJoinExpression() {
      List<Join> joins = new ArrayList<>();
      SqlContextHolder.getJoinTables().forEach(item -> {
         JoinTableModel tableModel = (JoinTableModel) item;
         Table table = new Table(tableModel.getJoinTable());
         table.setAlias(new Alias(tableModel.getJoinTableAlise()));
         Join join = new Join();
         join.setRightItem(table);
         join.setInner(true);
         Expression onExpression = new EqualsTo(new Column(tableModel.getFromTableColumnString()),
               new Column(tableModel.getJoinTableColumnString()));
         join.setOnExpressions(List.of(onExpression));
         joins.add(join);
      });
      return joins;
   }
}
