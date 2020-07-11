package com.test;

import com.test.constant.StatusEnum;
import com.test.dto.UserDTO;
import com.test.dto.UserTransferDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DemoMain {
    public static void main(String[] args) {
        // 测试数据
        List<UserDTO> userDTOList = new ArrayList<UserDTO>(){{
            add(new UserDTO(0L, "alice", new Date(), StatusEnum.activate));
            add(new UserDTO(1L, "bob", new Date(), StatusEnum.create));
            add(new UserDTO(2L, "candy", new Date(), StatusEnum.abandon));
        }};
        //
        List<UserTransferDTO> userTransferDTOS = userDTOList.stream()
                .map(new UserTransferDTO()::convertFrom).collect(Collectors.toList());
        System.out.println(userTransferDTOS);
    }




}
