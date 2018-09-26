package com.zyl.framework.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.lang.Nullable;

/**
 * 批量request操作warpper
 */
public class BatchRequestWrapper {
    private List<RequestWrapper> wrapper = new ArrayList<>();

    public BatchRequestWrapper(){}

    public void setParams(BaseEnum baseEnum, Map<String,?> entities,Map<String, ?> variables, Map<BaseRequest, ?>
            request) {
        wrapper.add(new RequestWrapper(baseEnum, variables, entities, request));
    }
    public void setParams(BaseEnum baseEnum, Map<String, ?> variables, Map<BaseRequest, ?> request) {
        wrapper.add(new RequestWrapper(baseEnum, variables, null,request));
    }

    public List<RequestWrapper> getWrapper() {
        return wrapper;
    }
    public static class RequestWrapper {
        private BaseEnum baseEnum;
        private Map<String, ?> variables;
        private Map<String,?> entities;
        private Map<BaseRequest, ?> request;

        public RequestWrapper(BaseEnum baseEnum, Map<String, ?> variables, @Nullable Map<String,?> entities,
                Map<BaseRequest, ?> request) {
            this.baseEnum = baseEnum;
            this.variables = variables;
            this.entities = entities;
            this.request = request;
        }

        public BaseEnum getBaseEnum() {
            return baseEnum;
        }

        public void setBaseEnum(BaseEnum baseEnum) {
            this.baseEnum = baseEnum;
        }

        public Map<String, ?> getVariables() {
            return variables;
        }

        public void setVariables(Map<String, ?> variables) {
            this.variables = variables;
        }

        public Map<String, ?> getEntities() {
            return entities;
        }

        public void setEntities(Map<String, ?> entities) {
            this.entities = entities;
        }

        public Map<BaseRequest, ?> getRequest() {
            return request;
        }

        public void setRequest(Map<BaseRequest, ?> request) {
            this.request = request;
        }
    }
}
