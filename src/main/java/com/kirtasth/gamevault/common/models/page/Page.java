package com.kirtasth.gamevault.common.models.page;

import java.util.List;

public record Page<T>(
        List<T> content,
        Integer pageNumber,
        Integer pageSize,
        Long totalElements,
        Integer totalPages
) {
    public static <T> Page<T> empty(int pageNumber, int pageSize) {
        return new Page<>(List.of(), pageNumber, pageSize, 0L, 0);
    }

    public boolean hasContent() {
        return content != null && !content.isEmpty();
    }
    public boolean hasNext(){ return pageNumber + 1 < totalPages; }
    public boolean hasPrevious() { return pageNumber > 0; }
}
