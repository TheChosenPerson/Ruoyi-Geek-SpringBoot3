package com.ruoyi.generator.domain;

import java.util.List;

import com.ruoyi.common.core.domain.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GenJoinTable extends BaseEntity {

    /** 表编号 */
    private Long tableId;

    private Long leftTableId;

    /** 关联表编号 */
    private Long rightTableId;

    /** 新引入的表 */
    private Long newTableId;

    /** 主表别名 */
    private String leftTableAlias;

    /** 关联表别名 */
    private String rightTableAlias;

    /** 主表外键 */
    private Long leftTableFk;

    /** 关联表外键 */
    private Long rightTableFk;

    /** 连接类型 */
    private String joinType;

    /** 关联字段 */
    private List<String> joinColumns;

    private Long orderNum;

}
