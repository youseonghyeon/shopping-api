package com.shop.shoppingapi.entity.converter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidateUtils {

    /**
     * 필드의 값이 null 또는 빈 값인지, 0보다 작은지 검증합니다.
     * support type: String, BigDecimal, Integer
     * @param fieldName 필드명
     * @param value     값
     */
    private static void validatePositiveOrHasText(String fieldName, Object value) {
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException(fieldName + " 값이 null일 수 없습니다.");
        }
        if (value instanceof String && !StringUtils.hasText((String) value)) {
            throw new IllegalArgumentException(fieldName + " 값이 비어 있을 수 없습니다.");
        }
        if (value instanceof BigDecimal && ((BigDecimal) value).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(fieldName + " 값은 0 이상이어야 합니다.");
        }
        if (value instanceof Integer && ((Integer) value) < 0) {
            throw new IllegalArgumentException(fieldName + " 값은 0 이상이어야 합니다.");
        }
    }


}
