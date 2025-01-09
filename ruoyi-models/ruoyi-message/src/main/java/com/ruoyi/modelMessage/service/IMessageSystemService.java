package com.ruoyi.modelMessage.service;

import java.util.List;

import com.ruoyi.modelMessage.domain.MessageSystem;
import com.ruoyi.modelMessage.domain.MessageTemplate;
import com.ruoyi.modelMessage.domain.vo.SysDeptVo;
import com.ruoyi.modelMessage.domain.vo.SysRoleVo;
import com.ruoyi.modelMessage.domain.vo.SysUserVo;

/**
 * 消息管理Service接口
 * 
 * @author ruoyi
 * @date 2024-12-21
 */
public interface IMessageSystemService 
{
    /**
     * 查询消息管理
     * 
     * @param messageId 消息管理主键
     * @return 消息管理
     */
    public MessageSystem selectMessageSystemByMessageId(Long messageId);

    /**
     * 查询消息管理列表
     * 
     * @param messageSystem 消息管理
     * @return 消息管理集合
     */
    public List<MessageSystem> selectMessageSystemList(MessageSystem messageSystem);

    /**
     * 新增消息管理
     * 
     * @param messageSystem 消息管理
     * @return 结果
     */
    public int insertMessageSystem(MessageSystem messageSystem);

    /**
     * 修改消息管理
     * 
     * @param messageSystem 消息管理
     * @return 结果
     */
    public int updateMessageSystem(MessageSystem messageSystem);

    /**
     * 批量删除消息管理
     * 
     * @param messageIds 需要删除的消息管理主键集合
     * @return 结果
     */
    public int deleteMessageSystemByMessageIds(Long[] messageIds);

    /**
     * 删除消息管理信息
     * 
     * @param messageId 消息管理主键
     * @return 结果
     */
    public int deleteMessageSystemByMessageId(Long messageId);

    //查询系统资源用户信息
     public List<SysUserVo> selectUser();

    //将信息状态未读信息变为已读
    public int updateState(Long messageId);

    //根据发送方式 执行不同操作
    public void processMessageSystem(MessageSystem messageSystem);

    // 根据发送方式过滤用户 (短信或邮箱)
    public List<SysUserVo> getUsersFilteredBySendMode(String filterType);

    //查询角色信息 然后根据角色把消息发给某角色
    public List<SysRoleVo> selectRole();

    //查询部门信息 然后根据部门把消息发给某部门
    public List<SysDeptVo> selectDept();

    /**
     * 根据角色ID获取用户列表。
     *
     * @param roleId 角色ID
     * @return 
     * 
     * */
    public List<SysUserVo> selectUsersByRoleId(Long roleId);

    /**
     * 根据部门ID获取用户列表。
     *
     * @param deptId 部门ID
     * @return 
     * 
     * */
    public List<SysUserVo> getUserNameByDeptId(Long deptId);

    // 查询模版签名
    public List<MessageTemplate> selecTemplates();

    // 批量发送信息
    public int batchInsertMessageSystem(List<MessageSystem> messageSystemList);
}
