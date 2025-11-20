package com.kyonggi.diet.auth.config;

import com.kyonggi.diet.elasticsearch.document.MenuDocument;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;

import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ElasticsearchIndexConfig {

    private final ElasticsearchOperations operations;

    @PostConstruct
    public void setupIndex() {
        IndexOperations io = operations.indexOps(MenuDocument.class);

        // 기존 삭제 후 재생성 (테스트 환경이면 무조건 추천)
        if (io.exists()) {
            io.delete();
        }

        Map<String, Object> settings = Map.of(
                "analysis", Map.of(
                        "tokenizer", Map.of(
                                "ngram_tokenizer", Map.of(
                                        "type", "ngram",
                                        "min_gram", 1,
                                        "max_gram", 2,
                                        "token_chars", List.of("letter", "digit")
                                )
                        ),
                        "analyzer", Map.of(
                                "ngram_analyzer", Map.of(
                                        "type", "custom",
                                        "tokenizer", "ngram_tokenizer"
                                )
                        )
                )
        );

        io.create(settings);

        // 매핑: searchText에 analyzer 적용
        Document mapping = Document.parse("""
        {
          "properties": {
            "searchText": { "type": "text", "analyzer": "ngram_analyzer" },
            "menuName":   { "type": "text", "analyzer": "standard" },
            "menuNameEn": { "type": "text", "analyzer": "standard" },
            "category":   { "type": "keyword" },
            "restaurantType": { "type": "keyword" },
            "subRestaurant": { "type": "keyword" },
            "menuId": { "type": "integer" },
            "reviewCount": { "type": "integer" },
            "averageRating": { "type": "float" }
          }
        }
        """);

        io.putMapping(mapping);

        System.out.println("menu_index 생성 완료");
    }
}
