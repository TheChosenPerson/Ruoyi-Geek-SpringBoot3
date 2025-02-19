package com.ruoyi.generator.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.generator.domain.GenJoinTable;
import com.ruoyi.generator.mapper.GenJoinTableMapper;

/**
 * 代码生成关联字段Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-02-19
 */
@Service
public class GenJoinTableServiceImpl implements IGenJoinTableService {
    @Autowired
    private GenJoinTableMapper genJoinTableMapper;

    /**
     * 查询代码生成关联字段列表
     * 
     * @param genJoinTable 代码生成关联字段
     * @return 代码生成关联字段
     */
    @Override
    public List<GenJoinTable> selectGenJoinTableList(GenJoinTable genJoinTable) {
        return genJoinTableMapper.selectGenJoinTableList(genJoinTable);
    }

    /**
     * 新增代码生成关联字段
     * 
     * @param genJoinTable 代码生成关联字段
     * @return 结果
     */
    @Override
    public int insertGenJoinTable(GenJoinTable genJoinTable) {
        genJoinTable.setCreateTime(DateUtils.getNowDate());
        return genJoinTableMapper.insertGenJoinTable(genJoinTable);
    }

    /**
     * 修改代码生成关联字段
     * 
     * @param genJoinTable 代码生成关联字段
     * @return 结果
     */
    @Override
    public int updateGenJoinTable(GenJoinTable genJoinTable) {
        genJoinTable.setUpdateTime(DateUtils.getNowDate());
        return genJoinTableMapper.updateGenJoinTable(genJoinTable);
    }

    /**
     * 根据tableId删除字段关联
     */
    public int deleteGenJoinTableByTableId(Long tableId){
        return genJoinTableMapper.deleteGenJoinTableByTableId(tableId);
    }

}
