package com.test.dto;

import com.alibaba.fastjson.JSONObject;
import com.test.constant.StatusEnum;
import com.mapping.util.AbsMapping;

import java.util.Date;

public class UserTransferDTO extends AbsMapping<UserTransferDTO> {

    private Long id;

    private String name;

    @CustomMapping
    private Date birthday;

    @CustomMapping(sourceField = "statusValue", convertMethod = "oldStatus2NewStatus")
    private String status;

    private JSONObject bizParams;

    // 状态转换方法
    public String oldStatus2NewStatus(StatusEnum statusValue) {
        return statusValue.name();
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public JSONObject getBizParams() {
        return bizParams;
    }

    public void setBizParams(JSONObject bizParams) {
        this.bizParams = bizParams;
    }

    @Override
    public String toString() {
        return "UserTransferDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                ", status='" + status + '\'' +
                ", bizParams=" + bizParams +
                '}';
    }

}
