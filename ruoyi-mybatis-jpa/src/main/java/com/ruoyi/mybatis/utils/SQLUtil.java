package com.ruoyi.mybatis.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.jdbc.SQL;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.mybatis.domain.ColumnInfo;
import com.ruoyi.mybatis.domain.MapColumnInfo;
import com.ruoyi.mybatis.domain.TableInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
                    .forEach(sql::LEFT_OUTER_JOIN);
            tableInfo.getNotNullMapColumns(entity).stream()
                    .map(MapColumnInfo::getQuerySql)
                    .forEach(sql::WHERE);
            tableInfo.getNotNullColumns(entity).stream()
                    .map(ColumnInfo::getQuerySql)
                    .map(query -> tableInfo.getTableNameT() + "." + query)
                    .forEach(sql::WHERE);
        } else {
            tableInfo.getNotNullColumns(entity).stream()
                    .map(ColumnInfo::getQuerySql)
                    .forEach(sql::WHERE);
        }
        log.debug(sql.toString());
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
