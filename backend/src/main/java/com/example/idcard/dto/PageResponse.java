package com.example.idcard.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 分页返回体（页码从 1 开始）。
 */
public record PageResponse<T>(List<T> list, long total, int page, int size, int totalPages) {

    public static <T> PageResponse<T> of(Page<?> source, List<T> list) {
        return new PageResponse<>(
                list,
                source.getTotalElements(),
                source.getNumber() + 1,
                source.getSize(),
                source.getTotalPages()
        );
    }
}
