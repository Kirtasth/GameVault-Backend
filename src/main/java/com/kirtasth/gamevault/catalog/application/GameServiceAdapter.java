package com.kirtasth.gamevault.catalog.application;

import com.kirtasth.gamevault.catalog.domain.models.*;
import com.kirtasth.gamevault.catalog.domain.ports.in.GameServicePort;
import com.kirtasth.gamevault.catalog.domain.ports.out.GameRepoPort;
import com.kirtasth.gamevault.catalog.domain.ports.out.ImageStoragePort;
import com.kirtasth.gamevault.catalog.domain.ports.out.UserValidationPort;
import com.kirtasth.gamevault.common.models.enums.RoleEnum;
import com.kirtasth.gamevault.common.models.page.Page;
import com.kirtasth.gamevault.common.models.page.PageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameServiceAdapter implements GameServicePort {

    private final GameRepoPort gameRepo;
    private final UserValidationPort userValidation;
    private final ImageStoragePort imageStorage;

    @Override
    public Game create(NewGame newGame) {
        var game = Game.builder()
                .developerId(newGame.developerId())
                .title(newGame.title())
                .description(newGame.description())
                .price(newGame.price())
                .gameStatuses(List.of())
                .tags(List.of())
                .releaseDate(newGame.releaseDate())
                .imageUrl(null)
                .build();

        var savedGame = gameRepo.save(game);

        if (newGame.image() != null) {
            var imageUrl = imageStorage.uploadGameMainImage(newGame.image(), savedGame.id());

            gameRepo.updateImageUrl(savedGame.id(), imageUrl);
        }
        return savedGame;
    }

    @Override
    public Game findById(Long id) {
        return gameRepo.findById(id);
    }

    @Override
    public Page<Game> listAll(PageRequest pageRequest, GameCriteria gameCriteria) {
        return gameRepo.findAll(pageRequest, gameCriteria);
    }

    @Override
    public Developer registerDeveloper(NewDeveloper newDeveloper) {

        this.userValidation.addRoles(newDeveloper.userId(), List.of(RoleEnum.DEVELOPER));

        var developer = Developer.builder()
                .id(newDeveloper.userId())
                .name(newDeveloper.name())
                .description(newDeveloper.description())
                .games(List.of())
                .build();
        return gameRepo.saveDeveloper(developer);
    }
}
