package com.shop.shoppingapi.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketApplyRequest {
    private Long userId;
    private Integer ticketNumber;
    private String reason;
}
