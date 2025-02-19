package com.ruoyi.generator.domain;

import com.ruoyi.common.core.domain.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GenJoinTable extends BaseEntity {

    /** 表编号 */
    private Long tableId;

    private Long mainTableId;

    /** 关联表编号 */
    private Long joinTableId;

    /** 主表别名 */
    private String mainTableAlias;

    /** 关联表别名 */
    private String joinTableAlias;

    /** 主表外键 */
    private String mainTableFk;

    /** 关联表外键 */
    private String joinTableFk;

}
