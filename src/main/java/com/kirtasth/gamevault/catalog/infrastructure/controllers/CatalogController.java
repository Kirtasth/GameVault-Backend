package com.kirtasth.gamevault.catalog.infrastructure.controllers;

import com.kirtasth.gamevault.catalog.domain.ports.in.GameServicePort;
import com.kirtasth.gamevault.catalog.domain.ports.out.UserValidationPort;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.requests.GameCriteriaRequest;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.requests.NewDeveloperRequest;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.requests.NewGameRequest;
import com.kirtasth.gamevault.catalog.infrastructure.mappers.CatalogMapper;
import com.kirtasth.gamevault.common.infrastructure.PageMapper;
import com.kirtasth.gamevault.users.domain.models.AuthUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.NonNull;
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

        var authUser = (AuthUser) authentication.getPrincipal();

        var userId = this.userValidation.getUserId(authUser.getEmail());

        this.gameService.create(this.mapper.toNewGame(newGameRequest, userId));

        return ResponseEntity.status(HttpStatus.CREATED).contentLength(0).build();
    }

    @GetMapping
    public ResponseEntity<?> listAllWithSpecificationAndPageable(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable,
            @ModelAttribute GameCriteriaRequest gameCriteriaRequest
    ) {
        var domainPageable = this.pageMapper.toDomain(pageable);
        var gameCriteria = this.mapper.toGameCriteria(gameCriteriaRequest);

        var result = this.gameService.listAll(domainPageable, gameCriteria);

        var response = this.pageMapper.toSpring(result, pageable)
                .map(this.mapper::toGameResponse);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/developer")
    public ResponseEntity<?> registerDeveloper(
            @RequestBody @Valid NewDeveloperRequest newDeveloperRequest,
            Authentication authentication
    ) {
        var userId = ((AuthUser)authentication.getPrincipal()).getId();
        log.info("Registering new developer with data: {}.", newDeveloperRequest);
        this.gameService.registerDeveloper(this.mapper.toNewDeveloper(userId, newDeveloperRequest));

        return ResponseEntity.status(HttpStatus.CREATED).contentLength(0).build();
    }

    @GetMapping("/my-games/{developerId}")
    public ResponseEntity<?> listMyGames(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @ModelAttribute GameCriteriaRequest gameCriteriaRequest,
            @PathVariable @NonNull @Min(1) Long developerId
    ) {
        var pageRequest = this.pageMapper.toDomain(pageable);

        var gameCriteria = this.mapper.toGameCriteria(gameCriteriaRequest);

        var result = this.gameService.listDevGames(developerId, pageRequest, gameCriteria);

        var response = this.pageMapper.toSpring(result, pageable)
                .map(this.mapper::toGameResponse);
        return ResponseEntity.ok(response);
    }
}
