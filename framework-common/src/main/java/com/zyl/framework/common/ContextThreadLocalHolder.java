package com.zyl.framework.common;

import java.util.HashMap;
import java.util.Map;

/**
 * thread local context 上下文holder
 * @author zhang
 */
public class ContextThreadLocalHolder {
    private static InheritableThreadLocal<Map<String,Object>> HODLER = new InheritableThreadLocal<Map<String,Object>>(){
        @Override
        protected Map<String,Object> childValue(Map<String,Object> parentValue){
            return (null != parentValue && parentValue.size() > 0) ? new HashMap<>(parentValue) : new HashMap<>();
        }
    };

    /**
     * add key value
     * @param key
     * @param value
     */
    public static void put(String key, Object value) {
        HODLER.get().put(key, value);
    }

    /**
     * remove key
     * @param key
     */
    public static void remove(String key) {
        HODLER.get().remove(key);
    }

    /**
     * clean current thread local from thread local map
     */
    public static void reset() {
        HODLER.remove();
    }
}
