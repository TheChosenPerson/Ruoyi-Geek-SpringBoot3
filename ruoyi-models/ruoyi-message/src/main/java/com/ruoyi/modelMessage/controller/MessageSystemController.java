package com.ruoyi.modelMessage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.modelMessage.domain.MessageSystem;
import com.ruoyi.modelMessage.domain.MessageTemplate;
import com.ruoyi.modelMessage.domain.vo.SysUserVo;
import com.ruoyi.modelMessage.service.IMessageSystemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 消息管理Controller
 * 
 * @author ruoyi
 * @date 2024-12-21
 */
@RestController
@RequestMapping("/modelMessage/messageSystem")
@Tag(name = "【消息管理】管理")
public class MessageSystemController extends BaseController
{
    @Autowired
    private IMessageSystemService messageSystemService;

    /**
     * 查询消息管理列表
     */
    @Operation(summary = "查询消息管理列表")
    //@PreAuthorize("@ss.hasPermi('modelMessage:messageSystem:list')")
    @GetMapping("/list")
    public TableDataInfo list(MessageSystem messageSystem)
    {
        startPage();
        messageSystem.setCreateBy(getUsername());
        messageSystem.setMessageRecipient(getUsername());
        List<MessageSystem> list = messageSystemService.selectMessageSystemList(messageSystem);
        return getDataTable(list);
    }

    /**
     * 导出消息管理列表
     */
    @Operation(summary = "导出消息管理列表")
    @PreAuthorize("@ss.hasPermi('modelMessage:messageSystem:export')")
    @Log(title = "消息管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MessageSystem messageSystem)
    {
        List<MessageSystem> list = messageSystemService.selectMessageSystemList(messageSystem);
        ExcelUtil<MessageSystem> util = new ExcelUtil<MessageSystem>(MessageSystem.class);
        util.exportExcel(response, list, "消息管理数据");
    }

    /**
     * 获取消息管理详细信息
     */
    @Operation(summary = "获取消息管理详细信息")
    //@PreAuthorize("@ss.hasPermi('modelMessage:messageSystem:query')")
    @GetMapping(value = "/{messageId}")
    public AjaxResult getInfo(@PathVariable("messageId") Long messageId)
    {
        return success(messageSystemService.selectMessageSystemByMessageId(messageId));
    }

    /**
     * 修改消息管理
     */
    @Operation(summary = "修改消息管理")
    //@PreAuthorize("@ss.hasPermi('modelMessage:messageSystem:edit')")
    @Log(title = "消息管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MessageSystem messageSystem)
    {
        messageSystem.setUpdateBy(getUsername());
        return toAjax(messageSystemService.updateMessageSystem(messageSystem));
    }

    /**
     * 删除消息管理
     */
    @Operation(summary = "删除消息管理")
    //@PreAuthorize("@ss.hasPermi('modelMessage:messageSystem:remove')")
    @Log(title = "消息管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{messageIds}")
    public AjaxResult remove(@PathVariable( name = "messageIds" ) Long[] messageIds) 
    {
        return toAjax(messageSystemService.deleteMessageSystemByMessageIds(messageIds));
    }

    /**
     * 批量发送消息
     */
    @Operation(summary = "发送消息")
    @Log(title = "发送消息", businessType = BusinessType.INSERT)
    @PostMapping
    @Transactional
    public AjaxResult batchAdd(@RequestBody List<MessageSystem> messageSystemList) {
        try {
            for (MessageSystem message : messageSystemList) {
                messageSystemService.processMessageSystem(message);
            }
            if (messageSystemService.batchInsertMessageSystem(messageSystemList) <= 0) {
                throw new ServiceException("消息发送失败！");
            }
            return AjaxResult.success("消息发送成功！");
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return AjaxResult.error("消息发送失败！", e);
        }
    }

    /**
     * 用户点击标题之后已读信息
     */
    @Operation(summary = "用户点击标题调整信息状态")
    @PostMapping("/{messageId}")
    public AjaxResult update(@PathVariable Long messageId){
        try{
            int state= messageSystemService.updateState(messageId);
            return success(state);
        }catch(Exception e){
            e.printStackTrace();
           return error();
        }
    }

    /**
     * 查询平台用户 sendMode 进行过滤。
     */
    @Operation(summary = "查询平台用户")
    @GetMapping("/selectUser")
    public AjaxResult selectUser(@RequestParam(required = false) String sendMode) {
        try {
            List<SysUserVo> list;
            if ("1".equals(sendMode)) { // 手机号
                list = messageSystemService.getUsersFilteredBySendMode("phone");
            } else if ("2".equals(sendMode)) { // 邮箱
                list = messageSystemService.getUsersFilteredBySendMode("email");
            } else {
                list = messageSystemService.selectUser();
            }
            return AjaxResult.success(list);
        } catch (ServiceException e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 查询角色信息
     */
    @Operation(summary = "查询角色")
    @GetMapping("/selectRole")
    public AjaxResult selectRole() {
       return success(messageSystemService.selectRole());
    }

    /**
     * 查询所有部门信息
     */
    @Operation(summary = "查询部门")
    @GetMapping("/selectDept")
    public AjaxResult selectDept() {
       return success(messageSystemService.selectDept());
    }

    /**
     * 根据角色ID获取所有符合条件的用户信息。
     *
     * @param roleId 角色ID
     * @return 用户信息列表
     */
    @Operation(summary = "根据角色ID获取所有符合条件的用户信息")
    @GetMapping("/getUsersByRole/{roleId}")
    public R<List<SysUserVo>> selectUsersByRoleId(@PathVariable Long roleId) {
       List<SysUserVo> users = messageSystemService.selectUsersByRoleId(roleId);
       return R.ok(users);
    }

    /**
     * 根据部门ID获取所有符合条件的用户信息。
     *
     * @param deptId 部门ID
     * @return 用户信息列表
     */
    @Operation(summary = "根据部门ID获取所有符合条件的用户信息")
    @GetMapping("/getUserNameByDeptId/{deptId}")
    public R<List<SysUserVo>> getUserNameByDeptId(@PathVariable Long deptId) {
        List<SysUserVo> depts= messageSystemService.getUserNameByDeptId(deptId);
        return R.ok(depts);
    }

    /**
     * 查询模版签名
     * @return 模版信息列表
     */
    @Operation(summary = "查询模版签名")
    @GetMapping("/selecTemplates")
    public R selecTemplates() {
        List<MessageTemplate> templates= messageSystemService.selecTemplates();
        return R.ok(templates);
    }
}
