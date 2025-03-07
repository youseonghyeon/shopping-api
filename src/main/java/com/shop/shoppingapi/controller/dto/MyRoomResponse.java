package com.shop.shoppingapi.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MyRoomResponse {

    private String username;
    private String role;
    private Boolean loggedIn;
    private BigDecimal point;

}
