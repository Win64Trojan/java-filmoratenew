package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class FilmTest {

    @Test
    void creatObjectFilm() {
        Film film = new Film();
        film.setId(1L);
        film.setName("Film 1");
        film.setDescription("Film 1");
        film.setReleaseDate(LocalDate.of(2023, 1, 1));
        film.setDuration(15);

        assertNotNull(film);
    }

}