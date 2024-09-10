package com.ruoyi.mybatisinterceptor.sql.dataSecurity;

import java.lang.reflect.Field;
import java.util.List;

import com.ruoyi.mybatisinterceptor.annotation.MybatisHandlerOrder;
import com.ruoyi.mybatisinterceptor.context.dataSecurity.SqlContextHolder;
import com.ruoyi.mybatisinterceptor.model.JoinTableModel;
import com.ruoyi.mybatisinterceptor.model.WhereModel;
import com.ruoyi.mybatisinterceptor.sql.MybatisPreHandler;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.sql.SqlUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

@MybatisHandlerOrder(1)
@Component
public class DataSecurityPreHandler implements MybatisPreHandler {

   private static final Field sqlFiled = ReflectionUtils.findField(BoundSql.class, "sql");
   static {
      sqlFiled.setAccessible(true);
   }

   @Override
   public void preHandle(Executor executor, MappedStatement mappedStatement, Object params, RowBounds rowBounds,
         ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql) throws Throwable {
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

   private static void handleWhere(Select select) throws JSQLParserException {
      PlainSelect plain = select.getPlainSelect();
      Expression expWhere = plain.getWhere();
      StringBuilder whereParam = new StringBuilder(" ");
      String where = expWhere != null ? expWhere.toString() : null;
      if (SqlContextHolder.getWhere() == null || SqlContextHolder.getWhere().size() <= 0) {
         return;
      }
      SqlContextHolder.getWhere().forEach(item -> {
         whereParam.append(((WhereModel) item).getSqlString());
      });
      where = StringUtils.isEmpty(where) ? whereParam.toString().substring(5, whereParam.length())
            : where + " " + whereParam.toString();
      plain.setWhere(CCJSqlParserUtil.parseCondExpression(where));
   }

   private static void handleJoin(Select select) {
      PlainSelect selectBody = select.getPlainSelect();
      if (SqlContextHolder.getJoinTables() == null || SqlContextHolder.getJoinTables().size() <= 0) {
         return;
      }
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
         selectBody.addJoins(join);
      });
   }

}
