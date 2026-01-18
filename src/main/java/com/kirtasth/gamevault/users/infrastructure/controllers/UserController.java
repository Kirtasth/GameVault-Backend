package com.kirtasth.gamevault.users.infrastructure.controllers;


import com.kirtasth.gamevault.common.infrastructure.PageMapper;
import com.kirtasth.gamevault.common.infrastructure.responses.ErrorResponse;
import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.models.User;
import com.kirtasth.gamevault.users.domain.ports.in.UserServicePort;
import com.kirtasth.gamevault.users.infrastructure.dtos.requests.UserCriteriaDto;
import com.kirtasth.gamevault.users.infrastructure.mappers.UserMapper;
import jakarta.validation.constraints.Min;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final PageMapper pageMapper;
    private final UserMapper userMapper;
    private final UserServicePort userService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> userInfo(@PathVariable @NonNull @Min(1) Long userId) {
        var userRes = this.userService.getUserById(userId);

        if (userRes instanceof Result.Failure<User>(
                int errorCode, String errorMsg, java.util.Map<String, String> errorDetails, Exception exception
        )) {
            var errorResponse = new ErrorResponse(
                    errorCode,
                    exception == null
                            ? "UNKNOWN_EXCEPTION"
                            : exception.getClass().getSimpleName(),
                    errorMsg,
                    errorDetails
            );
            return ResponseEntity.status(errorCode).body(errorResponse);
        }

        var user = (Result.Success<User>) userRes;
        return ResponseEntity.ok(userMapper.toUserResponse(user.data()));
    }


    @GetMapping
    public ResponseEntity<?> listWithParams(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable,
            @ModelAttribute UserCriteriaDto criteriaDto
            ){
        var pageRequest = this.pageMapper.toDomain(pageable);
        var userCriteria = this.userMapper.toUserCriteria(criteriaDto);

        var userList = this.pageMapper.toSpring(
                this.userService.listUsersWithCriteria(userCriteria, pageRequest),
                pageable).map(userMapper::toUserResponse);


        return ResponseEntity.ok(userList);
    }
    
}
