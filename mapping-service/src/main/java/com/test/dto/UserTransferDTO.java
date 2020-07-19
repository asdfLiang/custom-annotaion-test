package com.test.dto;

import com.alibaba.fastjson.JSONObject;
import com.mapping.util.AbsTransfer;
import com.test.constant.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserTransferDTO extends AbsTransfer<UserTransferDTO> {

    private Long id;

    private String name;

    @CustomMapping(sourceClassName = "com.test.dto.UserDO", convertMethod = "long2Date")
    @CustomMapping(sourceClassName = "com.test.dto.UserDTO")
    private Date birthday;

    @CustomMapping(sourceClassName = "com.test.dto.UserDO", sourceField = "statusCode", convertMethod = "oldStatus2NewStatus2")
    @CustomMapping(sourceClassName = "com.test.dto.UserDTO", sourceField = "statusValue", convertMethod = "oldStatus2NewStatus")
    private String status;

    private String text;

    private JSONObject bizParams;

    // 状态转换方法
    public String oldStatus2NewStatus(StatusEnum statusValue) {
        return statusValue.name();
    }

    // 状态转换方法
    public String oldStatus2NewStatus2(Integer statusCode) {
        switch (statusCode) {
            case 0:
                return "create";
            case 1:
                return "activate";
            case 2:
                return "abandon";
            default:
                throw new IllegalStateException("statusCode illegal");
        }
    }

    // 日期格式转换
    public Date long2Date(Long time) {
        return new Date(time);
    }
}
