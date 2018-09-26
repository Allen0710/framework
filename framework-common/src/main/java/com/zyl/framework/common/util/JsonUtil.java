package com.zyl.framework.common.util;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author zhang
 */
public class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ObjectMapper NON_NULL_MAPPER = new ObjectMapper();
    static {
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        NON_NULL_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        NON_NULL_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * 对象转Json，默认输出 null 值
     *
     * @param obj
     * @return
     */
    public static String toJsonString(Object obj) {
        return toJsonString(obj, false);
    }

    /**
     * 对象转json
     *
     * @param obj
     * @param nonNull 标识是否要讲null字段输出，true表示不输出，false表示输出null
     * @return
     */
    public static String toJsonString(Object obj, boolean nonNull) {
        if (obj == null) {
            return null;
        }
        String json;
        try {
            if (nonNull) {
                json = NON_NULL_MAPPER.writeValueAsString(obj);
            } else {
                json = MAPPER.writeValueAsString(obj);
            }
        } catch (Exception e) {
            throw new RuntimeException("JSON 序列化错误", e);
        }
        return json;
    }

    /**
     * 从字符串中解析出 json 对象
     *
     * @param content
     * @param clazz 如果是数组，可以使用 Object[].class
     * @return
     */
    public static <E> E parseObject(String content, Class<E> clazz) {
        if (content == null) {
            return null;
        }
        E obj = null;
        try {
            obj = MAPPER.readValue(content, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("JSON 反序列化错误", e);
        }
        return obj;
    }

    /**
     * 从 json 中解析 List
     *
     * @param content
     * @param listType
     * @return
     */
    public static <E> List<E> parseList(String content, TypeReference<List<E>> listType) {
        if (content == null) {
            return null;
        }
        List<E> obj = null;
        try {
            obj = MAPPER.readValue(content, listType);
        } catch (Exception e) {
            throw new RuntimeException("JSON 反序列化错误", e);
        }
        return obj;
    }

    /**
     * json字符串转换为Map
     *
     * @param content
     * @return
     * @return: Map<String,Object>
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseToMap(String content){
        if(content==null){
            return null;
        }
        Map<String, Object> objMap = null;
        try {
            objMap = MAPPER.readValue(content, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("JSON 反序列化错误", e);
        }
        return objMap;
    }
}
