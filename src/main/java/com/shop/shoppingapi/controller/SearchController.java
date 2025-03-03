package com.shop.shoppingapi.controller;

import com.shop.shoppingapi.controller.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@RestController
public class SearchController {

    @GetMapping("/search/products")
    public ResponseEntity<ApiResponse<List<String>>> search(@RequestParam("q") String query) {
        List<String> list = IntStream.rangeClosed(1, 10).mapToObj(i -> query + i).toList();
        return ApiResponse.success(list);
    }
}
