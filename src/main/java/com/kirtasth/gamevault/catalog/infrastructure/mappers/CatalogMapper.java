package com.kirtasth.gamevault.catalog.infrastructure.mappers;

import com.kirtasth.gamevault.catalog.domain.models.*;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.entities.DeveloperEntity;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.entities.GameEntity;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.entities.GameStatusEntity;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.requests.GameCriteriaRequest;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.requests.NewDeveloperRequest;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.requests.NewGameRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CatalogMapper {

    Game toGame(GameEntity gameEntity);

    GameEntity toGameEntity(Game game);

    GameStatus toGameStatus(GameStatusEntity gameStatusEntity);

    GameStatusEntity toGameStatusEntity(GameStatus gameStatus);

    NewGame toNewGame(NewGameRequest newGameRequest);

    GameCriteria toGameCriteria(GameCriteriaRequest gameCriteriaRequest);

    Developer toDeveloper(DeveloperEntity developerEntity);

    DeveloperEntity toDeveloperEntity(Developer developer);

    NewDeveloper toNewDeveloper(NewDeveloperRequest newDeveloperRequest);
}
