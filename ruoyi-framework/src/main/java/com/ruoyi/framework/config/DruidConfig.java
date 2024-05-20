package com.ruoyi.framework.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import com.alibaba.druid.spring.boot3.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.util.Utils;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.framework.config.properties.DruidProperties;
import com.ruoyi.framework.datasource.DynamicDataSource;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/**
 * druid 配置多数据源
 *
 * @author ruoyi
 */
@Configuration
public class DruidConfig {

    Logger logger = LoggerFactory.getLogger(DruidConfig.class);

    public static final String MASTER = DataSourceType.MASTER.name();

    public static final String SLAVE = DataSourceType.SLAVE.name();

    @Autowired
    private DruidProperties druidProperties;

    @Bean
    @Primary
    @DependsOn({ "transactionManager" })
    @ConfigurationProperties("spring.datasource.druid.master")
    public DataSource masterDataSource(Environment env) {
        String prefix = "spring.datasource.druid.master.";
        return getDataSource(env, prefix, MASTER);
    }

    @Bean
    @ConfigurationProperties("spring.datasource.druid.slave")
    @DependsOn({ "transactionManager" })
    @ConditionalOnProperty(prefix = "spring.datasource.druid.slave", name = "enabled", havingValue = "true")
    public DataSource slaveDataSource(Environment env) {
        String prefix = "spring.datasource.druid.slave.";
        return getDataSource(env, prefix, SLAVE);
    }

    protected DataSource getDataSource(Environment env, String prefix, String dataSourceName) {
        Properties prop = build(env, prefix);
        AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
        ds.setXaDataSourceClassName("com.alibaba.druid.pool.xa.DruidXADataSource");
        // 添加连接池限制
        ds.setMaxPoolSize(50);
        ds.setMinPoolSize(5);
        ds.setBorrowConnectionTimeout(60);
        ds.setUniqueResourceName(dataSourceName);
        ds.setXaProperties(prop);
        return ds;
    }

    protected Properties build(Environment env, String prefix) {
        Properties prop = new Properties();
        prop.put("url", env.getProperty(prefix + "url"));
        prop.put("username", env.getProperty(prefix + "username"));
        prop.put("password", env.getProperty(prefix + "password"));
        prop.put("initialSize", druidProperties.getInitialSize());
        prop.put("minIdle", druidProperties.getMinIdle());
        prop.put("maxActive", druidProperties.getMaxActive());
        prop.put("maxWait", druidProperties.getMaxWait());
        prop.put("timeBetweenEvictionRunsMillis", druidProperties.getTimeBetweenEvictionRunsMillis());
        prop.put("minEvictableIdleTimeMillis", druidProperties.getMinEvictableIdleTimeMillis());
        prop.put("maxEvictableIdleTimeMillis", druidProperties.getMaxEvictableIdleTimeMillis());
        prop.put("validationQuery", druidProperties.getValidationQuery());
        prop.put("testWhileIdle", druidProperties.isTestWhileIdle());
        prop.put("testOnBorrow", druidProperties.isTestOnBorrow());
        prop.put("testOnReturn", druidProperties.isTestOnReturn());
        return prop;
    }

    @Bean(name = "dynamicDataSource")
    @Primary
    public DynamicDataSource dataSource(DataSource masterDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(MASTER, masterDataSource);
        setDataSource(targetDataSources, SLAVE, "slaveDataSource");
        return new DynamicDataSource(masterDataSource, targetDataSources);
    }

    /**
     * 设置数据源
     *
     * @param targetDataSources 备选数据源集合
     * @param sourceName        数据源名称
     * @param beanName          bean名称
     */
    public void setDataSource(Map<Object, Object> targetDataSources, String sourceName, String beanName) {
        try {
            DataSource dataSource = SpringUtils.getBean(beanName);
            targetDataSources.put(sourceName, dataSource);
        } catch (Exception e) {
            logger.error("Failed to register a data source:{}", beanName);
        }
    }

    /**
     * 去除监控页面底部的广告
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    @ConditionalOnProperty(name = "spring.datasource.druid.statViewServlet.enabled", havingValue = "true")
    public FilterRegistrationBean removeDruidFilterRegistrationBean(DruidStatProperties properties) {
        // 获取web监控页面的参数
        DruidStatProperties.StatViewServlet config = properties.getStatViewServlet();
        // 提取common.js的配置路径
        String pattern = config.getUrlPattern() != null ? config.getUrlPattern() : "/druid/*";
        String commonJsPattern = pattern.replaceAll("\\*", "js/common.js");
        final String filePath = "support/http/resources/js/common.js";
        // 创建filter进行过滤
        Filter filter = new Filter() {
            @Override
            public void init(jakarta.servlet.FilterConfig filterConfig) throws ServletException {
            }

            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
                chain.doFilter(request, response);
                // 重置缓冲区，响应头不会被重置
                response.resetBuffer();
                // 获取common.js
                String text = Utils.readFromResource(filePath);
                // 正则替换banner, 除去底部的广告信息
                text = text.replaceAll("<a.*?banner\"></a><br/>", "");
                text = text.replaceAll("powered.*?shrek.wang</a>", "");
                response.getWriter().write(text);
            }

            @Override
            public void destroy() {
            }
        };
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns(commonJsPattern);
        return registrationBean;
    }
}
