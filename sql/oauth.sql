DROP TABLE IF EXISTS oauth_user;
CREATE TABLE oauth_user (
    id INT PRIMARY KEY COMMENT '主键',
    uuid VARCHAR(255) NOT NULL COMMENT '第三方系统的唯一ID，详细解释请参考：名词解释',
    user_id bigint(20)  NOT NULL comment '用户ID',
    source VARCHAR(255) NOT NULL COMMENT '第三方用户来源，可选值：GITHUB、GITEE、QQ，更多请参考：AuthDefaultSource.java(opens new window)',
    access_token VARCHAR(255) NOT NULL COMMENT '用户的授权令牌',
    expire_in INT COMMENT '第三方用户的授权令牌的有效期，部分平台可能没有',
    refresh_token VARCHAR(255) COMMENT '刷新令牌，部分平台可能没有',
    open_id VARCHAR(255) COMMENT '第三方用户的 open id，部分平台可能没有',
    uid VARCHAR(255) COMMENT '第三方用户的 ID，部分平台可能没有',
    access_code VARCHAR(255) COMMENT '个别平台的授权信息，部分平台可能没有',
    union_id VARCHAR(255) COMMENT '第三方用户的 union id，部分平台可能没有',
    scope VARCHAR(255) COMMENT '第三方用户授予的权限，部分平台可能没有',
    token_type VARCHAR(255) COMMENT '个别平台的授权信息，部分平台可能没有',
    id_token VARCHAR(255) COMMENT 'id token，部分平台可能没有',
    mac_algorithm VARCHAR(255) COMMENT '小米平台用户的附带属性，部分平台可能没有',
    mac_key VARCHAR(255) COMMENT '小米平台用户的附带属性，部分平台可能没有',
    code VARCHAR(255) COMMENT '用户的授权code，部分平台可能没有',
    oauth_token VARCHAR(255) COMMENT 'Twitter平台用户的附带属性，部分平台可能没有',
    oauth_token_secret VARCHAR(255) COMMENT 'Twitter平台用户的附带属性，部分平台可能没有'
);