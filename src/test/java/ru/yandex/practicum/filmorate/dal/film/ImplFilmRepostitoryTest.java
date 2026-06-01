package ru.yandex.practicum.filmorate.dal.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.dal.user.JdbcImplUserRepository;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.film.BaseFilmService;
import ru.yandex.practicum.filmorate.service.user.BaseUserService;

import java.time.LocalDate;
import java.util.LinkedHashSet;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({ImplFilmRepostitory.class, JdbcImplUserRepository.class, BaseFilmService.class, BaseUserService.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan("ru.yandex.practicum.filmorate")
@ActiveProfiles("test")
public class ImplFilmRepostitoryTest {

    @Autowired
    BaseFilmService filmRepostitory;
    @Autowired
    BaseUserService userRepository;


    static Rating testRating() {
        Rating rating = new Rating();
        rating.setId(1L);
        rating.setName("G");
        return rating;
    }

    static Genre testGenre() {
        Genre genre = new Genre();
        genre.setId(1L);
        genre.setName("Комедия");
        return genre;
    }

    static NewUserRequest testUser() {
        NewUserRequest user = new NewUserRequest();
        user.setName("Sasha");
        user.setLogin("admin");
        user.setBirthday(LocalDate.of(2000, 10, 10));
        user.setEmail("admin@yandex.ru");

        return user;
    }

    static FilmDto compareTestFilm() {
        LinkedHashSet<Genre> setGenres = new LinkedHashSet<>();
        setGenres.add(testGenre());
        FilmDto film = new FilmDto();
        film.setId(1L);
        film.setDescription("Гангстеры делят наркоферму");
        film.setName("Джентельмены");
        film.setMpa(testRating());
        film.setGenres(setGenres);
        film.setDuration(113);
        film.setReleaseDate(LocalDate.of(2019, 12, 3));
        film.setLikes(0);
        return film;
    }

    static NewFilmRequest createTestNewFilmRequest() {
        LinkedHashSet<Genre> setGenres = new LinkedHashSet<>();
        setGenres.add(testGenre());
        NewFilmRequest film = new NewFilmRequest();
        film.setDescription("Гангстеры делят наркоферму");
        film.setName("Джентельмены");
        film.setMpa(testRating());
        film.setGenres(setGenres);
        film.setDuration(113);
        film.setReleaseDate(LocalDate.of(2019, 12, 3));
        return film;
    }

    @Test
    public void testFindById() {


        FilmDto film = filmRepostitory.create(createTestNewFilmRequest());

        FilmDto filmDto = filmRepostitory.getFilmById(film.getId());

        FilmDto equalsFilmDto = compareTestFilm();
        equalsFilmDto.setId(film.getId());

        assertThat(filmDto)
                .usingRecursiveComparison()
                .isEqualTo(equalsFilmDto);
    }


    @Test
    public void testUpdateFilm() {

        UpdateFilmRequest updateFilmRequest = new UpdateFilmRequest();
        updateFilmRequest.setDuration(140);
        updateFilmRequest.setDescription("New Description");
        updateFilmRequest.setName("New Name");
        updateFilmRequest.setReleaseDate(LocalDate.of(2018, 12, 20));


        FilmDto film = filmRepostitory.create(createTestNewFilmRequest());

        updateFilmRequest.setId(film.getId());

        filmRepostitory.update(updateFilmRequest);

        FilmDto filmGetById = filmRepostitory.getFilmById(film.getId());

        assertThat(filmGetById.getDescription())
                .as("Description updated")
                .isEqualTo("New Description");

        assertThat(filmGetById.getName())
                .as("Name updated")
                .isEqualTo("New Name");

        assertThat(filmGetById.getDuration())
                .as("Duration updated")
                .isEqualTo(140);

        assertThat(filmGetById.getReleaseDate())
                .as("Release date updated")
                .isEqualTo(LocalDate.of(2018, 12, 20));
    }

    @Test
    public void testAddLikeAndRemoveLike() {
        FilmDto film = filmRepostitory.create(createTestNewFilmRequest());

        UserDto user = userRepository.create(testUser());

        filmRepostitory.addLike(film.getId(), user.getId());
        FilmDto filmDto = filmRepostitory.getFilmById(film.getId());


        assertThat(filmDto.getLikes())
                .as("Likes updated")
                .isEqualTo(1);


        filmRepostitory.removeLike(film.getId(), user.getId());

        filmDto = filmRepostitory.getFilmById(film.getId());

        assertThat(filmDto.getLikes())
                .as("Likes updated")
                .isEqualTo(0);
    }

    @Test
    public void testGetAllFilms() {

        filmRepostitory.create(createTestNewFilmRequest());
        filmRepostitory.create(createTestNewFilmRequest());
        filmRepostitory.create(createTestNewFilmRequest());

        assertThat(filmRepostitory.getAll().size())
                .as("Get all films")
                .isEqualTo(2);
    }


}