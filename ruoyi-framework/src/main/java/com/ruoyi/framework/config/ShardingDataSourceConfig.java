package com.ruoyi.framework.config;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.shardingsphere.api.config.sharding.KeyGeneratorConfiguration;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceBuilder;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.framework.config.properties.DruidProperties;

/**
 * sharding 配置信息
 * 
 * @author ruoyi
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.datasource.druid.sharding", name = "enabled", havingValue = "true")
public class ShardingDataSourceConfig {

    Logger logger = LoggerFactory.getLogger(ShardingDataSourceConfig.class);

    @Bean
    @ConfigurationProperties("spring.datasource.druid.sharding.order1")
    @ConditionalOnProperty(prefix = "spring.datasource.druid.sharding.order1", name = "enabled", havingValue = "true")
    public DataSource order1DataSource(DruidProperties druidProperties) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return druidProperties.dataSource(dataSource);
    }

    @Bean
    @ConfigurationProperties("spring.datasource.druid.sharding.order2")
    @ConditionalOnProperty(prefix = "spring.datasource.druid.sharding.order2", name = "enabled", havingValue = "true")
    public DataSource order2DataSource(DruidProperties druidProperties) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return druidProperties.dataSource(dataSource);
    }

    /**
     * 设置数据源
     *
     * @param targetDataSources 备选数据源集合
     * @param sourceName        数据源名称
     * @param beanName          bean名称
     */
    public void setDataSource(Map<String, DataSource> targetDataSources, String sourceName, String beanName) {
        try {
            DataSource dataSource = SpringUtils.getBean(beanName);
            targetDataSources.put(sourceName, dataSource);
        } catch (Exception e) {
            logger.error("Failed to register a sharding data source:{}", beanName);
        }
    }

    @Bean(name = "shardingDataSource")
    public DataSource shardingDataSource() throws SQLException {
        Map<String, DataSource> dataSourceMap = new HashMap<>();

        // 添加数据源
        setDataSource(dataSourceMap, "order1", "order1DataSource");
        setDataSource(dataSourceMap, "order2", "order2DataSource");

        // 配置分片规则
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        // 表规则配置 示例:
        // 添加order1.sys_order_0,order2.sys_order_0,order1.sys_order_1,order2.sys_order_1
        TableRuleConfiguration orderTableRuleConfig = new TableRuleConfiguration(
                "sys_user", "order$->{1..2}.sys_order_$->{0..1}");
        // 配置分库策略
        // 示例: 根据user_id分库,user_id为单数去order1库,偶数去order2库
        orderTableRuleConfig.setDatabaseShardingStrategyConfig(
                new InlineShardingStrategyConfiguration("user_id", "order$->{user_id % 2 + 1}"));
        // 配置分表策略
        // 示例: 根据order_id分表,order_id为偶数分到sys_order_0表,奇数分到sys_order_1表
        orderTableRuleConfig.setTableShardingStrategyConfig(
                new InlineShardingStrategyConfiguration("order_id", "sys_order_$->{order_id % 2}"));
        // 分布式主键
        orderTableRuleConfig.setKeyGeneratorConfig(new KeyGeneratorConfiguration("SNOWFLAKE", "order_id"));
        shardingRuleConfig.getTableRuleConfigs().add(orderTableRuleConfig);

        // 获取数据源对象
        DataSource dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig,
                getProperties());
        return dataSource;
    }

    /**
     * 系统参数配置
     */
    private Properties getProperties() {
        Properties shardingProperties = new Properties();
        shardingProperties.put("sql.show", true);
        return shardingProperties;
    }
}