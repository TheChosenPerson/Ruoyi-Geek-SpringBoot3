package com.ruoyi.online.config;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ruoyi.online.utils.SqlMapper;

@Configuration
public class SqlMapperConfiguration {
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Bean
    public SqlMapper getSqlMapper() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        return new SqlMapper(sqlSession);
    }
}
