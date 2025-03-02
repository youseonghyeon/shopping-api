package com.shop.shoppingapi.entity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class BaseEntity {

    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @LastModifiedBy
    @ManyToOne
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
