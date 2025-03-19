package com.shop.shoppingapi.repository.querydsl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.shoppingapi.entity.Product;
import com.shop.shoppingapi.entity.QProduct;
import com.shop.shoppingapi.entity.QReview;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Product> findProductsInQueryDsl(Pageable pageable, String containsName) {
        QProduct product = QProduct.product;
        QReview review = QReview.review;

        JPAQuery<Product> query = queryFactory.selectFrom(product)
                .leftJoin(product.reviews, review)
                .groupBy(product.id);
        // where
        if (StringUtils.hasText(containsName)) {
            query.where(product.name.containsIgnoreCase(containsName));
        }
        // order by
        List<? extends OrderSpecifier<?>> orderSpecifiers = extracted(pageable, product, review);
        for (OrderSpecifier<?> orderSpecifier : orderSpecifiers) {
            query.orderBy(orderSpecifier);
        }
        query.offset(pageable.getOffset())  // 페이지네이션 적용
                .limit(pageable.getPageSize()); // limit 적용
        // fetch
        List<Product> results = query
                .setHint("org.hibernate.cacheable", true)
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product);
        // where
        if (StringUtils.hasText(containsName)) {
            countQuery.where(product.name.containsIgnoreCase(containsName));
        }
        Long total = countQuery.setHint("org.hibernate.cacheable", true).fetchOne();

        return new PageImpl<>(results, pageable, total);
    }

    private static List<? extends OrderSpecifier<?>> extracted(Pageable pageable, QProduct product, QReview review) {
        return pageable.getSort().stream().map(order -> getOrderSpecifier(order, product, review)).toList();
    }

    @NotNull
    private static OrderSpecifier<?> getOrderSpecifier(Sort.Order order, QProduct product, QReview review) {
        Order sorting = order.isAscending() ? Order.ASC : Order.DESC;
        return switch (order.getProperty()) {
            // 가격순 정렬 시 할인율을 반영한 가격으로 정렬
            case "price" -> new OrderSpecifier<>(sorting,
                    product.price.subtract(product.price.multiply(product.discountRate))
            );
            // 해당 제품의 평균 별점을 기반으로 정렬
            case "rating" -> new OrderSpecifier<>(sorting,
                    review.rating.sum().divide(review.count().coalesce(1L))
            );
            // rating을 기반으로 정렬하되, 위시리스트 수를 고려하여 정렬
            case "recommended" -> new OrderSpecifier<>(sorting,
                    review.rating.sum().divide(review.count().coalesce(1L))
                            .multiply(product.wishlists.size().divide(99L).coalesce(1))
            );
            default -> {
                log.warn("Unknown sorting property: {}", order.getProperty());
                yield new OrderSpecifier<>(sorting, product.id);
            }
        };
    }
}
