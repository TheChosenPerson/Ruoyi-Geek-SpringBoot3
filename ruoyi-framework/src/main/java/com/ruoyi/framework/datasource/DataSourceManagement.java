package com.ruoyi.framework.datasource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.CommonDataSource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.ruoyi.common.service.datasource.AfterCreateDataSource;
import com.ruoyi.common.service.datasource.CreateDataSource;
import com.ruoyi.framework.config.DynamicDataSourceProperties;

@Configuration
public class DataSourceManagement implements InitializingBean {
    protected final Logger logger = LoggerFactory.getLogger(DataSourceManagement.class);
    private Map<String, DataSource> targetDataSources = new HashMap<>();

    @Autowired
    private DynamicDataSourceProperties dataSourceProperties;

    @Autowired
    private CreateDataSource c;

    @Autowired(required = false)
    private AfterCreateDataSource afterCreateDataSource;

    @Bean(name = "dynamicDataSource")
    @Primary
    public DynamicDataSource dataSource() {
        Map<Object, Object> objectMap = new HashMap<>();
        Map<String, DataSource> targetDataSources = this.getDataSourcesMap();
        for (Map.Entry<String, DataSource> entry : targetDataSources.entrySet()) {
            objectMap.put(entry.getKey(), entry.getValue());
        }
        return new DynamicDataSource(targetDataSources.get(dataSourceProperties.getPrimary()), objectMap);
    }

    public void validateDataSource(DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            String validationQuery = "SELECT 1";
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(validationQuery)) {
                if (!(rs.next() && rs.getInt(1) == 1)) {
                    throw new RuntimeException("数据源连接验证失败：查询结果不正确");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("数据源连接验证失败", e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        dataSourceProperties.getDatasource()
                .forEach((name, props) -> {
                    Properties properties = dataSourceProperties.build(props);
                    CommonDataSource commonDataSource = c.createDataSource(name, properties);
                    if (afterCreateDataSource != null) {
                        afterCreateDataSource.afterCreateDataSource(name, properties, commonDataSource);
                    }
                    DataSource dataSource = (DataSource) commonDataSource;
                    logger.info("数据源：{} 校验中.......", name);
                    // 计时
                    long start = System.currentTimeMillis();
                    validateDataSource(dataSource);
                    logger.info("数据源：{} 链接成功，耗时：{}ms", name, System.currentTimeMillis() - start);
                    this.putDataSource(name, dataSource);
                });
    }

    public DataSource getDataSource(String name) {
        return targetDataSources.get(name);
    }

    public Collection<DataSource> getDataSources() {
        return targetDataSources.values();
    }

    public Map<String, DataSource> getDataSourcesMap() {
        return targetDataSources;
    }

    public void putDataSource(String name, DataSource dataSource) {
        targetDataSources.put(name, dataSource);
    }
}
