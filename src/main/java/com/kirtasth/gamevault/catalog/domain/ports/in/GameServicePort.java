package com.kirtasth.gamevault.catalog.domain.ports.in;

import com.kirtasth.gamevault.catalog.domain.models.*;
import com.kirtasth.gamevault.common.models.page.Page;
import com.kirtasth.gamevault.common.models.page.PageRequest;
import com.kirtasth.gamevault.common.models.util.Result;

import java.util.List;

public interface GameServicePort {

    Result<Game> create(NewGame newGame);
    Result<Game> findById(Long id);
    Result<GameStatus> createStatus(NewGameStatus newGameStatus);
    Result<Game> addStatusList(Long gameId, List<GameStatus> gameStatuses);
    Result<Game> addTagList(Long gameId, List<GameTag> gameTags);
    Page<Game> listAll(PageRequest pageRequest, GameCriteria gameCriteria);
    Result<Developer> registerDeveloper(NewDeveloper newDeveloper);
}
