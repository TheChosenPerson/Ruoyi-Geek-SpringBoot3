@echo off
rem sql的平级目录

rem 设置数据库的用户名
set DB_USER=root
rem 设置数据库的密码
set DB_PASSWORD=123456
rem 设置数据库的名称
set DB_NAME=ry

rem 可以将你不需要的数据库注释掉
rem 创建数据库
echo Executing create_database.sql
mysql -u%DB_USER% -p%DB_PASSWORD% -D %DB_NAME% < sql\create_database.sql

rem 创建基础数据表
echo Executing ry_20230223.sql
mysql -u%DB_USER% -p%DB_PASSWORD% -D %DB_NAME% < sql\ry_20230223.sql

rem 创建代码生成器的数据表
echo Executing gen.sql
mysql -u%DB_USER% -p%DB_PASSWORD% -D %DB_NAME% < sql\gen.sql

rem 创建定时任务的数据表
echo Executing quartz.sql
mysql -u%DB_USER% -p%DB_PASSWORD% -D %DB_NAME% < sql\quartz.sql

rem 创建增强认证模块的数据表
echo Executing oauth.sql
mysql -u%DB_USER% -p%DB_PASSWORD% -D %DB_NAME% < sql\auth.sql

rem 创建支付模块的数据表
echo Executing pay.sql
mysql -u%DB_USER% -p%DB_PASSWORD% -D %DB_NAME% < sql\pay.sql

rem 创建在线开发模块的数据表
echo Executing online.sql
mysql -u%DB_USER% -p%DB_PASSWORD% -D %DB_NAME% < sql\online.sql
