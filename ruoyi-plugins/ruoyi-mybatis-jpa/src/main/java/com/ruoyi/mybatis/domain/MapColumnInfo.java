package com.ruoyi.mybatis.domain;

import java.lang.reflect.Field;

import org.springframework.core.annotation.AnnotationUtils;

import com.ruoyi.mybatis.annotation.ColumnMap;
import com.ruoyi.mybatis.annotation.Query;

/**
 * 数据库关联字段信息
 *
 * @author Dftre
 */
public class MapColumnInfo {
    private String columnName;
    private String fieldName;
    private Field field;
    private Query query;
    private ColumnMap columnMap;
    private String querySql;

    public MapColumnInfo(Field field) {
        this.field = field;
        this.columnMap = AnnotationUtils.findAnnotation(this.field, ColumnMap.class);
        this.columnName = this.columnMap.name();
        this.query = AnnotationUtils.findAnnotation(this.field, Query.class);
        this.fieldName = this.field.getName();
        this.field.setAccessible(true);
        this.querySql = this.getQuerySql(this.query);
    }

    public ColumnMap getJoin() {
        return this.columnMap;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getTemplate() {
        return getTemplate(false);
    }

    public String getTemplate(boolean params) {
        if (params) {
            return "#{params." + fieldName + "}";
        } else {
            return "#{" + fieldName + "}";
        }
    }

    public boolean fieldIsNotNull(Object entity) {
        try {
            return this.field.get(entity) != null;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access field for building query conditions.", e);
        }
    }

    public String getQuerySql(Query query) {
        if (query == null)
            return "";
        String column = this.getColumnName();
        String iField = getTemplate();
        String begin = "#{params." + query.section()[0] + "}";
        String end = "#{params." + query.section()[1] + "}";
        return switch (query.operation()) {
            case eq -> column + " = " + iField;
            case ne -> column + " <> " + iField;
            case gt -> column + " > " + iField;
            case ge -> column + " >= " + iField;
            case le -> column + " < " + iField;
            case lt -> column + " <= " + iField;
            case between -> column + " between " + begin + " and " + end;
            case notBetween -> column + " not between " + begin + " and " + end;
            case like -> column + " like concat('%'," + iField + ",'%')";
            case notLike -> column + "not like concat('%'," + iField + ",'%')";
            case likeLeft -> column + "like concat('%'," + iField + ")";
            case likeRight -> column + "like concat(" + iField + ",'%')";
            case notLikeLeft -> column + "not like concat('%'," + iField + ")";
            case notLikeRight -> column + "not like concat(" + iField + ",'%')";
            case isNull -> column + " is null";
            case isNotNull -> column + " is not null";
            case in -> column + " in (" + iField + "";
            case notIn -> column + " not in (" + iField + "";
            case inSql -> column + " in (" + query.sql() + ")";
            case notInSql -> column + " not in (" + query.sql() + ")";
            default -> throw new IllegalArgumentException(
                    "Unsupported operation: " + query.operation());
        };
    }

    public String getQuerySql() {
        return this.columnMap.target() + "." + this.querySql;
    }

}
