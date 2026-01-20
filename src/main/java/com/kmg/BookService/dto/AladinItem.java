package com.kmg.BookService.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinItem {
    private String title;
    private String link;
    private String author;
    private String pubDate;
    private String description;
    private String isbn;
    
    @JsonProperty("isbn13")
    private String isbn13;  // 알라딘은 isbn13 사용
    
    private String publisher;
    private String cover;  // 표지 이미지 URL
    private int priceSales;
    private int priceStandard;
}
