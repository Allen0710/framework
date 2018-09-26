package com.zyl.framework.database;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * @author zhang
 */
public class DynamicDataSource implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(DynamicDatabaseInterceptor.class);

    @Autowired
    private DataSourceProperty masterDataSourceProperty;

    @Autowired
    private DataSourceProperty slaveDataSourceProperty;

    private DataSource masterDataSource;

    private List<DataSource> slaveDataSources;

    public DataSource getMasterDataSource() {
        return masterDataSource;
    }

    public List<DataSource> getSlaveDataSources() {
        return slaveDataSources;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.masterDataSource = buildDataSource(masterDataSourceProperty);
        if (slaveDataSourceProperty.getUrl() != null) {
            String[] urls = slaveDataSourceProperty.getUrl().split(",");
            this.slaveDataSources = new ArrayList<>(urls.length);
            for(String url : urls) {
                // 更新DataSourceProperty的url属性
                slaveDataSourceProperty.setUrl(url);
                slaveDataSources.add(buildDataSource(slaveDataSourceProperty));
            }
        }
    }

    /**
     * 构造数据源，默认为Druid数据源
     * @param property
     *
     * @return
     */
    private DataSource buildDataSource(DataSourceProperty property) throws Exception{
        Class<? extends DataSource> type = null;
        try {
            if (property.getType() != null && property.getType().length() > 0) {
                type = (Class<? extends DataSource>) Class.forName(property.getType());
            } else {
                type = (Class<? extends DataSource>) Class.forName("com.alibaba.druid.pool.DruidDataSource");
            }
            DataSource dataSource = DataSourceBuilder.create().url(property.getUrl()).username(property.getUsername())
                    .password(property.getPassword()).driverClassName(property.getDriverClassName())
                    .type(type).build();
            if (dataSource instanceof DruidDataSource) {
                DruidDataSource druidDataSource = (DruidDataSource) dataSource;
                druidDataSource.setMaxActive(property.getMaxActive());
                druidDataSource.setInitialSize(property.getInitialSize());
                druidDataSource.setMinIdle(property.getMinIdle());
                druidDataSource.setTestOnBorrow(property.getTestOnBorrow());
                druidDataSource.setTestOnReturn(property.getTestOnReturn());
                druidDataSource.setTestWhileIdle(property.getTestWhileIdle());
                druidDataSource.setValidationQuery(property.getValidationQuery());
                druidDataSource.setMinEvictableIdleTimeMillis(property.getMinEvictableIdleTimeMillis());
                druidDataSource.setTimeBetweenEvictionRunsMillis(property.getTimeBetweenEvictionRunsMillis());
                druidDataSource.setKeepAlive(property.getKeepAlive());
                druidDataSource.setFilters(property.getFilters());
                druidDataSource.setPoolPreparedStatements(property.getPoolPreparedStatements());
            }
            return dataSource;
        } catch (Exception e) {
            logger.error("build data source exception", e);
            throw e;
        }
    }
}
