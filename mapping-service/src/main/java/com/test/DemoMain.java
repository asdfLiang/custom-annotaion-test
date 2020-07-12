package com.test;

import com.test.constant.StatusEnum;
import com.test.dto.UserDO;
import com.test.dto.UserDTO;
import com.test.dto.UserTransferDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DemoMain {
    public static void main(String[] args) {
        // mock数据
        List<UserDTO> userDTOList = new ArrayList<UserDTO>() {{
            add(new UserDTO(0L, "alice", new Date(), StatusEnum.activate));
            add(new UserDTO(1L, "bob", new Date(), StatusEnum.create));
            add(new UserDTO(2L, "candy", new Date(), StatusEnum.abandon));
        }};
        // 转换
        List<UserTransferDTO> userTransferDTOS = userDTOList.stream()
                .map(new UserTransferDTO()::convertFrom).collect(Collectors.toList());
        // 输出
        userTransferDTOS.forEach(System.out::println);

        UserTransferDTO david = new UserTransferDTO().convertFrom(new UserDO(3L, "david", new Date(), 1));
        System.out.println(david);
    }


}
