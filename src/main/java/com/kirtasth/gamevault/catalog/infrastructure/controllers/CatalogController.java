package com.kirtasth.gamevault.catalog.infrastructure.controllers;

import com.kirtasth.gamevault.catalog.domain.models.Developer;
import com.kirtasth.gamevault.catalog.domain.models.Game;
import com.kirtasth.gamevault.catalog.domain.ports.in.GameServicePort;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.requests.GameCriteriaRequest;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.requests.NewDeveloperRequest;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.requests.NewGameRequest;
import com.kirtasth.gamevault.catalog.infrastructure.mappers.CatalogMapper;
import com.kirtasth.gamevault.common.infrastructure.PageMapper;
import com.kirtasth.gamevault.common.infrastructure.responses.ErrorResponse;
import com.kirtasth.gamevault.common.models.util.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/catalog")
public class CatalogController {

    private final GameServicePort gameService;
    private final CatalogMapper mapper;
    private final PageMapper pageMapper;

    @PostMapping
    public ResponseEntity<?> createNewGame(
            @RequestBody @Valid NewGameRequest newGameRequest
    ) {

        var result = this.gameService.create(this.mapper.toNewGame(newGameRequest));

        if (result instanceof Result.Failure<Game>(
                int errorCode, String errorMsg, java.util.Map<String, String> errorDetails, Exception exception
        )) {
            return new ResponseEntity<>(
                    new ErrorResponse(
                            errorCode,
                            exception == null
                                    ? "UNKNOWN_EXCEPTION"
                                    : exception.getClass().getSimpleName(),
                            errorMsg,
                            errorDetails
                    ),
                    HttpStatus.valueOf(errorCode)
            );
        }

        return ResponseEntity.status(HttpStatus.CREATED).contentLength(0).build();
    }

    @GetMapping
    public ResponseEntity<?> listAllWithSpecificationAndPageable(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable,
            @ModelAttribute GameCriteriaRequest gameCriteriaRequest
    ) {
        var domainPageable = this.pageMapper.toDomain(pageable);
        var gameCriteria = this.mapper.toGameCriteria(gameCriteriaRequest);


        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @PostMapping("/register-developer")
    public ResponseEntity<?> registerDeveloper(
            @RequestBody @Valid NewDeveloperRequest newDeveloperRequest
    ) {
        log.info("Registering new developer with data: " + newDeveloperRequest.toString() + ".");
        var result = this.gameService.registerDeveloper(this.mapper.toNewDeveloper(newDeveloperRequest));

        if (result instanceof Result.Failure<Developer>(
                int errorCode, String errorMsg, Map<String, String> errorDetails, Exception exception
        )) {
            return new ResponseEntity<>(
                    new ErrorResponse(
                            errorCode,
                            exception == null
                                    ? "UNKNOWN_EXCEPTION"
                                    : exception.getClass().getSimpleName(),
                            errorMsg,
                            errorDetails
                    ),
                    HttpStatus.valueOf(errorCode)
            );
        }

        return ResponseEntity.status(HttpStatus.CREATED).contentLength(0).build();
    }
}
