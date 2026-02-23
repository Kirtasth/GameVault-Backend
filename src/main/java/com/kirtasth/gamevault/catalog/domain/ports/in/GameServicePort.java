package com.kirtasth.gamevault.catalog.domain.ports.in;

import com.kirtasth.gamevault.catalog.domain.models.*;
import com.kirtasth.gamevault.common.models.page.Page;
import com.kirtasth.gamevault.common.models.page.PageRequest;

public interface GameServicePort {

    Game create(NewGame newGame);

    Game findById(Long id);

    Page<Game> listAll(PageRequest pageRequest, GameCriteria gameCriteria);

    Developer registerDeveloper(NewDeveloper newDeveloper);
}
