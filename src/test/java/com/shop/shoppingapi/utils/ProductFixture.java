package com.shop.shoppingapi.utils;

import com.shop.shoppingapi.controller.dto.product.CreateProductRequest;
import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.entity.ProductConverter;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.Objects;

public class ProductFixture {

    private static String nameMock = "테스트 상품";
    private static String titleImageMock = "https://example.com/test-image.jpg";
    private static String titleMock = "테스트 상품 제목";
    private static BigDecimal priceMock = BigDecimal.valueOf(10000);
    private static String descriptionMock = "테스트 상품 설명입니다.";
    private static String categoryMock = "테스트 카테고리";
    private static Integer stockMock = 100;
    private static Double discountRateMock = 0.1;

    public static CreateProductRequest toCreateProductRequest(
            @Nullable String name,
            @Nullable String titleImage,
            @Nullable String title,
            @Nullable BigDecimal price,
            @Nullable String description,
            @Nullable String category,
            @Nullable Integer stock
    ) {
        return CreateProductRequest.builder()
                .name(Objects.isNull(name) ? nameMock : name)
                .titleImage(Objects.isNull(titleImage) ? titleImageMock : titleImage)
                .title(Objects.isNull(title) ? titleMock : title)
                .price(Objects.isNull(price) ? priceMock : price)
                .description(Objects.isNull(description) ? descriptionMock : description)
                .category(Objects.isNull(category) ? categoryMock : category)
                .stock(Objects.isNull(stock) ? stockMock : stock)
                .build();
    }

    public static Product toProduct() {
        return toProduct(null, null, null, null, null, null, null, null);
    }
    public static Product toProduct(
            @Nullable String name,
            @Nullable String titleImage,
            @Nullable String title,
            @Nullable BigDecimal price,
            @Nullable String description,
            @Nullable String category,
            @Nullable Integer stock,
            @Nullable Double discountRate
    ) {
        name = Objects.isNull(name) ? nameMock : name;
        titleImage = Objects.isNull(titleImage) ? titleImageMock : titleImage;
        title = Objects.isNull(title) ? titleMock : title;
        price = Objects.isNull(price) ? priceMock : price;
        description = Objects.isNull(description) ? descriptionMock : description;
        category = Objects.isNull(category) ? categoryMock : category;
        stock = Objects.isNull(stock) ? stockMock : stock;
        discountRate = Objects.isNull(discountRate) ? discountRateMock : discountRate;

        return ProductConverter.toEntity(name, titleImage, title, price, description, category, stock, discountRate);
    }

}
