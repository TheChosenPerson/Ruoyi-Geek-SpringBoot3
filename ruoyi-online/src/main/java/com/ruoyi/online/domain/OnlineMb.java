package com.ruoyi.online.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * mybatis在线接口对象 online_mb
 * 
 * @author ruoyi
 * @date 2024-01-26
 */
@Schema(description = "mybatis在线接口对象")
public class OnlineMb extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 主键 */
    @Schema(defaultValue = "主键")
    private Long mbId;

    


    /** 标签名 */
    @Schema(defaultValue = "标签名")
    @Excel(name = "标签名")
    private String tag;

    


    /** 标签id */
    @Schema(defaultValue = "标签id")
    @Excel(name = "标签id")
    private String tagId;

    


    /** 参数类型 */
    @Schema(defaultValue = "参数类型")
    @Excel(name = "参数类型")
    private String parameterType;

    


    /** 结果类型 */
    @Schema(defaultValue = "结果类型")
    @Excel(name = "结果类型")
    private String resultMap;

    


    /** sql语句 */
    @Schema(defaultValue = "sql语句")
    @Excel(name = "sql语句")
    private String sql;

    


    /** 请求路径 */
    @Schema(defaultValue = "请求路径")
    @Excel(name = "请求路径")
    private String path;

    


    /** 请求方式 */
    @Schema(defaultValue = "请求方式")
    @Excel(name = "请求方式")
    private String method;

    


    /** 响应类型 */
    @Schema(defaultValue = "响应类型")
    @Excel(name = "响应类型")
    private String resultType;

    


    /** 执行器 */
    @Schema(defaultValue = "执行器")
    @Excel(name = "执行器")
    private String actuator;

    

    public void setMbId(Long mbId) 
    {
        this.mbId = mbId;
    }

    public Long getMbId() 
    {
        return mbId;
    }


    public void setTag(String tag) 
    {
        this.tag = tag;
    }

    public String getTag() 
    {
        return tag;
    }


    public void setTagId(String tagId) 
    {
        this.tagId = tagId;
    }

    public String getTagId() 
    {
        return tagId;
    }


    public void setParameterType(String parameterType) 
    {
        this.parameterType = parameterType;
    }

    public String getParameterType() 
    {
        return parameterType;
    }


    public void setResultMap(String resultMap) 
    {
        this.resultMap = resultMap;
    }

    public String getResultMap() 
    {
        return resultMap;
    }


    public void setSql(String sql) 
    {
        this.sql = sql;
    }

    public String getSql() 
    {
        return sql;
    }


    public void setPath(String path) 
    {
        this.path = path;
    }

    public String getPath() 
    {
        return path;
    }


    public void setMethod(String method) 
    {
        this.method = method;
    }

    public String getMethod() 
    {
        return method;
    }


    public void setResultType(String resultType) 
    {
        this.resultType = resultType;
    }

    public String getResultType() 
    {
        return resultType;
    }


    public void setActuator(String actuator) 
    {
        this.actuator = actuator;
    }

    public String getActuator() 
    {
        return actuator;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("mbId", getMbId())
            .append("tag", getTag())
            .append("tagId", getTagId())
            .append("parameterType", getParameterType())
            .append("resultMap", getResultMap())
            .append("sql", getSql())
            .append("path", getPath())
            .append("method", getMethod())
            .append("resultType", getResultType())
            .append("actuator", getActuator())
            .toString();
    }
}