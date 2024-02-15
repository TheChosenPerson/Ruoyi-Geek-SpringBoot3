DROP TABLE IF EXISTS `online_mb`;

CREATE TABLE `online_mb` (
    `mb_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `tag` varchar(255) NULL COMMENT '标签名',
    `tag_id` varchar(255) NULL COMMENT '标签id',
    `parameterType` varchar(255) NULL COMMENT '参数类型',
    `resultMap` varchar(255) NULL COMMENT '结果类型',
    `sql` varchar(255) NULL COMMENT 'sql语句',
    `path` varchar(255) NULL COMMENT '请求路径',
    `method` varchar(255) NULL COMMENT '请求方式',
    `resultType` varchar(255) NULL COMMENT '响应类型',
    `actuator` varchar(255) NULL COMMENT '执行器',
    PRIMARY KEY (`mb_id`)
) ENGINE = InnoDB  COMMENT = '在线接口';

-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('mybatis在线接口', '3', '1', 'mb', 'tool/online/mb/index', 1, 0, 'C', '0', '0', 'online:mb:list', 'code', 'admin', sysdate(), '', null, 'mybatis在线接口菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('mybatis在线接口查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'online:mb:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('mybatis在线接口新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'online:mb:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('mybatis在线接口修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'online:mb:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('mybatis在线接口删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'online:mb:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('mybatis在线接口导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'online:mb:export',       '#', 'admin', sysdate(), '', null, '');