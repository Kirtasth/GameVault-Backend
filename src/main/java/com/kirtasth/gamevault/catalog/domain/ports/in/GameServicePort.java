package com.kirtasth.gamevault.catalog.domain.ports.in;

import com.kirtasth.gamevault.catalog.domain.models.*;
import com.kirtasth.gamevault.common.domain.models.page.Page;
import com.kirtasth.gamevault.common.domain.models.page.PageRequest;

import java.util.List;

public interface GameServicePort {

    Game create(NewGame newGame);

    Game findById(Long id);

    Page<Game> listAll(PageRequest pageRequest, GameCriteria gameCriteria);

    Developer registerDeveloper(NewDeveloper newDeveloper);

    Page<Game> listDevGames(Long developerId, PageRequest pageRequest, GameCriteria gameCriteria);

    Page<Game> listCustomGames(List<Long> gameIds, PageRequest pageRequest);
}
