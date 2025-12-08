package com.kirtasth.gamevault.users.infrastructure.controllers;


import com.kirtasth.gamevault.common.infrastructure.PageMapper;
import com.kirtasth.gamevault.users.domain.ports.in.UserServicePort;
import com.kirtasth.gamevault.users.infrastructure.dtos.requests.UserCriteriaDto;
import com.kirtasth.gamevault.users.infrastructure.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final PageMapper pageMapper;
    private final UserMapper userMapper;
    private final UserServicePort userService;

    @GetMapping
    public ResponseEntity<?> listWithParams(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable,
            @ModelAttribute UserCriteriaDto criteriaDto
            ){
        var pageRequest = this.pageMapper.toDomain(pageable);
        var userCriteria = this.userMapper.toUserCriteria(criteriaDto);



        return ResponseEntity.ok(List.of());
    }
}
