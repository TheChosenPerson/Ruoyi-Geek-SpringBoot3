package com.ruoyi.framework.datasource;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.ruoyi.common.service.datasource.CreateDataSource;
import com.ruoyi.framework.config.DynamicDataSourceProperties;

@Configuration
public class DataSourceManagement implements InitializingBean {
    private Map<String, DataSource> targetDataSources = new HashMap<>();

    @Autowired
    private DynamicDataSourceProperties dataSourceProperties;

    @Autowired
    private CreateDataSource c;

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

    @Override
    public void afterPropertiesSet() throws Exception {
        dataSourceProperties.getDatasource()
                .forEach((name, props) -> this.putDataSource(name, c.createDataSource(name, props)));
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
