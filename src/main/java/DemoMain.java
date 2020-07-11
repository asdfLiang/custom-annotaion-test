import com.alibaba.fastjson.JSONObject;
import dto.UserDTO;
import dto.UserTransferDTO;
import enums.StatusEnum;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

public class DemoMain {
    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, NoSuchFieldException {
        UserDTO userDTO = new UserDTO(0L, "alice", new Date(), StatusEnum.activate, null);
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(userDTO));
        userDTO.setBizParams(jsonObject);
        UserTransferDTO userTransferDTO = UserTransferDTO.convertFrom(userDTO);
        System.out.println(userTransferDTO);
    }
}

