package com.shop.shoppingapi.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyRoomResponse {

    private String username;
    private String role;
    private boolean hasLoggedIn;

}
