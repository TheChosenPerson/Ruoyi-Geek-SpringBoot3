package com.ruoyi.mybatis.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.mybatis.annotation.Query;
import com.ruoyi.mybatis.enums.QueryEnum;

public class QueryUtil {

    public static boolean fieldQueryIsNotNull(Object entity, Field field, Query query) {
        try {
            if (query != null) {
                BaseEntity baseEntity = (BaseEntity) entity;
                Map<String, Object> map = baseEntity.getParams();
                if (query.operation().equals(QueryEnum.between)) {
                    return map.get(query.sections()[0]) != null && map.get(query.sections()[1]) != null;
                } else if (query.operation().equals(QueryEnum.in)) {
                    Object obj = map.get(query.section());
                    if (obj == null) {
                        return false;
                    } else {
                        if (obj instanceof String) {
                            List<String> list = new ArrayList<>();
                            for (String split : ((String) obj).split(",")) {
                                list.add("\"" + split + "\"");
                            }
                            map.put(query.section() + "_operation", String.join(",", list));
                            return true;
                        } else if (obj instanceof Collection) {
                            List<String> list = new ArrayList<>();
                            for (Object split : ((Collection<?>) obj)) {
                                list.add("\"" + split.toString() + "\"");
                            }
                            map.put(query.section() + "_operation", String.join(",", list));
                            return true;
                        } else {
                            return false;
                        }
                    }

                }
            }
            return field.get(entity) != null;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access field for building query conditions.", e);
        }
    }

    public static String getJoinSql(String leftTable, String rightTable, String leftField, String rightField) {
        return rightTable + " on "
                + leftTable + "." + leftField + " = "
                + rightTable + "." + rightField;
    }

    public static String getQuerySql(String column, String iField, Query query) {
        if (query == null)
            return "";
        String begin = "#{params." + query.sections()[0] + "}";
        String end = "#{params." + query.sections()[1] + "}";
        String inParams = "${params." + query.section() + "_operation" + "}";
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
            case in -> column + " in (" + inParams + ")";
            case notIn -> column + " not in (" + inParams + ")";
            case inSql -> column + " in (" + query.sql() + ")";
            case notInSql -> column + " not in (" + query.sql() + ")";
            default -> throw new IllegalArgumentException(
                    "Unsupported operation: " + query.operation());
        };
    }

}
