package com.kyonggi.diet.elasticsearch.service;

import com.kyonggi.diet.elasticsearch.document.MenuDocument;
import com.kyonggi.diet.elasticsearch.dto.MenuDocumentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;
import org.springframework.data.elasticsearch.core.SearchHit;
import java.util.List;

import static co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.bool;
import static co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.match;

@Service
@RequiredArgsConstructor
public class MenuSearchService {

    private final ElasticsearchOperations operations;

    public List<MenuDocumentDto> search(String keyword) {

        NativeQuery query = NativeQuery.builder()
                .withQuery(
                        bool(b -> b
                                .must(match(m -> m
                                        .field("menuName")
                                        .query(keyword)
                                ))
                        )
                )
                .build();

        return operations.search(query, MenuDocument.class)
                .stream()
                .map(SearchHit::getContent)
                .map(MenuDocumentDto::fromDocument)
                .toList();
    }
}
