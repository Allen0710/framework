package com.zyl.framework.database;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author zhang
 *  Desc: 动态数据源实现读写分离
 */
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {
    private AtomicLong counter = new AtomicLong(0);
    private static final Long MAX_POOL = Long.MAX_VALUE;
    private final Lock lock = new ReentrantLock();

    @Autowired
    private DynamicDataSource dynamicDataSource;

    private List<DataSource> slaveDataSources;

    @Override
    public void afterPropertiesSet() {
        DataSource masterDataSource = dynamicDataSource.getMasterDataSource();
        this.slaveDataSources = dynamicDataSource.getSlaveDataSources();
        if (masterDataSource == null) {
            throw new IllegalArgumentException("Property 'masterDataSource' is required");
        }
        setDefaultTargetDataSource(masterDataSource);
        Map<Object, Object> targetDataSources = new HashMap<>((null != slaveDataSources && slaveDataSources
                .size() > 0) ? slaveDataSources.size() + 1 : 1);
        targetDataSources.put(DynamicDataSourceGlobal.WRITE.name(), masterDataSource);
        if(slaveDataSources != null && slaveDataSources.size() > 0) {
            for (int i = 0; i < slaveDataSources.size(); i++) {
                targetDataSources.put(DynamicDataSourceGlobal.READ.name() + i, slaveDataSources.get(i));
            }
        }
        setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        DynamicDataSourceGlobal dynamicDataSourceGlobal = DynamicDataSourceHolder.getDataSource();
        if(dynamicDataSourceGlobal == null || dynamicDataSourceGlobal == DynamicDataSourceGlobal.WRITE
                || null == slaveDataSources || slaveDataSources.size() <= 0) {
            return DynamicDataSourceGlobal.WRITE.name();
        } else {
            long currValue = counter.incrementAndGet();
            if((currValue + 1) >= MAX_POOL) {
                try {
                    lock.lock();
                    if((currValue + 1) >= MAX_POOL) {
                        counter.set(0);
                    }
                } finally {
                    lock.unlock();
                }
            }
            int index = (int) (currValue % slaveDataSources.size());
            return DynamicDataSourceGlobal.READ.name() + index;
        }
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
