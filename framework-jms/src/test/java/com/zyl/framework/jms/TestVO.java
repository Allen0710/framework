package com.zyl.framework.jms;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TestVO {
    @JsonProperty("user_id")
    private String userId;

    private String email;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
