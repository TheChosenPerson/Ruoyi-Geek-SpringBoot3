DROP TABLE IF EXISTS QRTZ_FIRED_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_PAUSED_TRIGGER_GRPS;
DROP TABLE IF EXISTS QRTZ_SCHEDULER_STATE;
DROP TABLE IF EXISTS QRTZ_LOCKS;
DROP TABLE IF EXISTS QRTZ_SIMPLE_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_SIMPROP_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_CRON_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_BLOB_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_JOB_DETAILS;
DROP TABLE IF EXISTS QRTZ_CALENDARS;

-- ----------------------------
-- 1、存储每一个已配置的 jobDetail 的详细信息
-- ----------------------------

CREATE TABLE QRTZ_JOB_DETAILS (
    sched_name           varchar(120)    NOT NULL,
    job_name             varchar(200)    NOT NULL,
    job_group            varchar(200)    NOT NULL,
    description          varchar(250)    NULL,
    job_class_name       varchar(250)    NOT NULL,
    is_durable           varchar(1)      NOT NULL,
    is_nonconcurrent     varchar(1)      NOT NULL,
    is_update_data       varchar(1)      NOT NULL,
    requests_recovery    varchar(1)      NOT NULL,
    job_data             bytea           NULL,
    PRIMARY KEY (sched_name, job_name, job_group)
);

COMMENT ON COLUMN QRTZ_JOB_DETAILS.sched_name IS '调度名称';
COMMENT ON COLUMN QRTZ_JOB_DETAILS.job_name IS '任务名称';
COMMENT ON COLUMN QRTZ_JOB_DETAILS.job_group IS '任务组名';
COMMENT ON COLUMN QRTZ_JOB_DETAILS.description IS '相关介绍';
COMMENT ON COLUMN QRTZ_JOB_DETAILS.job_class_name IS '执行任务类名称';
COMMENT ON COLUMN QRTZ_JOB_DETAILS.is_durable IS '是否持久化';
COMMENT ON COLUMN QRTZ_JOB_DETAILS.is_nonconcurrent IS '是否并发';
COMMENT ON COLUMN QRTZ_JOB_DETAILS.is_update_data IS '是否更新数据';
COMMENT ON COLUMN QRTZ_JOB_DETAILS.requests_recovery IS '是否接受恢复执行';
COMMENT ON COLUMN QRTZ_JOB_DETAILS.job_data IS '存放持久化job对象';

-- ----------------------------
-- 2、 存储已配置的 Trigger 的信息
-- ----------------------------
CREATE TABLE QRTZ_TRIGGERS (
    sched_name           varchar(120)    NOT NULL,
    trigger_name         varchar(200)    NOT NULL,
    trigger_group        varchar(200)    NOT NULL,
    job_name             varchar(200)    NOT NULL,
    job_group            varchar(200)    NOT NULL,
    description          varchar(250)    NULL,
    next_fire_time       bigint          NULL,
    prev_fire_time       bigint          NULL,
    priority             integer         NULL,
    trigger_state        varchar(16)     NOT NULL,
    trigger_type         varchar(8)      NOT NULL,
    start_time           bigint          NOT NULL,
    end_time             bigint          NULL,
    calendar_name        varchar(200)    NULL,
    misfire_instr        smallint        NULL,
    job_data             bytea           NULL,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    FOREIGN KEY (sched_name, job_name, job_group) REFERENCES QRTZ_JOB_DETAILS(sched_name, job_name, job_group)
);

COMMENT ON COLUMN QRTZ_TRIGGERS.sched_name IS '调度名称';
COMMENT ON COLUMN QRTZ_TRIGGERS.trigger_name IS '触发器的名字';
COMMENT ON COLUMN QRTZ_TRIGGERS.trigger_group IS '触发器所属组的名字';
COMMENT ON COLUMN QRTZ_TRIGGERS.job_name IS 'qrtz_job_details表job_name的外键';
COMMENT ON COLUMN QRTZ_TRIGGERS.job_group IS 'qrtz_job_details表job_group的外键';
COMMENT ON COLUMN QRTZ_TRIGGERS.description IS '相关介绍';
COMMENT ON COLUMN QRTZ_TRIGGERS.next_fire_time IS '上一次触发时间（毫秒）';
COMMENT ON COLUMN QRTZ_TRIGGERS.prev_fire_time IS '下一次触发时间（默认为-1表示不触发）';
COMMENT ON COLUMN QRTZ_TRIGGERS.priority IS '优先级';
COMMENT ON COLUMN QRTZ_TRIGGERS.trigger_state IS '触发器状态';
COMMENT ON COLUMN QRTZ_TRIGGERS.trigger_type IS '触发器的类型';
COMMENT ON COLUMN QRTZ_TRIGGERS.start_time IS '开始时间';
COMMENT ON COLUMN QRTZ_TRIGGERS.end_time IS '结束时间';
COMMENT ON COLUMN QRTZ_TRIGGERS.calendar_name IS '日程表名称';
COMMENT ON COLUMN QRTZ_TRIGGERS.misfire_instr IS '补偿执行的策略';
COMMENT ON COLUMN QRTZ_TRIGGERS.job_data IS '存放持久化job对象';

-- ----------------------------
-- 3、 存储简单的 Trigger，包括重复次数，间隔，以及已触发的次数
-- ----------------------------
CREATE TABLE QRTZ_SIMPLE_TRIGGERS (
    sched_name           varchar(120)    NOT NULL,
    trigger_name         varchar(200)    NOT NULL,
    trigger_group        varchar(200)    NOT NULL,
    repeat_count         bigint          NOT NULL,
    repeat_interval      bigint          NOT NULL,
    times_triggered      bigint          NOT NULL,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES QRTZ_TRIGGERS(sched_name, trigger_name, trigger_group)
);

COMMENT ON COLUMN QRTZ_SIMPLE_TRIGGERS.sched_name IS '调度名称';
COMMENT ON COLUMN QRTZ_SIMPLE_TRIGGERS.trigger_name IS 'qrtz_triggers表trigger_name的外键';
COMMENT ON COLUMN QRTZ_SIMPLE_TRIGGERS.trigger_group IS 'qrtz_triggers表trigger_group的外键';
COMMENT ON COLUMN QRTZ_SIMPLE_TRIGGERS.repeat_count IS '重复的次数统计';
COMMENT ON COLUMN QRTZ_SIMPLE_TRIGGERS.repeat_interval IS '重复的间隔时间';
COMMENT ON COLUMN QRTZ_SIMPLE_TRIGGERS.times_triggered IS '已经触发的次数';

-- ----------------------------
-- 4、 存储 Cron Trigger，包括 Cron 表达式和时区信息
-- ---------------------------- 
CREATE TABLE QRTZ_CRON_TRIGGERS (
    sched_name           varchar(120)    NOT NULL,
    trigger_name         varchar(200)    NOT NULL,
    trigger_group        varchar(200)    NOT NULL,
    cron_expression      varchar(200)    NOT NULL,
    time_zone_id         varchar(80)     NULL,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES QRTZ_TRIGGERS(sched_name, trigger_name, trigger_group)
);

COMMENT ON COLUMN QRTZ_CRON_TRIGGERS.sched_name IS '调度名称';
COMMENT ON COLUMN QRTZ_CRON_TRIGGERS.trigger_name IS 'qrtz_triggers表trigger_name的外键';
COMMENT ON COLUMN QRTZ_CRON_TRIGGERS.trigger_group IS 'qrtz_triggers表trigger_group的外键';
COMMENT ON COLUMN QRTZ_CRON_TRIGGERS.cron_expression IS 'cron表达式';
COMMENT ON COLUMN QRTZ_CRON_TRIGGERS.time_zone_id IS '时区';

-- ----------------------------
-- 5、 Trigger 作为 Blob 类型存储(用于 Quartz 用户用 JDBC 创建他们自己定制的 Trigger 类型，JobStore 并不知道如何存储实例的时候)
-- ---------------------------- 
CREATE TABLE QRTZ_BLOB_TRIGGERS (
    sched_name           varchar(120)    NOT NULL,
    trigger_name         varchar(200)    NOT NULL,
    trigger_group        varchar(200)    NOT NULL,
    blob_data            bytea           NULL,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES QRTZ_TRIGGERS(sched_name, trigger_name, trigger_group)
);

COMMENT ON COLUMN QRTZ_BLOB_TRIGGERS.sched_name IS '调度名称';
COMMENT ON COLUMN QRTZ_BLOB_TRIGGERS.trigger_name IS 'qrtz_triggers表trigger_name的外键';
COMMENT ON COLUMN QRTZ_BLOB_TRIGGERS.trigger_group IS 'qrtz_triggers表trigger_group的外键';
COMMENT ON COLUMN QRTZ_BLOB_TRIGGERS.blob_data IS '存放持久化Trigger对象';

-- ----------------------------
-- 6、 以 Blob 类型存储存放日历信息， quartz可配置一个日历来指定一个时间范围
-- ---------------------------- 
CREATE TABLE QRTZ_CALENDARS (
    sched_name           varchar(120)    NOT NULL,
    calendar_name        varchar(200)    NOT NULL,
    calendar             bytea           NOT NULL,
    PRIMARY KEY (sched_name, calendar_name)
);

COMMENT ON COLUMN QRTZ_CALENDARS.sched_name IS '调度名称';
COMMENT ON COLUMN QRTZ_CALENDARS.calendar_name IS '日历名称';
COMMENT ON COLUMN QRTZ_CALENDARS.calendar IS '存放持久化calendar对象';

-- ----------------------------
-- 7、 存储已暂停的 Trigger 组的信息
-- ---------------------------- 
CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS (
    sched_name           varchar(120)    NOT NULL,
    trigger_group        varchar(200)    NOT NULL,
    PRIMARY KEY (sched_name, trigger_group)
);

COMMENT ON COLUMN QRTZ_PAUSED_TRIGGER_GRPS.sched_name IS '调度名称';
COMMENT ON COLUMN QRTZ_PAUSED_TRIGGER_GRPS.trigger_group IS 'qrtz_triggers表trigger_group的外键';

-- ----------------------------
-- 8、 存储与已触发的 Trigger 相关的状态信息，以及相联 Job 的执行信息
-- ---------------------------- 
CREATE TABLE QRTZ_FIRED_TRIGGERS (
    sched_name           varchar(120)    NOT NULL,
    entry_id             varchar(95)     NOT NULL,
    trigger_name         varchar(200)    NOT NULL,
    trigger_group        varchar(200)    NOT NULL,
    instance_name        varchar(200)    NOT NULL,
    fired_time           bigint          NOT NULL,
    sched_time           bigint          NOT NULL,
    priority             integer         NOT NULL,
    state                varchar(16)     NOT NULL,
    job_name             varchar(200)    NULL,
    job_group            varchar(200)    NULL,
    is_nonconcurrent     varchar(1)      NULL,
    requests_recovery    varchar(1)      NULL,
    PRIMARY KEY (sched_name, entry_id)
);

COMMENT ON COLUMN QRTZ_FIRED_TRIGGERS.sched_name IS '调度名称';
COMMENT ON COLUMN QRTZ_FIRED_TRIGGERS.entry_id IS '调度器实例id';
COMMENT ON COLUMN QRTZ_FIRED_TRIGGERS.trigger_name IS 'qrtz_triggers表trigger_name的外键';
COMMENT ON COLUMN QRTZ_FIRED_TRIGGERS.trigger_group IS 'qrtz_triggers表trigger_group的外键';
COMMENT ON COLUMN QRTZ_FIRED_TRIGGERS.instance_name IS '调度器实例名';
COMMENT ON COLUMN QRTZ_FIRED_TRIGGERS.fired_time IS '触发的时间';
COMMENT ON COLUMN QRTZ_FIRED_TRIGGERS.sched_time IS '定时器制定的时间';
COMMENT ON COLUMN QRTZ_FIRED_TRIGGERS.priority IS '优先级';
COMMENT ON COLUMN QRTZ_FIRED_TRIGGERS.state IS '状态';
COMMENT ON COLUMN QRTZ_FIRED_TRIGGERS.job_name IS '任务名称';
COMMENT ON COLUMN QRTZ_FIRED_TRIGGERS.job_group IS '任务组名';
COMMENT ON COLUMN QRTZ_FIRED_TRIGGERS.is_nonconcurrent IS '是否并发';
COMMENT ON COLUMN QRTZ_FIRED_TRIGGERS.requests_recovery IS '是否接受恢复执行';

-- ----------------------------
-- 9、 存储少量的有关 Scheduler 的状态信息，假如是用于集群中，可以看到其他的 Scheduler 实例
-- ---------------------------- 
CREATE TABLE QRTZ_SCHEDULER_STATE (
    sched_name           varchar(120)    NOT NULL,
    instance_name        varchar(200)    NOT NULL,
    last_checkin_time    bigint          NOT NULL,
    checkin_interval     bigint          NOT NULL,
    PRIMARY KEY (sched_name, instance_name)
);

COMMENT ON COLUMN QRTZ_SCHEDULER_STATE.sched_name IS '调度名称';
COMMENT ON COLUMN QRTZ_SCHEDULER_STATE.instance_name IS '实例名称';
COMMENT ON COLUMN QRTZ_SCHEDULER_STATE.last_checkin_time IS '上次检查时间';
COMMENT ON COLUMN QRTZ_SCHEDULER_STATE.checkin_interval IS '检查间隔时间';

-- ----------------------------
-- 10、 存储程序的悲观锁的信息(假如使用了悲观锁)
-- ---------------------------- 
CREATE TABLE QRTZ_LOCKS (
    sched_name           varchar(120)    NOT NULL,
    lock_name            varchar(40)     NOT NULL,
    PRIMARY KEY (sched_name, lock_name)
);

COMMENT ON COLUMN QRTZ_LOCKS.sched_name IS '调度名称';
COMMENT ON COLUMN QRTZ_LOCKS.lock_name IS '悲观锁名称';

-- ----------------------------
-- 11、 Quartz集群实现同步机制的行锁表
-- ---------------------------- 
CREATE TABLE QRTZ_SIMPROP_TRIGGERS (
    sched_name           varchar(120)    NOT NULL,
    trigger_name         varchar(200)    NOT NULL,
    trigger_group        varchar(200)    NOT NULL,
    str_prop_1           varchar(512)    NULL,
    str_prop_2           varchar(512)    NULL,
    str_prop_3           varchar(512)    NULL,
    int_prop_1           int             NULL,
    int_prop_2           int             NULL,
    long_prop_1          bigint          NULL,
    long_prop_2          bigint          NULL,
    dec_prop_1           numeric(13,4)   NULL,
    dec_prop_2           numeric(13,4)   NULL,
    bool_prop_1          varchar(1)      NULL,
    bool_prop_2          varchar(1)      NULL,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES QRTZ_TRIGGERS(sched_name, trigger_name, trigger_group)
);

COMMENT ON COLUMN QRTZ_SIMPROP_TRIGGERS.sched_name IS '调度名称';
COMMENT ON COLUMN QRTZ_SIMPROP_TRIGGERS.trigger_name IS 'qrtz_triggers表trigger_name的外键';
COMMENT ON COLUMN QRTZ_SIMPROP_TRIGGERS.trigger_group IS 'qrtz_triggers表trigger_group的外键';
COMMENT ON COLUMN QRTZ_SIMPROP_TRIGGERS.str_prop_1 IS 'String类型的trigger的第一个参数';
COMMENT ON COLUMN QRTZ_SIMPROP_TRIGGERS.str_prop_2 IS 'String类型的trigger的第二个参数';
COMMENT ON COLUMN QRTZ_SIMPROP_TRIGGERS.str_prop_3 IS 'String类型的trigger的第三个参数';
COMMENT ON COLUMN QRTZ_SIMPROP_TRIGGERS.int_prop_1 IS 'int类型的trigger的第一个参数';
COMMENT ON COLUMN QRTZ_SIMPROP_TRIGGERS.int_prop_2 IS 'int类型的trigger的第二个参数';
COMMENT ON COLUMN QRTZ_SIMPROP_TRIGGERS.long_prop_1 IS 'long类型的trigger的第一个参数';
COMMENT ON COLUMN QRTZ_SIMPROP_TRIGGERS.long_prop_2 IS 'long类型的trigger的第二个参数';
COMMENT ON COLUMN QRTZ_SIMPROP_TRIGGERS.dec_prop_1 IS 'decimal类型的trigger的第一个参数';
COMMENT ON COLUMN QRTZ_SIMPROP_TRIGGERS.dec_prop_2 IS 'decimal类型的trigger的第二个参数';
COMMENT ON COLUMN QRTZ_SIMPROP_TRIGGERS.bool_prop_1 IS 'Boolean类型的trigger的第一个参数';
COMMENT ON COLUMN QRTZ_SIMPROP_TRIGGERS.bool_prop_2 IS 'Boolean类型的trigger的第二个参数';