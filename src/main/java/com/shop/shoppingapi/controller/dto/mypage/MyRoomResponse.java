package com.shop.shoppingapi.controller.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyRoomResponse {

    private String username;
    private String role;
    private Boolean loggedIn;
    private int point;

}
