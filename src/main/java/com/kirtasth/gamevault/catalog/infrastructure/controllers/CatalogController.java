package com.kirtasth.gamevault.catalog.infrastructure.controllers;

import com.kirtasth.gamevault.catalog.domain.models.Developer;
import com.kirtasth.gamevault.catalog.domain.models.Game;
import com.kirtasth.gamevault.catalog.domain.ports.in.GameServicePort;
import com.kirtasth.gamevault.catalog.domain.ports.out.UserValidationPort;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.requests.GameCriteriaRequest;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.requests.NewDeveloperRequest;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.requests.NewGameRequest;
import com.kirtasth.gamevault.catalog.infrastructure.mappers.CatalogMapper;
import com.kirtasth.gamevault.common.infrastructure.PageMapper;
import com.kirtasth.gamevault.common.infrastructure.responses.ErrorResponse;
import com.kirtasth.gamevault.common.models.util.Result;
import com.kirtasth.gamevault.users.domain.models.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/catalog")
public class CatalogController {

    private final GameServicePort gameService;
    private final CatalogMapper mapper;
    private final PageMapper pageMapper;
    private final UserValidationPort userValidation;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createNewGame(
            @ModelAttribute @Valid NewGameRequest newGameRequest,
            Authentication authentication
    ) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var authUser = (AuthUser)authentication.getPrincipal();

        var userIdRes = this.userValidation.getUserId(authUser.getEmail());

        if (userIdRes instanceof Result.Failure<Long> failure) {
            return handleFailure(failure);
        }

        var userId = ((Result.Success<Long>) userIdRes).data();

        var result = this.gameService.create(this.mapper.toNewGame(newGameRequest, userId));

        if (result instanceof Result.Failure<Game> failure) {
            return handleFailure(failure);
        }

        return ResponseEntity.status(HttpStatus.CREATED).contentLength(0).build();
    }

    @GetMapping
    public ResponseEntity<?> listAllWithSpecificationAndPageable(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable,
            @ModelAttribute GameCriteriaRequest gameCriteriaRequest,
            Authentication authentication
    ) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var authUser = (AuthUser)authentication.getPrincipal();

        var userIdRes = this.userValidation.getUserId(authUser.getEmail());

        if (userIdRes instanceof Result.Failure<Long> failure) {
            return handleFailure(failure);
        }

        var domainPageable = this.pageMapper.toDomain(pageable);
        var gameCriteria = this.mapper.toGameCriteria(gameCriteriaRequest);

        var result = this.gameService.listAll(domainPageable, gameCriteria);

        var response = this.pageMapper.toSpring(result, pageable).map(this.mapper::toGameResponse);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/developer")
    public ResponseEntity<?> registerDeveloper(
            @RequestBody @Valid NewDeveloperRequest newDeveloperRequest
    ) {
        log.info("Registering new developer with data: {}.", newDeveloperRequest);
        var result = this.gameService.registerDeveloper(this.mapper.toNewDeveloper(newDeveloperRequest));

        if (result instanceof Result.Failure<Developer> failure) {
            return handleFailure(failure);
        }

        return ResponseEntity.status(HttpStatus.CREATED).contentLength(0).build();
    }

    private ResponseEntity<ErrorResponse> handleFailure(Result.Failure<?> failure) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        failure.errorCode(),
                        failure.exception() == null
                                ? "UNKNOWN_EXCEPTION"
                                : failure.exception().getClass().getSimpleName(),
                        failure.errorMsg(),
                        failure.errorDetails()
                ),
                HttpStatus.valueOf(failure.errorCode())
        );
    }
}
