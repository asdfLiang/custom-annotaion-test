package dto;

import com.alibaba.fastjson.JSONObject;
import enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;

    private String name;

    private Date birthday;

    private StatusEnum statusValue;

    private JSONObject bizParams;
}
