package com.ruoyi.mybatisinterceptor.handler.page;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.ruoyi.common.utils.sql.SqlUtil;
import com.ruoyi.mybatisinterceptor.annotation.MybatisHandlerOrder;
import com.ruoyi.mybatisinterceptor.context.page.PageContextHolder;
import com.ruoyi.mybatisinterceptor.context.page.model.PageInfo;
import com.ruoyi.mybatisinterceptor.handler.MybatisPreHandler;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;

@Component
@MybatisHandlerOrder(2)
public class PagePreHandler implements MybatisPreHandler {

      private static final List<ResultMapping> EMPTY_RESULTMAPPING = new ArrayList<ResultMapping>(0);

      private static final String SELECT_COUNT_SUFIX = "_SELECT_COUNT";
      private static final Field sqlFiled = ReflectionUtils.findField(BoundSql.class, "sql");
      static {
            sqlFiled.setAccessible(true);
      }

      @Override
      public void preHandle(Executor executor, MappedStatement mappedStatement, Object params, RowBounds rowBounds,
                  ResultHandler<?> resultHandler, CacheKey cacheKey, BoundSql boundSql) throws Throwable {
            if (PageContextHolder.isPage()) {
                  String originSql = boundSql.getSql();
                  Statement sql = SqlUtil.parseSql(originSql);
                  if (sql instanceof Select) {
                        PageInfo pageInfo = PageContextHolder.getPageInfo();
                        Statement handleLimit = handleLimit((Select) sql, pageInfo);
                        Statement countSql = getCountSql((Select) sql);
                        Long count = getCount(executor, mappedStatement, params, boundSql, rowBounds, resultHandler,
                                    countSql.toString());
                        PageContextHolder.setTotal(count);
                        sqlFiled.set(boundSql, handleLimit.toString());
                        cacheKey = executor.createCacheKey(mappedStatement, params, rowBounds, boundSql);
                  }
            }

      }

      private static MappedStatement createCountMappedStatement(MappedStatement ms, String newMsId) {
            MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), newMsId,
                        ms.getSqlSource(),
                        ms.getSqlCommandType());
            builder.resource(ms.getResource());
            builder.fetchSize(ms.getFetchSize());
            builder.statementType(ms.getStatementType());
            builder.keyGenerator(ms.getKeyGenerator());
            if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
                  StringBuilder keyProperties = new StringBuilder();
                  for (String keyProperty : ms.getKeyProperties()) {
                        keyProperties.append(keyProperty).append(",");
                  }
                  keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
                  builder.keyProperty(keyProperties.toString());
            }
            builder.timeout(ms.getTimeout());
            builder.parameterMap(ms.getParameterMap());
            // count查询返回值int
            List<ResultMap> resultMaps = new ArrayList<ResultMap>();
            ResultMap resultMap = new ResultMap.Builder(ms.getConfiguration(), ms.getId(), Long.class,
                        EMPTY_RESULTMAPPING)
                        .build();
            resultMaps.add(resultMap);
            builder.resultMaps(resultMaps);
            builder.resultSetType(ms.getResultSetType());
            builder.cache(ms.getCache());
            builder.flushCacheRequired(ms.isFlushCacheRequired());
            builder.useCache(ms.isUseCache());
            return builder.build();
      }

      public static Long getCount(Executor executor, MappedStatement mappedStatement, Object parameter,
                  BoundSql boundSql, RowBounds rowBounds, ResultHandler<?> resultHandler, String countSql)
                  throws SQLException {

            Map<String, Object> additionalParameters = boundSql.getAdditionalParameters();

            BoundSql countBoundSql = new BoundSql(mappedStatement.getConfiguration(), countSql,
                        boundSql.getParameterMappings(), parameter);
            for (String key : additionalParameters.keySet()) {
                  countBoundSql.setAdditionalParameter(key, additionalParameters.get(key));
            }
            CacheKey countKey = executor.createCacheKey(mappedStatement, parameter, RowBounds.DEFAULT, countBoundSql);

            List<Object> query = executor.query(
                        createCountMappedStatement(mappedStatement, getCountMSId(mappedStatement)),
                        parameter, RowBounds.DEFAULT, resultHandler, countKey,
                        countBoundSql);
            return (Long) query.get(0);
      }

      private static String getCountMSId(MappedStatement mappedStatement) {
            return mappedStatement.getId() + SELECT_COUNT_SUFIX;
      }

      public static Statement getCountSql(Select select) {
            PlainSelect plain = select.getPlainSelect();
            PlainSelect countPlain = new PlainSelect();
            countPlain.setSelectItems(List.of(new SelectItem<>(new Column("COUNT(0)"))));
            countPlain.setJoins(plain.getJoins());
            countPlain.setWhere(plain.getWhere());
            countPlain.setFromItem(plain.getFromItem());
            countPlain.setDistinct(plain.getDistinct());
            countPlain.setHaving(plain.getHaving());
            countPlain.setIntoTables(plain.getIntoTables());
            // countPlain.setOrderByElements(plain.getOrderByElements());
            return plain;
      }

      private static Statement handleLimit(Select select, PageInfo pageInfo) {
            Limit limit = new Limit();
            limit.setRowCount(new Column(pageInfo.getPageSize().toString()));
            limit.setOffset(new Column(pageInfo.getOffeset().toString()));
            PlainSelect plain = select.getPlainSelect();
            plain.setLimit(limit);
            return select;
      }

}
