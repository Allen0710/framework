package com.zyl.framework.common;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;

import com.google.common.base.Joiner;
import com.zyl.framework.common.util.BeanUtil;
import com.zyl.framework.common.util.JsonUtil;

/**
 * 由于Spring默认的SimpleKeyGenerator是不会将函数名组合进key中的，此处自定义KeyGenerator
 *
 * @author zhang
 */
public class CacheKeyGenerator implements KeyGenerator {
    private final static int NO_PARAM_KEY = 0;
    /**
     * key前缀，用于区分不同项目的缓存，建议每个项目单独设置
     * 默认main-server=ms
     */
    private String keyPrefix = "ms";
    private static final char SP = ':';

    @Override
    public Object generate(Object target, Method method, Object... params) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(Joiner.on(SP).join(keyPrefix, target.getClass().getSimpleName(), method.getName()));
        if (params.length > 0) {
            // 参数值
            for (Object object : params) {
                if (BeanUtil.isSimpleValueType(object.getClass())) {
                    strBuilder.append(object);
                } else {
                    strBuilder.append(JsonUtil.toJsonString(object).hashCode());
                }
            }
        } else {
            strBuilder.append(NO_PARAM_KEY);
        }
        return strBuilder.toString();
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }
}
