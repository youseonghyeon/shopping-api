package com.shop.shoppingapi.service;

import com.shop.shoppingapi.controller.dto.SubmitOrderRequest;
import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.entity.User;
import com.shop.shoppingapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderValidationService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public void validateOrder(SubmitOrderRequest request) {

        // 1. 요청된 상품이 모두 존재하는지 검증
        List<Long> productIds = request.getProductIds();
        List<Product> products = productRepository.findAllById(productIds);
        this.validateProductsExist(products, productIds);

        Map<Long, BigDecimal> discountedPriceMap = products.stream().collect(Collectors.toMap(Product::getId, Product::getDiscountedPrice));
        BigDecimal calculatedItemsTotal = calculateItemsTotal(request.getItems(), discountedPriceMap);

        // 3. 상품별 할인율을 적용하여 총 상품 가격 계산
        this.validateTotalProductPrice(calculatedItemsTotal, request.getTotalProductPrice());

        // 5. 최종 결제 금액 검증: (할인 후 상품총액 - usedPoints) + shippingFee
        BigDecimal shippingFee = request.getShippingFee();
        BigDecimal usedPoints = new BigDecimal(request.getUsedPoints());
        BigDecimal expectedFinalPayment = calculatedItemsTotal.subtract(usedPoints).add(shippingFee);
        this.validateFinalPayment(expectedFinalPayment, request.getTotalPayment());

        // 6. 상품 가격보다 사용하려는 포인트가 큰지 검증
        this.validatePointGreaterThanProductPrice(request.getTotalProductPrice(), usedPoints);
    }

    @Transactional(readOnly = true)
    public void validateUserPoint(User user, Integer usedPoints) {
        if (user.getPoint() < usedPoints) {
            throw new IllegalArgumentException("사용 가능한 포인트가 부족합니다.");
        }
    }

    private void validatePointGreaterThanProductPrice(BigDecimal totalProductPrice, BigDecimal usedPoints) {
        if (usedPoints.compareTo(totalProductPrice) > 0) {
            throw new IllegalArgumentException("상품 가격보다 사용하려는 포인트가 많습니다.");
        }
    }

    private void validateProductsExist(List<Product> products, List<Long> productIds) {
        if (products.size() != productIds.size()) {
            throw new IllegalArgumentException("상품 정보가 올바르지 않습니다.");
        }
    }

    private BigDecimal calculateItemsTotal(List<SubmitOrderRequest.OrderItemRequest> items, Map<Long, BigDecimal> productPriceMap) {
        BigDecimal total = BigDecimal.ZERO;
        for (SubmitOrderRequest.OrderItemRequest item : items) {
            Long productId = item.getProductId();
            BigDecimal discountedPrice = productPriceMap.get(productId);
            if (discountedPrice == null) {
                throw new IllegalArgumentException("상품 정보 누락: " + productId);
            }
            // 예상 총액 = 할인 적용 후 단가 * 수량
            BigDecimal expectedTotalPrice = discountedPrice.multiply(new BigDecimal(item.getQuantity()));
            BigDecimal multiply = discountedPrice.multiply(new BigDecimal(item.getQuantity()));
            if (multiply.compareTo(expectedTotalPrice) != 0) {
                throw new IllegalArgumentException("상품 ID " + productId + "의 주문 금액이 올바르지 않습니다.");
            }
            total = total.add(expectedTotalPrice);
        }
        return total;
    }

    private void validateTotalProductPrice(BigDecimal calculatedTotal, BigDecimal requestTotal) {
        if (calculatedTotal.compareTo(requestTotal) != 0) {
            throw new IllegalArgumentException("총 상품 가격이 올바르지 않습니다.");
        }
    }

    private void validateFinalPayment(BigDecimal expectedFinalPayment, BigDecimal requestFinalPayment) {
        if (expectedFinalPayment.compareTo(requestFinalPayment) != 0) {
            throw new IllegalArgumentException("최종 결제 금액이 올바르지 않습니다.");
        }
    }
}
