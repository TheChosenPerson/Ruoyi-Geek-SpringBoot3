package com.ruoyi.oauth.common.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.oauth.common.domain.OauthUser;
import com.ruoyi.oauth.common.mapper.OauthUserMapper;
import com.ruoyi.oauth.common.service.IOauthUserService;

/**
 * 第三方认证Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-01-18
 */
@Service
public class OauthUserServiceImpl implements IOauthUserService 
{
    @Autowired
    private OauthUserMapper oauthUserMapper;

    /**
     * 查询第三方认证
     * 
     * @param id 第三方认证主键
     * @return 第三方认证
     */
    @Override
    public OauthUser selectOauthUserById(Long id)
    {
        return oauthUserMapper.selectOauthUserById(id);
    }
    @Override
    public OauthUser selectOauthUserByUUID(String uuid)
    {
        return oauthUserMapper.selectOauthUserByUUID(uuid);
    }
    @Override
    public OauthUser selectOauthUserByUserId(Long userId)
    {
        return oauthUserMapper.selectOauthUserByUserId(userId);
    }
    /**
     * 查询第三方认证列表
     * 
     * @param oauthUser 第三方认证
     * @return 第三方认证
     */
    @Override
    public List<OauthUser> selectOauthUserList(OauthUser oauthUser)
    {
        return oauthUserMapper.selectOauthUserList(oauthUser);
    }

    /**
     * 新增第三方认证
     * 
     * @param oauthUser 第三方认证
     * @return 结果
     */
    @Override
    public int insertOauthUser(OauthUser oauthUser)
    {
        return oauthUserMapper.insertOauthUser(oauthUser);
    }

    /**
     * 修改第三方认证
     * 
     * @param oauthUser 第三方认证
     * @return 结果
     */
    @Override
    public int updateOauthUser(OauthUser oauthUser)
    {
        return oauthUserMapper.updateOauthUser(oauthUser);
    }

    /**
     * 批量删除第三方认证
     * 
     * @param ids 需要删除的第三方认证主键
     * @return 结果
     */
    @Override
    public int deleteOauthUserByIds(Long[] ids)
    {
        return oauthUserMapper.deleteOauthUserByIds(ids);
    }

    /**
     * 删除第三方认证信息
     * 
     * @param id 第三方认证主键
     * @return 结果
     */
    @Override
    public int deleteOauthUserById(Long id)
    {
        return oauthUserMapper.deleteOauthUserById(id);
    }
}
