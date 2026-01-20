package com.kmg.BookService.service;

import com.kmg.BookService.dto.AladinItem;
import com.kmg.BookService.dto.AladinResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenApiClient {
    @Value("${openapi.book.url}")
    private String apiUrl;

    @Value("${openapi.book.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    
    // 1) 검색 API
    public List<AladinItem> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            log.warn("검색어가 비어있습니다");
            return Collections.emptyList();
        }
        
        String url = UriComponentsBuilder.fromUriString(apiUrl)
                .path("/ItemSearch.aspx")
                .queryParam("ttbkey", apiKey)
                .queryParam("Query", query)
                .queryParam("QueryType", "Title")  // Title, Author, Publisher 등
                .queryParam("MaxResults", 10)
                .queryParam("start", 1)
                .queryParam("SearchTarget", "Book")
                .queryParam("output", "js")
                .queryParam("Version", "20131101")
                .build()
                .toUriString();
        
        return fetchAndParse(url);
    }

    // 2) 상품 리스트 API
    public List<AladinItem> getList(String queryType) {
        String url = UriComponentsBuilder.fromUriString(apiUrl)
                .path("/ItemList.aspx")
                .queryParam("ttbkey", apiKey)
                .queryParam("QueryType", queryType)
                .queryParam("MaxResults", 10)
                .queryParam("start", 1)
                .queryParam("SearchTarget", "Book")
                .queryParam("output", "js")
                .queryParam("Version", "20131101")
                .build()
                .toUriString();
        
        return fetchAndParse(url);
    }

    // 3) ISBN 조회 API
    public AladinItem lookUp(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            log.warn("ISBN이 비어있습니다");
            return null;
        }
        
        String url = UriComponentsBuilder.fromUriString(apiUrl)
                .path("/ItemLookUp.aspx")
                .queryParam("ttbkey", apiKey)
                .queryParam("itemIdType", "ISBN13")
                .queryParam("ItemId", isbn)
                .queryParam("output", "js")
                .queryParam("Version", "20131101")
                .build()
                .toUriString();
        
        List<AladinItem> items = fetchAndParse(url);
        return items.isEmpty() ? null : items.get(0);
    }

    // 공통 파싱 로직 (퍼징 타겟)
    public List<AladinItem> fetchAndParse(String url) {
        try {
            log.debug("알라딘 API 호출: {}", url);
            String jsonResponse = restTemplate.getForObject(url, String.class);
            
            if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
                log.warn("API 응답이 비어있습니다");
                return Collections.emptyList();
            }
            
            return parseJson(jsonResponse);
            
        } catch (RestClientException e) {
            log.error("API 호출 실패: {}", e.getMessage());
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("예상치 못한 오류: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    // JSON 파싱 (퍼징 타겟)
    public List<AladinItem> parseJson(String json) {
        try {
            AladinResponse response = objectMapper.readValue(json, AladinResponse.class);
            return response.getItem() != null ? response.getItem() : Collections.emptyList();
        } catch (Exception e) {
            log.error("JSON 파싱 실패: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
    
}
