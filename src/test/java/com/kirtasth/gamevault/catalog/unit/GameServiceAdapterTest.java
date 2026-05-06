package com.kirtasth.gamevault.catalog.unit;

import com.kirtasth.gamevault.catalog.application.GameServiceAdapter;
import com.kirtasth.gamevault.catalog.domain.models.*;
import com.kirtasth.gamevault.catalog.domain.ports.out.GameRepoPort;
import com.kirtasth.gamevault.catalog.domain.ports.out.UserValidationPort;
import com.kirtasth.gamevault.common.domain.models.enums.RoleEnum;
import com.kirtasth.gamevault.common.domain.models.page.Page;
import com.kirtasth.gamevault.common.domain.models.page.PageRequest;
import com.kirtasth.gamevault.common.domain.ports.out.ImageStoragePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceAdapterTest {

    @Mock
    private GameRepoPort gameRepo;

    @Mock
    private UserValidationPort userValidation;

    @Mock
    private ImageStoragePort imageStorage;

    @InjectMocks
    private GameServiceAdapter gameServiceAdapter;

    @Test
    void create_ShouldCreateGameAndReturnIt() {
        byte[] mockImage = "test image".getBytes();
        NewGame newGame = new NewGame(
                1L, "Test Game", "Description", 19.99, Instant.now(), mockImage
        );

        Game savedGame = Game.builder()
                .id(100L)
                .developerId(1L)
                .title("Test Game")
                .description("Description")
                .price(19.99)
                .gameStatuses(List.of())
                .tags(List.of())
                .releaseDate(newGame.releaseDate())
                .build();

        when(gameRepo.save(any(Game.class))).thenReturn(savedGame);
        when(imageStorage.uploadGameMainImage(mockImage, 100L)).thenReturn("http://image.url/main.png");

        Game result = gameServiceAdapter.create(newGame);

        assertNotNull(result);
        assertEquals(100L, result.id());
        assertEquals("Test Game", result.title());

        verify(gameRepo).save(any(Game.class));
        verify(imageStorage).uploadGameMainImage(mockImage, 100L);
        verify(gameRepo).updateImageUrl(100L, "http://image.url/main.png");
    }

    @Test
    void create_ShouldCreateGameWithoutImage() {
        NewGame newGame = new NewGame(
                1L, "Test Game No Image", "Description", 19.99, Instant.now(), null
        );

        Game savedGame = Game.builder()
                .id(101L)
                .developerId(1L)
                .title("Test Game No Image")
                .description("Description")
                .price(19.99)
                .gameStatuses(List.of())
                .tags(List.of())
                .releaseDate(newGame.releaseDate())
                .build();

        when(gameRepo.save(any(Game.class))).thenReturn(savedGame);

        Game result = gameServiceAdapter.create(newGame);

        assertNotNull(result);
        assertEquals(101L, result.id());

        verify(gameRepo).save(any(Game.class));
        verifyNoInteractions(imageStorage);
        verify(gameRepo, never()).updateImageUrl(anyLong(), anyString());
    }

    @Test
    void findById_ShouldReturnGame() {
        Game game = Game.builder().id(1L).title("Found Game").build();
        when(gameRepo.findById(1L)).thenReturn(game);

        Game result = gameServiceAdapter.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Found Game", result.title());
    }

    @Test
    void listAll_ShouldReturnPageOfGames() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        GameCriteria criteria = GameCriteria.builder().build();
        Page<Game> gamePage = new Page<>(List.of(Game.builder().id(1L).build()), 0, 10, 1L, 1);

        when(gameRepo.findAll(pageRequest, criteria)).thenReturn(gamePage);

        Page<Game> result = gameServiceAdapter.listAll(pageRequest, criteria);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(1L, result.content().getFirst().id());
    }

    @Test
    void registerDeveloper_ShouldRegisterDeveloperAndAddRole() {
        NewDeveloper newDeveloper = new NewDeveloper(1L, "DevName", "DevDescription");

        Developer savedDev = Developer.builder()
                .id(1L)
                .name("DevName")
                .description("DevDescription")
                .games(List.of())
                .build();

        when(gameRepo.saveDeveloper(any(Developer.class))).thenReturn(savedDev);

        Developer result = gameServiceAdapter.registerDeveloper(newDeveloper);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("DevName", result.name());

        verify(userValidation).addRoles(1L, List.of(RoleEnum.DEVELOPER));
        verify(gameRepo).saveDeveloper(any(Developer.class));
    }

    @Test
    void listDevGames_ShouldReturnPageOfDeveloperGames() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        GameCriteria criteria = GameCriteria.builder().build();
        Page<Game> gamePage = new Page<>(List.of(Game.builder().id(2L).build()), 0, 10, 1L, 1);

        when(gameRepo.findAllByDevId(1L, pageRequest, criteria)).thenReturn(gamePage);

        Page<Game> result = gameServiceAdapter.listDevGames(1L, pageRequest, criteria);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(2L, result.content().getFirst().id());
    }

    @Test
    void listCustomGames_ShouldReturnPageOfCustomGames() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Long> ids = List.of(1L, 2L);
        Page<Game> gamePage = new Page<>(List.of(
                Game.builder().id(1L).build(),
                Game.builder().id(2L).build()
        ), 0, 10, 2L, 1);

        when(gameRepo.findAllByIds(ids, pageRequest)).thenReturn(gamePage);

        Page<Game> result = gameServiceAdapter.listCustomGames(ids, pageRequest);

        assertNotNull(result);
        assertEquals(2, result.content().size());
    }
}