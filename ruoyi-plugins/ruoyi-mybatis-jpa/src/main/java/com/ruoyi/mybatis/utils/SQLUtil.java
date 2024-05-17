package com.ruoyi.mybatis.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.jdbc.SQL;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.mybatis.domain.ColumnInfo;
import com.ruoyi.mybatis.domain.MapColumnInfo;
import com.ruoyi.mybatis.domain.TableInfo;

/**
 * sql构建工具
 *
 * @author Dftre
 */
public class SQLUtil {

    private static final Map<Class<?>, TableInfo> tableInfoMap = new HashMap<>();

    private static <T extends BaseEntity> TableInfo getTableInfo(T entity) {
        Class<?> clazz = entity.getClass();
        TableInfo tableInfo = tableInfoMap.get(clazz);
        if (tableInfo == null)
            tableInfo = new TableInfo(clazz);
        return tableInfo;
    }

    public static <T extends BaseEntity> String list(T entity) {
        SQL sql = new SQL();
        TableInfo tableInfo = getTableInfo(entity);
        sql.SELECT(String.join(",", tableInfo.getQueryColumns()))
                .FROM(tableInfo.getTableNameFrom());

        if (tableInfo.isEnbleMap()) {
            tableInfo.getJoinSql().stream()
                    .filter(StringUtils::isNotEmpty)
                    .forEach(sql::LEFT_OUTER_JOIN);
            tableInfo.getNotNullMapColumnsForQuery(entity).stream()
                    .map(MapColumnInfo::getQuerySql)
                    .forEach(sql::WHERE);
            tableInfo.getNotNullColumnsForQuery(entity).stream()
                    .map(ColumnInfo::getQuerySql)
                    .map(query -> tableInfo.getTableNameT() + "." + query)
                    .forEach(sql::WHERE);
            if (tableInfo.hasDataScope()) {
                sql.WHERE("1=1 ${params.dataScope}");
            }

            Arrays.stream(tableInfo.getOrderBy())
                    .filter(StringUtils::isNotEmpty)
                    .map(order -> tableInfo.getTableNameT() + "." + order)
                    .forEach(sql::ORDER_BY);
        } else {
            tableInfo.getNotNullColumnsForQuery(entity).stream()
                    .map(ColumnInfo::getQuerySql)
                    .filter(StringUtils::isNotEmpty)
                    .forEach(sql::WHERE);
            Arrays.stream(tableInfo.getOrderBy())
                    .filter(StringUtils::isNotEmpty)
                    .forEach(sql::ORDER_BY);
        }
        return sql.toString();
    }

    public static <T extends BaseEntity> String insert(T entity) {
        SQL sql = new SQL();
        TableInfo tableInfo = getTableInfo(entity);
        sql.INSERT_INTO(tableInfo.getTableName());
        tableInfo.getNotNullColumns(entity).stream()
                .forEach(column -> sql.VALUES(column.getColumnName(), column.getTemplate()));

        return sql.toString();
    }

    public static <T extends BaseEntity> String update(T entity) {
        SQL sql = new SQL();
        TableInfo tableInfo = getTableInfo(entity);
        sql.UPDATE(tableInfo.getTableName());
        List<String> sets = new ArrayList<String>();
        tableInfo.getColumns().stream()
                .forEach(column -> {
                    if (column.isPrimaryKey()) {
                        sql.WHERE(column.getColumnName() + " = " + column.getTemplate());
                    } else {
                        sets.add(column.getColumnName() + " = " + column.getTemplate());
                    }
                });
        sql.SET(sets.toArray(new String[0]));
        return sql.toString();
    }

    public static <T extends BaseEntity> String deleteById(T entity) {
        SQL sql = new SQL();
        TableInfo tableInfo = getTableInfo(entity);
        sql.DELETE_FROM(tableInfo.getTableName());
        tableInfo.getPrimaryKeys().stream()
                .map(column -> column.getColumnName() + " = " + column.getTemplate())
                .forEach(sql::WHERE);

        return sql.toString();
    }

    public static <T extends BaseEntity> String selectById(T entity) {
        SQL sql = new SQL();
        TableInfo tableInfo = getTableInfo(entity);
        sql.SELECT(String.join(",", tableInfo.getQueryColumns()))
                .FROM(tableInfo.getTableNameFrom());

        if (tableInfo.isEnbleMap()) {
            tableInfo.getJoinSql().stream()
                    .forEach(sql::LEFT_OUTER_JOIN);

            tableInfo.getPrimaryKeys().stream()
                    .map(column -> tableInfo.getTableNameT() + "." + column.getColumnName() + " = "
                            + column.getTemplate())
                    .forEach(sql::WHERE);
        } else {
            tableInfo.getPrimaryKeys().stream()
                    .map(column -> column.getColumnName() + " = " + column.getTemplate())
                    .forEach(sql::WHERE);
        }

        return sql.toString();
    }
}
