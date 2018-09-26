package com.zyl.framework.common.template;

import org.springframework.web.client.AsyncRestTemplate;

/**
 * 默认clientTemplate实现
 */
public class SimpleAsyncClientTemplate extends AbstractTemplate {

    public SimpleAsyncClientTemplate(AsyncRestTemplate template) {
        setTemplate(template);
    }

    @Override
    void setTemplate(AsyncRestTemplate asyncRestTemplate) {
        this.asyncRestTemplate = asyncRestTemplate == null ? new AsyncRestTemplate() : asyncRestTemplate;
    }
}
