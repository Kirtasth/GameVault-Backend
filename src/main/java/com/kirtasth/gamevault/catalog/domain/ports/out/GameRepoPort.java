package com.kirtasth.gamevault.catalog.domain.ports.out;

import com.kirtasth.gamevault.catalog.application.exception.GameAlreadyExistsException;
import com.kirtasth.gamevault.catalog.application.exception.GameNotFoundException;
import com.kirtasth.gamevault.catalog.domain.models.Developer;
import com.kirtasth.gamevault.catalog.domain.models.Game;
import com.kirtasth.gamevault.catalog.domain.models.GameCriteria;
import com.kirtasth.gamevault.common.models.page.Page;
import com.kirtasth.gamevault.common.models.page.PageRequest;

public interface GameRepoPort {

    Game save(Game game) throws GameAlreadyExistsException;

    Game findById(Long id) throws GameNotFoundException;

    Page<Game> findAll(PageRequest pageRequest, GameCriteria gameCriteria);

    Developer saveDeveloper(Developer developer);

    void updateImageUrl(Long gameId, String imageUrl);

    Page<Game> findAllByDevId(Long developerId, PageRequest pageRequest, GameCriteria gameCriteria);
}
