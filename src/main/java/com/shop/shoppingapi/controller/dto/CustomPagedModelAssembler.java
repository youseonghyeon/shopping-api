package com.shop.shoppingapi.controller.dto;

import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class CustomPagedModelAssembler<T> implements RepresentationModelAssembler<Page<T>, PagedModel<T>> {

    @Override
    public PagedModel<T> toModel(Page<T> page) {
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(page.getSize(), page.getNumber(), page.getTotalElements());
        return PagedModel.of(page.getContent(), metadata);
    }
}
