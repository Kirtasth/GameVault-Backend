package com.kirtasth.gamevault.catalog.domain.ports.out;

import com.kirtasth.gamevault.catalog.domain.models.*;
import com.kirtasth.gamevault.common.models.enums.GameStatusEnum;
import com.kirtasth.gamevault.common.models.page.Page;
import com.kirtasth.gamevault.common.models.page.PageRequest;
import com.kirtasth.gamevault.common.models.util.Result;

import java.util.List;

public interface GameRepoPort {

    Result<Game> save(Game game);
    Result<GameStatus> saveStatus(GameStatus gameStatus);
    Result<Game> findById(Long id);
    Result<GameStatus> findByStatusEnum(GameStatusEnum status);
    Result<Game> addStatusList(Long gameId, List<GameStatus> gameStatuses);
    Result<Game> addTagList(Long gameId, List<GameTag> gameTags);
    Page<Game> findAll(PageRequest pageRequest, GameCriteria gameCriteria);

    Result<Developer> saveDeveloper(Developer developer);
}
