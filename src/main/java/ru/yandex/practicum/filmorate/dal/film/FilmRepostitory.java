package ru.yandex.practicum.filmorate.dal.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface FilmRepostitory {
    List<Film> getAll();

    Film create(Film film);

    Optional<Film> findFilmById(Long filmId);

    void addLike(Film film, User user);

    void removeLike(Film film, User user);

    Film update(Film film);

    List<Film> findPopularFilms(int size);
}
