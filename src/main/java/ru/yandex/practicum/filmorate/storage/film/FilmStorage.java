package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    Optional<Film> getFilmById(Long id);

    Collection<Film> getAll();

    Film addLike(Film film, User user);

    Film removeLike(Film film, User user);

    Collection<Film> findPopularFilms(int size);
}
