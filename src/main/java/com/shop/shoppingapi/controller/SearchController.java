package com.shop.shoppingapi.controller;

import jakarta.servlet.http.HttpServletRequest;
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

    @GetMapping("/search")
    public ResponseEntity<List<String>> search(@RequestParam("q") String query, HttpServletRequest request) {
        List<String> list = IntStream.rangeClosed(1, 10).mapToObj(i -> query + i).toList();
        return ResponseEntity.ok(list);
    }
}
