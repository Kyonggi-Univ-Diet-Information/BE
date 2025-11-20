package com.kyonggi.diet.elasticsearch.controller;

import com.kyonggi.diet.elasticsearch.dto.MenuDocumentDto;
import com.kyonggi.diet.elasticsearch.service.MenuSearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/menus")
@Tag(name = "검색 API", description = "경슐랭 / 이스퀘어 / 샐리박스 식당 음식 검색 API (엘라스틱 서치)")
public class MenuSearchController {

    private final MenuSearchService menuSearchService;

    @GetMapping("/search")
    public ResponseEntity<List<MenuDocumentDto>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(menuSearchService.search(keyword));
    }
}
