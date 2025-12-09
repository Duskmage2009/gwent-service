package com.github.duskmage2009.dto.request.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardListResponse {

    private List<CardListItemResponse> list;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private int pageSize;
}