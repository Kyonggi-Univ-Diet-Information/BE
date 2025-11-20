package com.kyonggi.diet.elasticsearch.service;

import com.kyonggi.diet.Food.eumer.FoodType;
import com.kyonggi.diet.elasticsearch.document.MenuDocument;
import com.kyonggi.diet.elasticsearch.dto.MenuDocumentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;
import org.springframework.data.elasticsearch.core.SearchHit;
import java.util.List;
import java.util.Map;

import static co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.bool;
import static co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.*;

@Service
@RequiredArgsConstructor
public class MenuSearchService {

    private final ElasticsearchOperations operations;

    public List<MenuDocumentDto> search(String keyword, FoodType category) {

        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }

        NativeQuery query = NativeQuery.builder()
                .withQuery(bool(b -> {

                    // MUST: multi-match
                    b.must(multiMatch(
                            mm -> mm
                                    .fields("searchText")
                                    .query(keyword)
                    ));

                    // FILTER: category exact match
                    if (category != null && !category.toString().isBlank()) {
                        b.filter(term(
                                t -> t.field("category").value(category.toString())
                        ));
                    }

                    return b;
                }))
                .build();

        return operations.search(query, MenuDocument.class)
                .stream()
                .map(SearchHit::getContent)
                .map(MenuDocumentDto::fromDocument)
                .toList();
    }

    public void updateRating(Long menuId, Long count, Double avg) {

        Document doc = Document.from(
                Map.of(
                        "reviewCount", count,
                        "averageRating", avg
                )
        );

        UpdateQuery query = UpdateQuery.builder(menuId.toString())
                .withDocument(doc)
                .build();


        operations.update(
                query,
                IndexCoordinates.of("menu_index")
        );
    }

}
