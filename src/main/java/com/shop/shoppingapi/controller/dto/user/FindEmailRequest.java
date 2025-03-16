package com.shop.shoppingapi.controller.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindEmailRequest {

    @NotBlank
    private String username;
    @NotBlank
    private String phoneNumber;

}
