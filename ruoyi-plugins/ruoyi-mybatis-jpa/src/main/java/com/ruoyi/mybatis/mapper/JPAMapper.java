package com.ruoyi.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.mybatis.utils.SQLUtil;

public interface JPAMapper<T extends BaseEntity> {
    @SelectProvider(value = SQLUtil.class, method = "selectById")
    public T selectById(T entity);

    @SelectProvider(value = SQLUtil.class, method = "list")
    public List<T> selectList(T entity);

    @InsertProvider(value = SQLUtil.class, method = "insert")
    @Options(useGeneratedKeys = true)
    public int insert(T entity);

    @UpdateProvider(value = SQLUtil.class, method = "update")
    public int update(T entity);

    @DeleteProvider(value = SQLUtil.class, method = "deleteById")
    public int deleteById(T entity);
}
