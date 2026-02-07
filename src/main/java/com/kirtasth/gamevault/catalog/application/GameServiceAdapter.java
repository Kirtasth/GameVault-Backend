package com.kirtasth.gamevault.catalog.application;

import com.kirtasth.gamevault.catalog.domain.models.*;
import com.kirtasth.gamevault.catalog.domain.ports.in.GameServicePort;
import com.kirtasth.gamevault.catalog.domain.ports.out.GameRepoPort;
import com.kirtasth.gamevault.catalog.domain.ports.out.UserValidationPort;
import com.kirtasth.gamevault.common.models.page.Page;
import com.kirtasth.gamevault.common.models.page.PageRequest;
import com.kirtasth.gamevault.common.models.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GameServiceAdapter implements GameServicePort {

    private final GameRepoPort gameRepo;
    private final UserValidationPort userValidation;

    @Override
    public Result<Game> create(NewGame newGame) {
        var canCreateGamesRes = this.userValidation.canCreateGames(newGame.developerId());

        if (canCreateGamesRes instanceof Result.Failure<Boolean>(
                int errorCode, String errorMsg, Map<String, String> errorDetails, Exception exception
        )) {
            return new Result.Failure<>(
                    errorCode,
                    errorMsg,
                    errorDetails,
                    exception
            );
        }

        var canCreateGames = ((Result.Success<Boolean>) canCreateGamesRes).data();

        if (!canCreateGames) {
            return new Result.Failure<>(
                    403,
                    "User is not developer or admin",
                    null,
                    null
            );
        }

        var game = Game.builder()
                .developerId(newGame.developerId())
                .title(newGame.title())
                .description(newGame.description())
                .price(newGame.price())
                .gameStatuses(List.of())
                .tags(List.of())
                .releaseDate(newGame.releaseDate())
                .build();

        return gameRepo.save(game);
    }

    @Override
    public Result<GameStatus> createStatus(NewGameStatus newGameStatus) {
        var gameStatus = GameStatus.builder()
                .gameId(newGameStatus.gameId())
                .status(newGameStatus.status())
                .description(newGameStatus.description())
                .build();

        return null;
    }

    @Override
    public Result<Game> findById(Long id) {
        return gameRepo.findById(id);
    }

    @Override
    public Result<Game> addStatusList(Long gameId, List<GameStatus> gameStatuses) {
        return gameRepo.addStatusList(gameId, gameStatuses);
    }

    @Override
    public Result<Game> addTagList(Long gameId, List<GameTag> gameTags) {
        return gameRepo.addTagList(gameId, gameTags);
    }

    @Override
    public Page<Game> listAll(PageRequest pageRequest, GameCriteria gameCriteria) {

        return null;
    }

    @Override
    public Result<Developer> registerDeveloper(NewDeveloper newDeveloper) {
        var developer = Developer.builder()
                .id(newDeveloper.userId())
                .name(newDeveloper.name())
                .description(newDeveloper.description())
                .games(List.of())
                .build();

        return gameRepo.saveDeveloper(developer);
    }
}
