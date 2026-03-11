package com.kirtasth.gamevault.catalog.infrastructure.mappers;

import com.kirtasth.gamevault.catalog.domain.models.*;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.entities.DeveloperEntity;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.entities.GameEntity;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.requests.GameCriteriaRequest;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.requests.NewDeveloperRequest;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.requests.NewGameRequest;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.responses.GameResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Mapper(componentModel = "spring",
        uses = {DeveloperEntityResolver.class})
public interface CatalogMapper {

    @Mapping(target = "image", expression = "java(mapImage(newGameRequest.image()))")
    @Mapping(target = "developerId", source = "developerId")
    NewGame toNewGame(NewGameRequest newGameRequest, Long developerId);

    GameCriteria toGameCriteria(GameCriteriaRequest gameCriteriaRequest);

    NewDeveloper toNewDeveloper(Long userId, NewDeveloperRequest newDeveloperRequest);

    @Mapping(target = "developerId", source = "developer.id")
    Game toGame(GameEntity gameEntity);

    @Mapping(target = "developer", source = "developerId", qualifiedByName = "developerIdToEntity")
    GameEntity toGameEntity(Game game);

    Developer toDeveloper(DeveloperEntity developerEntity);

    DeveloperEntity toDeveloperEntity(Developer developer);

    GameResponse toGameResponse(Game game);

    default byte[] mapImage(MultipartFile image) {
        try {
            return image != null ? image.getBytes() : null;
        } catch (IOException e) {
            return null;
        }
    }
}
