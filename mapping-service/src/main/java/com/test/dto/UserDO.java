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
public class UserDO {

    private Long id;

    private String name;

    private Long birthday;

    private Integer statusCode;

    private JSONObject bizParams;

    public UserDO(Long id, String name, Date birthday, Integer statusCode) {
        this.id = id;
        this.name = name;
        this.birthday = birthday.getTime();
        this.statusCode = statusCode;
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(this));
        this.bizParams = jsonObject;
    }

}
