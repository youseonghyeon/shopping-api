package com.shop.shoppingapi.redis.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductPrice {

    private Long productId;
    private BigDecimal originalPrice;
    private Double discountRate;
    private BigDecimal discountedPrice;
    private String productName;
    private String productTitleImage;

    public boolean abnormalData() {
        boolean abnormalFlag = originalPrice == null
                || discountRate == null
                || discountedPrice == null
                || productName == null
                || productTitleImage == null;
        if (abnormalFlag) {
            log.error("ProductPrice is abnormal: {}", this);
        }
        return abnormalFlag;
    }
}
