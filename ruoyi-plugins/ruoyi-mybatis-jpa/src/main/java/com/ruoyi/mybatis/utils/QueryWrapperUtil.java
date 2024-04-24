// package com.ruoyi.mybatis.utils;

// import java.util.Arrays;
// import java.util.Map;

// import org.springframework.core.annotation.AnnotationUtils;

// import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// import com.ruoyi.common.core.domain.BaseEntity;
// import com.ruoyi.mybatis.annotation.Query;

// public class QueryWrapperUtil {
//     public static <T extends BaseEntity> QueryWrapper<T> initQueryWrapper(T entity) {
//         QueryWrapper<T> queryWrapper = new QueryWrapper<>();

//         Class<?> clazz = entity.getClass();
//         Map<String, Object> params = entity.getParams();
//         Arrays.stream(clazz.getDeclaredFields())
//                 .filter(field -> AnnotationUtils.findAnnotation(field, Query.class) != null)
//                 .forEach(field -> {
//                     field.setAccessible(true);
//                     Query queryAnnotation = field.getAnnotation(Query.class);

//                     try {
//                         Object fieldValue = field.get(entity);
//                         if (fieldValue != null) { // 判断属性值是否非空
//                             switch (queryAnnotation.operation()) {
//                                 case eq -> queryWrapper.eq(queryAnnotation.column(), fieldValue);
//                                 case ne -> queryWrapper.ne(queryAnnotation.column(), fieldValue);
//                                 case gt -> queryWrapper.gt(queryAnnotation.column(), fieldValue);
//                                 case ge -> queryWrapper.ge(queryAnnotation.column(), fieldValue);
//                                 case le -> queryWrapper.le(queryAnnotation.column(), fieldValue);
//                                 case lt -> queryWrapper.lt(queryAnnotation.column(), fieldValue);
//                                 case between -> {
//                                     String begin = queryAnnotation.section()[0];
//                                     String end = queryAnnotation.section()[1];
//                                     queryWrapper.between(queryAnnotation.column(), params.get(begin), params.get(end));
//                                 }
//                                 case notBetween -> {
//                                     String begin = queryAnnotation.section()[0];
//                                     String end = queryAnnotation.section()[1];
//                                     queryWrapper.notBetween(queryAnnotation.column(), params.get(begin),
//                                             params.get(end));
//                                 }
//                                 case like -> queryWrapper.like(queryAnnotation.column(), fieldValue);
//                                 case notLike -> queryWrapper.notLike(queryAnnotation.column(), fieldValue);
//                                 case likeLeft -> queryWrapper.likeLeft(queryAnnotation.column(), fieldValue);
//                                 case likeRight -> queryWrapper.likeRight(queryAnnotation.column(), fieldValue);
//                                 case notLikeLeft -> queryWrapper.notLikeLeft(queryAnnotation.column(), fieldValue);
//                                 case notLikeRight -> queryWrapper.notLikeRight(queryAnnotation.column(), fieldValue);
//                                 case isNull -> queryWrapper.isNull(queryAnnotation.column());
//                                 case isNotNull -> queryWrapper.isNotNull(queryAnnotation.column());
//                                 case in -> queryWrapper.in(queryAnnotation.column(), (Object[]) fieldValue);
//                                 case notIn -> queryWrapper.notIn(queryAnnotation.column(), (Object[]) fieldValue);
//                                 case inSql -> queryWrapper.inSql(queryAnnotation.column(), queryAnnotation.sql());
//                                 case notInSql -> queryWrapper.notInSql(queryAnnotation.column(), queryAnnotation.sql());
//                                 default ->
//                                     throw new IllegalArgumentException(
//                                             "Unsupported operation: " + queryAnnotation.operation());
//                             }
//                         }
//                     } catch (IllegalAccessException e) {
//                         throw new RuntimeException("Failed to access field for building query conditions.", e);
//                     }
//                 });

//         return queryWrapper;
//     }
// }
