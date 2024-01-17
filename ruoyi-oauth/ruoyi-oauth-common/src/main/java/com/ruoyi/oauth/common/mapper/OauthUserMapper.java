package com.ruoyi.oauth.common.mapper;

import java.util.List;

import com.ruoyi.oauth.common.domain.OauthUser;

/**
 * 第三方认证Mapper接口
 * 
 * @author ruoyi
 * @date 2024-01-18
 */
public interface OauthUserMapper {
    /**
     * 查询第三方认证
     * 
     * @param id 第三方认证主键
     * @return 第三方认证
     */
    public OauthUser selectOauthUserById(Long id);
    public OauthUser selectOauthUserByUserId(Long userId);

    /**
     * 查询第三方认证
     * 钉钉、抖音：uuid 为用户的 unionid
     * 微信公众平台登录、京东、酷家乐、美团：uuid 为用户的 openId
     * 微信开放平台登录、QQ：uuid 为用户的 openId，平台支持获取unionid， unionid 在 AuthToken
     * 中（如果支持），在登录完成后，可以通过 response.getData().getToken().getUnionId() 获取
     * Google：uuid 为用户的 sub，sub为Google的所有账户体系中用户唯一的身份标识符，详见：OpenID Connect
     * 
     * @param uuid
     * @return
     */
    public OauthUser selectOauthUserByUUID(String uuid);

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
     * 删除第三方认证
     * 
     * @param id 第三方认证主键
     * @return 结果
     */
    public int deleteOauthUserById(Long id);

    /**
     * 批量删除第三方认证
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOauthUserByIds(Long[] ids);
}
