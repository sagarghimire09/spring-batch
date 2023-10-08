package com.example.springbatch.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DbConfig {

    @Value("${source.datasource.url}")
    private String sourceUrl;

    @Value("${source.datasource.username}")
    private String sourceUsername;

    @Value("${source.datasource.password}")
    private String sourcePassword;

    @Value("${target.datasource.url}")
    private String targetUrl;

    @Value("${target.datasource.username}")
    private String targetUsername;

    @Value("${target.datasource.password}")
    private String targetPassword;

    @Primary
    @Bean("sourceDataSource")
    public DataSource sourceDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(sourceUrl);
        dataSource.setUsername(sourceUsername);
        dataSource.setPassword(sourcePassword);
        return dataSource;
    }

    @Bean("targetDataSource")
    public DataSource targetDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(targetUrl);
        dataSource.setUsername(targetUsername);
        dataSource.setPassword(targetPassword);
        return dataSource;
    }

}
