package com.zyl.framework.database;

/**
 * @author zhang
 * 动态数据源holder
 */
public final class DynamicDataSourceHolder {
    private static final ThreadLocal<DynamicDataSourceGlobal> HOLDER = new ThreadLocal<DynamicDataSourceGlobal>();

    private DynamicDataSourceHolder() {
        //
    }

    public static void putDataSource(DynamicDataSourceGlobal dataSource){
        HOLDER.set(dataSource);
    }

    public static DynamicDataSourceGlobal getDataSource(){
        return HOLDER.get();
    }

    public static void clearDataSource() {
        HOLDER.remove();
    }
}
