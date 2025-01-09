-- ----------------------------
-- 消息系统
-- ----------------------------
-- 消息表
DROP TABLE IF EXISTS message_system;
CREATE TABLE message_system (
  message_id bigserial PRIMARY KEY,
  message_title varchar(64) DEFAULT NULL,
  create_by varchar(64) DEFAULT NULL,
  create_time timestamp DEFAULT NULL,
  send_mode varchar(100) DEFAULT NULL,
  code varchar(100) DEFAULT NULL,
  message_content text DEFAULT NULL,
  message_recipient varchar(100) DEFAULT NULL,
  message_status varchar(64) DEFAULT NULL,
  message_type varchar(64) DEFAULT NULL,
  update_by varchar(64) DEFAULT NULL,
  update_time timestamp DEFAULT NULL,
  remark varchar(500) DEFAULT NULL
);

COMMENT ON TABLE message_system IS '消息表';
COMMENT ON COLUMN message_system.message_id IS '主键';
COMMENT ON COLUMN message_system.message_title IS '标题';
COMMENT ON COLUMN message_system.create_by IS '创建者';
COMMENT ON COLUMN message_system.create_time IS '创建时间';
COMMENT ON COLUMN message_system.send_mode IS '发送方式(0平台 1手机号 2 邮箱)';
COMMENT ON COLUMN message_system.code IS '号码';
COMMENT ON COLUMN message_system.message_content IS '消息内容';
COMMENT ON COLUMN message_system.message_recipient IS '接收人';
COMMENT ON COLUMN message_system.message_status IS '消息状态(0未读 1已读)';
COMMENT ON COLUMN message_system.message_type IS '消息类型';
COMMENT ON COLUMN message_system.update_by IS '更新者';
COMMENT ON COLUMN message_system.update_time IS '更新时间';
COMMENT ON COLUMN message_system.remark IS '备注';

-- 模版表
DROP TABLE IF EXISTS message_template;
CREATE TABLE message_template (
  template_id bigserial PRIMARY KEY,
  template_name varchar(100) DEFAULT NULL,
  template_code varchar(64) DEFAULT NULL,
  template_type varchar(64) DEFAULT NULL,
  template_content text DEFAULT NULL,
  template_variable text DEFAULT NULL,
  create_by varchar(64) DEFAULT NULL,
  create_time timestamp DEFAULT NULL,
  update_by varchar(64) DEFAULT NULL,
  update_time timestamp DEFAULT NULL,
  remark varchar(500) DEFAULT NULL
);

COMMENT ON TABLE message_template IS '模版表';
COMMENT ON COLUMN message_template.template_id IS '主键';
COMMENT ON COLUMN message_template.template_name IS '模版名称';
COMMENT ON COLUMN message_template.template_code IS '模版CODE';
COMMENT ON COLUMN message_template.template_type IS '模版类型';
COMMENT ON COLUMN message_template.template_content IS '模版内容';
COMMENT ON COLUMN message_template.template_variable IS '变量属性';
COMMENT ON COLUMN message_template.create_by IS '创建者';
COMMENT ON COLUMN message_template.create_time IS '创建时间';
COMMENT ON COLUMN message_template.update_by IS '更新者';
COMMENT ON COLUMN message_template.update_time IS '更新时间';
COMMENT ON COLUMN message_template.remark IS '备注';

-- 变量表
DROP TABLE IF EXISTS message_variable;
CREATE TABLE message_variable (
  variable_id bigserial PRIMARY KEY,
  variable_name varchar(100) DEFAULT NULL,
  variable_type varchar(64) DEFAULT NULL,
  variable_content varchar(100) DEFAULT NULL,
  create_by varchar(64) DEFAULT NULL,
  create_time timestamp DEFAULT NULL,
  update_by varchar(64) DEFAULT NULL,
  update_time timestamp DEFAULT NULL,
  remark varchar(500) DEFAULT NULL
);

COMMENT ON TABLE message_variable IS '变量表';
COMMENT ON COLUMN message_variable.variable_id IS '主键';
COMMENT ON COLUMN message_variable.variable_name IS '变量名称';
COMMENT ON COLUMN message_variable.variable_type IS '变量类型';
COMMENT ON COLUMN message_variable.variable_content IS '变量内容';
COMMENT ON COLUMN message_variable.create_by IS '创建者';
COMMENT ON COLUMN message_variable.create_time IS '创建时间';
COMMENT ON COLUMN message_variable.update_by IS '更新者';
COMMENT ON COLUMN message_variable.update_time IS '更新时间';
COMMENT ON COLUMN message_variable.remark IS '备注';

-- ----------------------------
-- 消息系统
-- ----------------------------
-- 消息表
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('消息系统', 0, 6, 'modelMessage', NULL, NULL, '', 1, 0, 'M', '0', '0', '', 'message', 'admin', '2024-12-31 11:57:29', 'xl', '2025-01-03 15:48:44', '');

DO $$
DECLARE
    parentId INTEGER;
    messageParentId INTEGER;
BEGIN
    SELECT LASTVAL() INTO parentId;
    SELECT LASTVAL() INTO messageParentId;


    -- 消息管理菜单
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
    VALUES ('消息管理', messageParentId, 0, 'messageSystem', 'modelMessage/messageSystem/index', NULL, '', 1, 0, 'C', '0', '0', 'modelMessage:messageSystem:list', '#', 'admin', '2024-12-21 15:00:31', 'admin', '2024-12-31 15:04:49', '消息管理菜单')
    RETURNING menu_id INTO parentId;

    -- 消息管理按钮
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
    VALUES ('消息管理查询', parentId, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'modelMessage:messageSystem:query', '#', 'admin', '2024-12-21 15:00:31', '', NULL, '');
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
    VALUES ('消息管理新增', parentId, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'modelMessage:messageSystem:add', '#', 'admin', '2024-12-21 15:00:31', '', NULL, '');
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
    VALUES ('消息管理修改', parentId, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'modelMessage:messageSystem:edit', '#', 'admin', '2024-12-21 15:00:31', '', NULL, '');
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
    VALUES ('消息管理删除', parentId, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'modelMessage:messageSystem:remove', '#', 'admin', '2024-12-21 15:00:31', '', NULL, '');
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
    VALUES ('消息管理导出', parentId, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'modelMessage:messageSystem:export', '#', 'admin', '2024-12-21 15:00:31', '', NULL, '');

    -- 模版管理菜单
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
    VALUES ('模版管理', messageParentId, 1, 'template', 'modelMessage/template/index', NULL, '', 1, 0, 'C', '0', '0', 'modelMessage:template:list', '#', 'admin', '2024-12-31 14:59:52', '', NULL, '模版管理菜单')
    RETURNING menu_id INTO parentId;

    -- 模版管理按钮
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
    VALUES ('模版管理查询', parentId, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'modelMessage:template:query', '#', 'admin', '2024-12-31 14:59:52', '', NULL, '');
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
    VALUES ('模版管理新增', parentId, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'modelMessage:template:add', '#', 'admin', '2024-12-31 14:59:52', '', NULL, '');
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
    VALUES ('模版管理修改', parentId, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'modelMessage:template:edit', '#', 'admin', '2024-12-31 14:59:52', '', NULL, '');
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
    VALUES ('模版管理删除', parentId, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'modelMessage:template:remove', '#', 'admin', '2024-12-31 14:59:52', '', NULL, '');
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
    VALUES ('模版管理导出', parentId, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'modelMessage:template:export', '#', 'admin', '2024-12-31 14:59:52', '', NULL, '');

    -- 变量管理菜单
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
    VALUES ('变量管理', messageParentId, 2, 'variable', 'modelMessage/variable/index', NULL, '', 1, 0, 'C', '0', '0', 'modelMessage:variable:list', '#', 'admin', '2024-12-31 15:01:50', 'admin', '2024-12-31 15:04:56', '变量管理菜单')
    RETURNING menu_id INTO parentId;

    -- 变量管理按钮
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
    VALUES ('变量管理查询', parentId, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'modelMessage:variable:query', '#', 'admin', '2024-12-31 15:01:50', '', NULL, '');
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
    VALUES ('变量管理新增', parentId, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'modelMessage:variable:add', '#', 'admin', '2024-12-31 15:01:50', '', NULL, '');
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
    VALUES ('变量管理修改', parentId, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'modelMessage:variable:edit', '#', 'admin', '2024-12-31 15:01:50', '', NULL, '');
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
    VALUES ('变量管理删除', parentId, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'modelMessage:variable:remove', '#', 'admin', '2024-12-31 15:01:50', '', NULL, '');
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
    VALUES ('变量管理导出', parentId, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'modelMessage:variable:export', '#', 'admin', '2024-12-31 15:01:50', '', NULL, '');

END $$;

-- 消息系统字典
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (142, 0, '未读', '0', 'message_status', NULL, 'primary', 'N', '0', 'xl', '2024-12-21 15:13:02', '', NULL, NULL);
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (143, 1, '已读', '1', 'message_status', NULL, 'success', 'N', '0', 'xl', '2024-12-21 15:13:15', 'xl', '2024-12-21 15:13:22', NULL);
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (144, 0, '平台', '0', 'send_mode', NULL, 'primary', 'N', '0', 'xl', '2024-12-25 09:40:01', '', NULL, NULL);
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (145, 1, '短信', '1', 'send_mode', NULL, 'success', 'N', '0', 'xl', '2024-12-25 09:40:16', 'xl', '2025-01-01 10:12:07', NULL);
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (146, 2, '邮件', '2', 'send_mode', NULL, 'warning', 'N', '0', 'xl', '2024-12-25 09:40:28', 'xl', '2025-01-01 10:12:14', NULL);
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (147, 0, '验证码', '0', 'template_type', NULL, 'primary', 'N', '0', 'xl', '2025-01-03 09:22:52', '', NULL, NULL);
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (148, 0, '通知', '0', 'message_type', NULL, 'primary', 'N', '0', 'xl', '2025-01-03 15:12:29', '', NULL, NULL);
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (149, 0, '提示', '1', 'message_type', NULL, 'success', 'N', '0', 'xl', '2025-01-03 15:12:41', 'xl', '2025-01-03 15:12:45', NULL);
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (150, 1, '推广', '1', 'template_type', NULL, 'success', 'N', '0', 'xl', '2025-01-03 15:13:15', '', NULL, NULL);