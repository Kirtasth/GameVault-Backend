package com.kirtasth.gamevault.common.infrastructure;

import com.kirtasth.gamevault.common.models.page.Page;
import com.kirtasth.gamevault.common.models.page.PageRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PageMapper {

    default PageRequest toDomain(org.springframework.data.domain.Pageable springPageable) {
        var sortOrders = springPageable.getSort()
                .stream()
                .map(order -> order.isAscending()
                        ? PageRequest.Sort.Order.asc(order.getProperty())
                        : PageRequest.Sort.Order.desc(order.getProperty()))
                .toList();
        var domainSort = sortOrders.isEmpty()
                ? PageRequest.Sort.unsorted()
                : new PageRequest.Sort(sortOrders);

        return new PageRequest(
                springPageable.getPageNumber(),
                springPageable.getPageSize(),
                domainSort);
    }

    default org.springframework.data.domain.Pageable toSpring(PageRequest domain) {
        if (domain.sort() == null || domain.sort().orders().isEmpty()) {
            return org.springframework.data.domain.PageRequest.of(domain.page(), domain.size());
        }

        var springOrders = domain.sort().orders()
                .stream()
                .map(order -> order.direction() == PageRequest.Sort.Direction.ASC
                        ? Sort.Order.asc(order.property())
                        : Sort.Order.desc(order.property()))
                .toList();
        return org.springframework.data.domain.PageRequest.of(
                domain.page(),
                domain.size(),
                Sort.by(springOrders));
    }

    default <T> Page<T> toDomain(org.springframework.data.domain.Page<T> springPage) {
        if (springPage == null || !springPage.hasContent()) {
            return Page.empty(0, 10);
        }

        return new Page<>(
                springPage.getContent(),
                springPage.getNumber(),
                springPage.getSize(),
                springPage.getTotalElements(),
                springPage.getTotalPages()
        );
    }

    default <T> org.springframework.data.domain.Page<T> toSpring(Page<T> domainPage, Pageable pageable) {
        if (domainPage == null || !domainPage.hasContent()){
            return org.springframework.data.domain.Page.empty();
        }

        return new org.springframework.data.domain.PageImpl<>(
                domainPage.content(),
                pageable,
                domainPage.totalElements()
        );
    }
}
