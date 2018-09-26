package com.zyl.framework.common.util;

import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.Locale;

import org.springframework.util.ClassUtils;

/**
 * @author zhang
 */
public class BeanUtil {
    /**
     * 判断是否是简单值类型.包括：基础数据类型、CharSequence、Number、Date、URL、URI、Locale、Class;
     *
     * @param clazz
     * @return
     */
    public static boolean isSimpleValueType(Class<?> clazz) {
        return (ClassUtils.isPrimitiveOrWrapper(clazz) || clazz.isEnum() || CharSequence.class.isAssignableFrom(clazz)
                        || Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz) || URI.class == clazz
                        || URL.class == clazz || Locale.class == clazz || Class.class == clazz);
    }
}
