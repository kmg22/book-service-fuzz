package com.kmg.BookService.dto;

import lombok.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinResponse{
    private String version;
    private String title;
    private String link;
    private String pubDate;
    private int totalResults;
    private int startIndex;
    private int itemsPerPage;
    private String query;
    private int searchCategoryId;
    private String searchCategoryName;

    private List<AladinItem> item;
}

