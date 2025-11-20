package com.kyonggi.diet.elasticsearch.repository;

import com.kyonggi.diet.elasticsearch.document.MenuDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface MenuSearchRepository extends ElasticsearchRepository<MenuDocument, String> {
}
