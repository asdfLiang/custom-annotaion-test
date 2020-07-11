package com.test.dto;

import com.alibaba.fastjson.JSONObject;
import com.test.constant.StatusEnum;

import java.util.Date;

public class UserDTO {

    private Long id;

    private String name;

    private Date birthday;

    private StatusEnum statusValue;

    private JSONObject bizParams;

    public UserDTO() {
    }

    public UserDTO(Long id, String name, Date birthday, StatusEnum statusValue) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.statusValue = statusValue;
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(this));
        this.bizParams = jsonObject;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public StatusEnum getStatusValue() {
        return statusValue;
    }

    public void setStatusValue(StatusEnum statusValue) {
        this.statusValue = statusValue;
    }

    public JSONObject getBizParams() {
        return bizParams;
    }

    public void setBizParams(JSONObject bizParams) {
        this.bizParams = bizParams;
    }
}
