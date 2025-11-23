package com.kyonggi.diet.search;

import com.kyonggi.diet.search.DTO.SearchRequestDTO;
import com.kyonggi.diet.search.DTO.SearchResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchService {

    private final SearchRepository searchRepository;

    public List<SearchResponseDTO> search(SearchRequestDTO dto) {
        return searchRepository.search(dto);
    }
}
