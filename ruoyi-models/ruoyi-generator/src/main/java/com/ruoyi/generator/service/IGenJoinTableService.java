package com.ruoyi.generator.service;

import java.util.List;

import com.ruoyi.generator.domain.GenJoinTable;

/**
 * 代码生成关联字段Service接口
 * 
 * @author ruoyi
 * @date 2025-02-19
 */
public interface IGenJoinTableService {
    /**
     * 查询代码生成关联字段列表
     * 
     * @param genJoinTable 代码生成关联字段
     * @return 代码生成关联字段集合
     */
    public List<GenJoinTable> selectGenJoinTableList(GenJoinTable genJoinTable);

    /**
     * 新增代码生成关联字段
     * 
     * @param genJoinTable 代码生成关联字段
     * @return 结果
     */
    public int insertGenJoinTable(GenJoinTable genJoinTable);

    /**
     * 修改代码生成关联字段
     * 
     * @param genJoinTable 代码生成关联字段
     * @return 结果
     */
    public int updateGenJoinTable(GenJoinTable genJoinTable);

    /**
     * 根据tableId删除字段关联
     */
    public int deleteGenJoinTableByTableId(Long tableId);
}
