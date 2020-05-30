import com.alibaba.fastjson.JSONObject;
import dto.UserDTO;
import dto.UserTransferDTO;
import enums.StatusEnum;

import java.util.Date;

public class DemoMain {
    public static void main(String[] args) {
        UserDTO userDTO = new UserDTO(0L, "alice", new Date(), StatusEnum.activate, new JSONObject());
        UserTransferDTO userTransferDTO = UserTransferDTO.convertFrom(userDTO);
    }
}

