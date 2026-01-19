package com.kirtasth.gamevault.common.models.page;

import java.util.List;
import java.util.Objects;

public record PageRequest(
        Integer page,
        Integer size,
        Sort sort
) {
    public static PageRequest of(int page, int size) {
        return new PageRequest(page, size, Sort.unsorted());
    }

    public static PageRequest of(int page, int size, Sort sort){
        return new PageRequest(page, size, Objects.requireNonNullElse(sort, Sort.unsorted()));
    }

    public record Sort(List<Order> orders) {

        public static Sort unsorted() {
            return new Sort(List.of());
        }

        public static Sort by(Sort.Order... orders){
            return new Sort(List.of(orders));
        }

        public record Order(Sort.Direction direction, String property){
            public static Sort.Order asc(String property) {
                return new Sort.Order(Sort.Direction.ASC, property);
            }

            public static Sort.Order desc(String property) {
                return new Sort.Order(Sort.Direction.DESC, property);
            }

        }

        public enum Direction { ASC, DESC }
    }


}
