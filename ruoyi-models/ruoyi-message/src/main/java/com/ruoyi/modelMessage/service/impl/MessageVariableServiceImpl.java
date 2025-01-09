package com.ruoyi.modelMessage.service.impl;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aliyun.oss.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.modelMessage.domain.MessageVariable;
import com.ruoyi.modelMessage.mapper.MessageVariableMapper;
import com.ruoyi.modelMessage.service.IMessageVariableService;

/**
 * 变量管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-12-31
 */
@Service
public class MessageVariableServiceImpl implements IMessageVariableService 
{
    private static final int CODE_LENGTH = 6;
    
    @Autowired
    private MessageVariableMapper messageVariableMapper;

    /**
     * 查询变量管理
     * 
     * @param variableId 变量管理主键
     * @return 变量管理
     */
    @Override
    public MessageVariable selectMessageVariableByVariableId(Long variableId)
    {
        return messageVariableMapper.selectMessageVariableByVariableId(variableId);
    }

    /**
     * 查询变量管理列表
     * 
     * @param messageVariable 变量管理
     * @return 变量管理
     */
    @Override
    public List<MessageVariable> selectMessageVariableList(MessageVariable messageVariable)
    {
        return messageVariableMapper.selectMessageVariableList(messageVariable);
    }

    /**
     * 新增变量管理
     * 
     * @param messageVariable 变量管理
     * @return 结果
     */
    @Override
    public int insertMessageVariable(MessageVariable messageVariable)
    {
        messageVariable.setCreateBy(SecurityUtils.getUsername());
        messageVariable.setCreateTime(DateUtils.getNowDate());
        messageVariable.setUpdateBy(SecurityUtils.getUsername());
        messageVariable.setUpdateTime(DateUtils.getNowDate());
        return messageVariableMapper.insertMessageVariable(messageVariable);
    }

    /**
     * 修改变量管理
     * 
     * @param messageVariable 变量管理
     * @return 结果
     */
    @Override
    public int updateMessageVariable(MessageVariable messageVariable)
    {
        messageVariable.setUpdateBy(SecurityUtils.getUsername());
        messageVariable.setUpdateTime(DateUtils.getNowDate());
        return messageVariableMapper.updateMessageVariable(messageVariable);
    }

    /**
     * 批量删除变量管理
     * 
     * @param variableIds 需要删除的变量管理主键
     * @return 结果
     */
    @Override
    public int deleteMessageVariableByVariableIds(Long[] variableIds)
    {
        return messageVariableMapper.deleteMessageVariableByVariableIds(variableIds);
    }

    /**
     * 删除变量管理信息
     * 
     * @param variableId 变量管理主键
     * @return 结果
     */
    @Override
    public int deleteMessageVariableByVariableId(Long variableId)
    {
        return messageVariableMapper.deleteMessageVariableByVariableId(variableId);
    }

     //查询变量
    @Override
    public List<MessageVariable> selectMessageVariable() {
       return messageVariableMapper.selectMessageVariable();
    }

    //删除变量之前检查一下有没有模版使用
    @Override
    public boolean selectTemplateByVariableId(String templateVariable) {
        return messageVariableMapper.selectTemplateByVariableId(templateVariable);
    }

    //根据类型生成不同的变量内容
    @Override
    public String generateVariableContent(String variableType) {
        switch (variableType) {
            case "time":
                return DateUtils.dateTimeNow("HH:mm:ss");
            case "date":
                return DateUtils.dateTimeNow("yyyy-MM-dd");
            case "datetime":
                return DateUtils.dateTimeNow("yyyy-MM-dd HH:mm:ss");
            case "addresser":
                return SecurityUtils.getUsername();
            case "recipients":
                return SecurityUtils.getUsername(); 
            case "RandomnDigits":
                return RandomStringUtils.randomNumeric(CODE_LENGTH);
            case "RandomnCharacters":
                return RandomStringUtils.randomAlphabetic(CODE_LENGTH);
            case "RandomN-digitLetters":
                return RandomStringUtils.randomAlphanumeric(CODE_LENGTH);
            default:
                throw new ServiceException("不明确的类型 " + variableType);
        }
    }
}
