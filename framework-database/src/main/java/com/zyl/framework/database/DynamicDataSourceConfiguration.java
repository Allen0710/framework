package com.zyl.framework.database;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * 动态数据源配置
 * @author zhang
 */
@EnableConfigurationProperties
public class DynamicDataSourceConfiguration {
    @Bean(name = "masterDataSourceProperty")
    @ConfigurationProperties("spring.datasource.master")
    @Order(0)
    public DataSourceProperty masterDataSourceProperty() {
        return new DataSourceProperty();
    }

    @Bean(name = "slaveDataSourceProperty")
    @ConfigurationProperties("spring.datasource.slave")
    @Order(1)
    public DataSourceProperty slaveDataSourceProperty() {
        return new DataSourceProperty();
    }

    @Bean
    public DynamicDataSource dynamicDataSource() {
        return new DynamicDataSource();
    }

    @Bean
    @DependsOn("dynamicDataSource")
    public DataSource dataSource() {
        return new DynamicRoutingDataSource();
    }

    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource) {
        DynamicDataSourceTransactionManager dataSourceTransactionManager = new DynamicDataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);
        return dataSourceTransactionManager;
    }

    @Bean
    public DynamicDataSourceAspect dataSourceAspect() {
        return new DynamicDataSourceAspect();
    }
}
