package com.kirtasth.gamevault.catalog.infrastructure.repositories;

import com.kirtasth.gamevault.catalog.domain.models.*;
import com.kirtasth.gamevault.catalog.domain.ports.out.GameRepoPort;
import com.kirtasth.gamevault.catalog.infrastructure.mappers.CatalogMapper;
import com.kirtasth.gamevault.catalog.infrastructure.repositories.jpa.DeveloperRepository;
import com.kirtasth.gamevault.catalog.infrastructure.repositories.jpa.GameRepository;
import com.kirtasth.gamevault.catalog.infrastructure.repositories.jpa.GameStatusRepository;
import com.kirtasth.gamevault.common.models.enums.GameStatusEnum;
import com.kirtasth.gamevault.common.models.page.Page;
import com.kirtasth.gamevault.common.models.page.PageRequest;
import com.kirtasth.gamevault.common.models.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GameRepoAdapter implements GameRepoPort {

    private final GameRepository gameRepository;
    private final GameStatusRepository gameStatusRepository;
    private final DeveloperRepository developerRepository;
    private final CatalogMapper mapper;

    @Override
    public Result<Game> save(Game game) {
        return new Result.Success<>(
                mapper.toGame(gameRepository.save(mapper.toGameEntity(game)))
        );
    }

    @Override
    public Result<GameStatus> saveStatus(GameStatus gameStatus) {
        return new Result.Success<>(
                mapper.toGameStatus(gameStatusRepository.save(mapper.toGameStatusEntity(gameStatus)))
        );
    }

    @Override
    public Result<Game> findById(Long id) {
        var gameEntity = gameRepository.findById(id);
        if (gameEntity.isPresent()) {
            return new Result.Success<>(mapper.toGame(gameEntity.get()));
        }

        return new Result.Failure<>(
                404,
                "Game not found",
                null,
                null
        );
    }

    @Override
    public Result<GameStatus> findByStatusEnum(GameStatusEnum status) {
        var gameEntityOpt = gameStatusRepository.findByStatus(status);

        if (gameEntityOpt.isEmpty()) {
            return new Result.Failure<>(
                    404,
                    "Game status not found",
                    null,
                    null
            );
        }

        return new Result.Success<>(
                mapper.toGameStatus(gameEntityOpt.get())
        );
    }

    @Override
    public Result<Game> addStatusList(Long gameId, List<GameStatus> gameStatuses) {
        var gameEntityOpt = gameRepository.findById(gameId);

        if (gameEntityOpt.isEmpty()) {
            return new Result.Failure<>(
                    404,
                    "Game not found",
                    null,
                    null
            );
        }
        var gameEntity = gameEntityOpt.get();

        var addedAll = gameEntity.getGameStatuses().addAll(gameStatuses.stream()
                .map(mapper::toGameStatusEntity)
                .toList());

        if (!addedAll) {
            return new Result.Failure<>(
                    500,
                    "Error adding game statuses",
                    null,
                    null
            );
        }

        return new Result.Success<>(mapper.toGame(gameRepository.save(gameEntity)));
    }

    @Override
    public Result<Game> addTagList(Long gameId, List<GameTag> gameTags) {
        return null;
    }

    @Override
    public Page<Game> findAll(PageRequest pageRequest, GameCriteria gameCriteria) {
        return null;
    }

    @Override
    public Result<Developer> saveDeveloper(Developer developer) {
        return new Result.Success<>(
                mapper.toDeveloper(developerRepository.save(mapper.toDeveloperEntity(developer)))
        );
    }
}
