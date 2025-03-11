package com.shop.shoppingapi.security.utils;

import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.function.Function;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class LazyLoadingUtils {

    /**
     * 리스트에 있는 모든 항목의 지연 로딩 속성을 강제로 초기화합니다.
     * <p>
     * 리스트 전체의 지연 속성을 로드하려면 {@link #forceLoad(Collection, Function)} 메서드를 사용하세요.<br>
     * 만약 단일 항목의 지연 속성만 로드하려면 {@link #forceLoad(Object, Function)} 메서드를 사용하세요.
     * </p>
     *
     * @param list   항목들의 리스트
     * @param loader 지연 로딩 속성을 로드하는 함수
     * @param <T>    항목의 타입
     * @implNote LazyLoadingUtils.forceLoad(productList, Product : : getReviews);
     */
    public static <T> void forceLoad(Collection<T> list, Function<T, ?> loader) {
        list.forEach(item -> {
            Object lazyProperty = loader.apply(item);
            if (lazyProperty instanceof Collection) {
                int noUseValue = ((Collection<?>) lazyProperty).size();
            } else if (lazyProperty != null) {
                String noUseValue = lazyProperty.toString();
            }
        });
    }

    public static <T> void forceLoad(T item, Function<T, ?> loader) {
        Object lazyProperty = loader.apply(item);
        if (lazyProperty instanceof Collection) {
            int noUseValue = ((Collection<?>) lazyProperty).size();
        } else if (lazyProperty != null) {
            String noUseValue = lazyProperty.toString();
        }
    }

}
