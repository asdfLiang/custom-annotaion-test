package com.test.dto;

import com.alibaba.fastjson.JSONObject;
import com.test.constant.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;

    private String name;

    private Date birthday;

    private StatusEnum statusValue;

    private JSONObject bizParams;

    public UserDTO(Long id, String name, Date birthday, StatusEnum statusValue) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.statusValue = statusValue;
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(this));
        this.bizParams = jsonObject;
    }
}
