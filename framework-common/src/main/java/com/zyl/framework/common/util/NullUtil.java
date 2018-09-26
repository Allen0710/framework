package com.zyl.framework.common.util;

import java.util.Collection;
import java.util.Map;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * null empty util
 * @author zhang
 */
public class NullUtil {

    /**
     * 判断object是否为null或empty
     * @param o
     * @return
     */
    public static boolean isNullOrEmpty(Object o) {
        if (null == o) {
            return true;
        }

        if (o instanceof String) {
            return StringUtils.isEmpty(o);
        }

        if (o instanceof Collection) {
            return CollectionUtils.isEmpty((Collection) o);
        }

        if (o instanceof Map) {
            return CollectionUtils.isEmpty((Map<?, ?>) o);
        }
        return false;
    }

    /**
     * 判断object是否为null或empty
     * @param o
     * @return
     */
    public static boolean isNotNullOrEmpty(Object o) {
        return !isNullOrEmpty(o);
    }
}
