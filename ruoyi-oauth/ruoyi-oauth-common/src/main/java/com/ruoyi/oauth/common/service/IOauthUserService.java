package com.ruoyi.oauth.common.service;

import java.util.List;

import com.ruoyi.oauth.common.domain.OauthUser;

/**
 * 第三方认证Service接口
 * 
 * @author ruoyi
 * @date 2024-01-18
 */
public interface IOauthUserService 
{
    /**
     * 查询第三方认证
     * 
     * @param id 第三方认证主键
     * @return 第三方认证
     */
    public OauthUser selectOauthUserById(Long id);
    public OauthUser selectOauthUserByUUID(String uuid);
    public OauthUser selectOauthUserByUserId(Long userId);
    

    /**
     * 查询第三方认证列表
     * 
     * @param oauthUser 第三方认证
     * @return 第三方认证集合
     */
    public List<OauthUser> selectOauthUserList(OauthUser oauthUser);

    /**
     * 新增第三方认证
     * 
     * @param oauthUser 第三方认证
     * @return 结果
     */
    public int insertOauthUser(OauthUser oauthUser);

    /**
     * 修改第三方认证
     * 
     * @param oauthUser 第三方认证
     * @return 结果
     */
    public int updateOauthUser(OauthUser oauthUser);

    /**
     * 批量删除第三方认证
     * 
     * @param ids 需要删除的第三方认证主键集合
     * @return 结果
     */
    public int deleteOauthUserByIds(Long[] ids);

    /**
     * 删除第三方认证信息
     * 
     * @param id 第三方认证主键
     * @return 结果
     */
    public int deleteOauthUserById(Long id);
}
