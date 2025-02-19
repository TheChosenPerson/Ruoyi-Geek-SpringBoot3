package com.ruoyi.generator.domain.vo;

import java.util.List;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.generator.domain.GenJoinTable;
import com.ruoyi.generator.domain.GenTable;
import com.ruoyi.generator.domain.GenTableColumn;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

/**
 * 业务表 gen_table
 * 
 * @author ruoyi
 */
@Data
@Setter
@EqualsAndHashCode(callSuper = true)
public class GenTableVo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private GenTable table;

    private List<GenTableColumn> columns;

    private List<GenJoinTable> joins;

    @Valid
    private List<GenTable> joinTables;

    @Valid
    private List<GenTableColumn> joinColumns;

}