-- ----------------------------
-- 18、在线接口表
-- ----------------------------
DROP TABLE IF EXISTS online_mb;

CREATE TABLE online_mb (
    mb_id bigserial PRIMARY KEY,
    tag varchar(255) NULL,
    tag_id varchar(255) NULL,
    parameter_type varchar(255) NULL,
    result_map varchar(255) NULL,
    sql_text varchar(255) NULL,
    path varchar(255) NULL,
    method varchar(255) NULL,
    result_type varchar(255) NULL,
    actuator varchar(255) NULL,
    user_id char(1) NULL,
    dept_id char(1) NULL,
    permission_type varchar(255) NULL,
    permission_value varchar(255) NULL,
    del_flag varchar(10) NOT NULL DEFAULT '0'
);

COMMENT ON COLUMN online_mb.mb_id IS '主键';
COMMENT ON COLUMN online_mb.tag IS '标签名';
COMMENT ON COLUMN online_mb.tag_id IS '标签id';
COMMENT ON COLUMN online_mb.parameter_type IS '参数类型';
COMMENT ON COLUMN online_mb.result_map IS '结果类型';
COMMENT ON COLUMN online_mb.sql_text IS 'sql语句';
COMMENT ON COLUMN online_mb.path IS '请求路径';
COMMENT ON COLUMN online_mb.method IS '请求方式';
COMMENT ON COLUMN online_mb.result_type IS '响应类型';
COMMENT ON COLUMN online_mb.actuator IS '执行器';
COMMENT ON COLUMN online_mb.user_id IS '是否需要userId';
COMMENT ON COLUMN online_mb.dept_id IS '是否需要deptId';
COMMENT ON COLUMN online_mb.permission_type IS '许可类型';
COMMENT ON COLUMN online_mb.permission_value IS '许可值';
COMMENT ON COLUMN online_mb.del_flag IS '删除标志（0代表存在 1代表删除）';

-- 插入菜单数据
SELECT setval('sys_menu_menu_id_seq', max(menu_id)) FROM sys_menu WHERE menu_id < 100;
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
VALUES ('Online', 0, 5, 'onlinedev', NULL, NULL, '', 1, 0, 'M', '0', '0', NULL, 'international', 'admin', CURRENT_TIMESTAMP, '', NULL, '');

-- 获取插入的父菜单ID
DO $$
DECLARE
    parentId bigint;
BEGIN
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
    VALUES ('Online', 0, 5, 'onlinedev', NULL, NULL, '', 1, 0, 'M', '0', '0', NULL, 'international', 'admin', CURRENT_TIMESTAMP, '', NULL, '')
    RETURNING menu_id INTO parentId;

    -- 菜单 SQL
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
    VALUES ('mybatis在线接口', parentId, '1', 'mb', 'online/mb/index', '', 1, 0, 'C', '0', '0', 'online:mb:list', 'code', 'admin', CURRENT_TIMESTAMP, '', NULL, 'mybatis在线接口菜单');

    -- 获取插入的子菜单ID
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) 
    VALUES ('数据库', parentId, 1, 'db', 'online/db/index', NULL, '', 1, 0, 'C', '0', '0', 'admin', 'table', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, '')
    RETURNING menu_id INTO parentId;

    -- 按钮 SQL
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
    VALUES ('mybatis在线接口查询', parentId, '1', '#', '', '', 1, 0, 'F', '0', '0', 'online:mb:query', '#', 'admin', CURRENT_TIMESTAMP, '', NULL, '');

    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
    VALUES ('mybatis在线接口新增', parentId, '2', '#', '', '', 1, 0, 'F', '0', '0', 'online:mb:add', '#', 'admin', CURRENT_TIMESTAMP, '', NULL, '');

    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
    VALUES ('mybatis在线接口修改', parentId, '3', '#', '', '', 1, 0, 'F', '0', '0', 'online:mb:edit', '#', 'admin', CURRENT_TIMESTAMP, '', NULL, '');

    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
    VALUES ('mybatis在线接口删除', parentId, '4', '#', '', '', 1, 0, 'F', '0', '0', 'online:mb:remove', '#', 'admin', CURRENT_TIMESTAMP, '', NULL, '');

    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
    VALUES ('mybatis在线接口导出', parentId, '5', '#', '', '', 1, 0, 'F', '0', '0', 'online:mb:export', '#', 'admin', CURRENT_TIMESTAMP, '', NULL, '');
END $$;

-- 插入字典类型
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark) 
VALUES ('请求方式', 'online_api_method', '0', 'admin', CURRENT_TIMESTAMP, 'admin', CURRENT_TIMESTAMP, NULL);

INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark) 
VALUES ('标签名', 'online_api_tag', '0', 'admin', CURRENT_TIMESTAMP, '', NULL, NULL);

INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark) 
VALUES ('响应类型', 'online_api_result', '0', 'admin', CURRENT_TIMESTAMP, '', NULL, NULL);

INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark) 
VALUES ('执行器', 'online_api_actuator', '0', 'admin', CURRENT_TIMESTAMP, '', NULL, NULL);

-- 插入字典数据
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) 
VALUES (0, 'POST', 'POST', 'online_api_method', NULL, 'default', 'N', '0', 'admin', CURRENT_TIMESTAMP, '', NULL, NULL);

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) 
VALUES (0, 'GET', 'GET', 'online_api_method', NULL, 'default', 'N', '0', 'admin', CURRENT_TIMESTAMP, '', NULL, NULL);

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) 
VALUES (0, 'PUT', 'PUT', 'online_api_method', NULL, 'default', 'N', '0', 'admin', CURRENT_TIMESTAMP, '', NULL, NULL);

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) 
VALUES (0, 'DELETE', 'DELETE', 'online_api_method', NULL, 'default', 'N', '0', 'admin', CURRENT_TIMESTAMP, '', NULL, NULL);

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) 
VALUES (0, 'select', 'select', 'online_api_tag', NULL, 'default', 'N', '0', 'admin', CURRENT_TIMESTAMP, '', NULL, NULL);

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) 
VALUES (0, 'update', 'update', 'online_api_tag', NULL, 'default', 'N', '0', 'admin', CURRENT_TIMESTAMP, '', NULL, NULL);

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) 
VALUES (0, 'insert', 'insert', 'online_api_tag', NULL, 'default', 'N', '0', 'admin', CURRENT_TIMESTAMP, '', NULL, NULL);

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) 
VALUES (0, 'delete', 'delete', 'online_api_tag', NULL, 'default', 'N', '0', 'admin', CURRENT_TIMESTAMP, '', NULL, NULL);

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) 
VALUES (0, 'selectList', 'selectList', 'online_api_actuator', NULL, 'default', 'N', '0', 'admin', CURRENT_TIMESTAMP, '', NULL, NULL);

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) 
VALUES (0, 'insert', 'insert', 'online_api_actuator', NULL, 'default', 'N', '0', 'admin', CURRENT_TIMESTAMP, '', NULL, NULL);

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) 
VALUES (0, 'selectOne', 'selectOne', 'online_api_actuator', NULL, 'default', 'N', '0', 'admin', CURRENT_TIMESTAMP, '', NULL, NULL);

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) 
VALUES (0, 'update', 'update', 'online_api_actuator', NULL, 'default', 'N', '0', 'admin', CURRENT_TIMESTAMP, '', NULL, NULL);

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) 
VALUES (0, 'delete', 'delete', 'online_api_actuator', NULL, 'default', 'N', '0', 'admin', CURRENT_TIMESTAMP, '', NULL, NULL);