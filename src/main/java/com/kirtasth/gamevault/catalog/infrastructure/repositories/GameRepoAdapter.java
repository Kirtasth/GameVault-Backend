package com.kirtasth.gamevault.catalog.infrastructure.repositories;

import com.kirtasth.gamevault.catalog.application.exception.DeveloperAlreadyExistsException;
import com.kirtasth.gamevault.catalog.application.exception.GameAlreadyExistsException;
import com.kirtasth.gamevault.catalog.application.exception.GameNotFoundException;
import com.kirtasth.gamevault.catalog.application.exception.GameUpdateException;
import com.kirtasth.gamevault.catalog.domain.models.*;
import com.kirtasth.gamevault.catalog.domain.ports.out.GameRepoPort;
import com.kirtasth.gamevault.catalog.infrastructure.dtos.entities.GameEntity;
import com.kirtasth.gamevault.catalog.infrastructure.mappers.CatalogMapper;
import com.kirtasth.gamevault.catalog.infrastructure.repositories.jpa.DeveloperRepository;
import com.kirtasth.gamevault.catalog.infrastructure.repositories.jpa.GameRepository;
import com.kirtasth.gamevault.catalog.infrastructure.repositories.jpa.GameStatusRepository;
import com.kirtasth.gamevault.catalog.infrastructure.specifications.GameEntitySpecification;
import com.kirtasth.gamevault.common.infrastructure.PageMapper;
import com.kirtasth.gamevault.common.models.page.Page;
import com.kirtasth.gamevault.common.models.page.PageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GameRepoAdapter implements GameRepoPort {

    private final GameRepository gameRepository;
    private final GameStatusRepository gameStatusRepository;
    private final DeveloperRepository developerRepository;
    private final CatalogMapper mapper;
    private final PageMapper pageMapper;
    private final GameEntitySpecification gameEntitySpecification;

    @Override
    public Game save(Game game) throws GameAlreadyExistsException {
        try {
            return mapper.toGame(gameRepository.save(mapper.toGameEntity(game)));
        } catch (DataIntegrityViolationException e) {
            throw new GameAlreadyExistsException(game.id());
        }
    }

    @Override
    public Game findById(Long id) throws GameNotFoundException {
        return gameRepository.findById(id).map(mapper::toGame).orElseThrow(
                () -> new GameNotFoundException(id)
        );
    }

    @Override
    public Page<Game> findAll(PageRequest pageRequest, GameCriteria gameCriteria) {
        var pageable = this.pageMapper.toSpring(pageRequest);

        Specification<GameEntity> spec = Specification.allOf(
                this.gameEntitySpecification.containsTitle(gameCriteria.title()),
                this.gameEntitySpecification.containsDeveloper(gameCriteria.developerName()),
                this.gameEntitySpecification.priceGreaterOrEqual(gameCriteria.minPrice()),
                this.gameEntitySpecification.priceLessOrEqual(gameCriteria.maxPrice()),
                this.gameEntitySpecification.releasedAfter(gameCriteria.fromReleaseTime()),
                this.gameEntitySpecification.releasedBefore(gameCriteria.toReleaseTime()),
                this.gameEntitySpecification.containsAllGameTags(gameCriteria.gameTags())
        );

        var page = this.gameRepository.findAll(spec, pageable).map(mapper::toGame);
        return this.pageMapper.toDomain(page);
    }

    @Override
    public Developer saveDeveloper(Developer developer) throws DeveloperAlreadyExistsException {
        try{
            return mapper.toDeveloper(developerRepository.save(mapper.toDeveloperEntity(developer)));
        } catch (DataIntegrityViolationException e) {
            throw new DeveloperAlreadyExistsException(developer.name());
        }
    }

    @Override
    public void updateImageUrl(Long gameId, String imageUrl) throws GameUpdateException, GameNotFoundException {
        if (!gameRepository.existsById(gameId)) {
            throw new GameNotFoundException(gameId);
        }
        try {
            gameRepository.updateImageUrl(gameId, imageUrl);
        } catch (DataIntegrityViolationException e) {
            throw new GameUpdateException(gameId, "imageUrl");
        }
    }
}
