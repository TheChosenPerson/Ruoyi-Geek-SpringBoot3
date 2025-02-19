package com.ruoyi.generator.domain;

import com.ruoyi.common.core.domain.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GenJoinTableColumn extends BaseEntity {

    /** 归属表编号 */
    private Long tableId;

    /** 关联表编号 */
    private Long rightTableId;

    /** 编号 */
    private Long columnId;

    /** 关联表别名 */
    private String rightTableAlias;

}
