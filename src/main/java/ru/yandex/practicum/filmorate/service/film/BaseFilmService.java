package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class BaseFilmService implements FilmService {

    @Autowired
    private final FilmStorage filmStorage;
    @Autowired
    private final UserService userService;

    @Override
    public Film create(Film film) {
        log.trace("Добавление фильма");
        return filmStorage.create(film);
    }

    @Override
    public Film update(Film film) {
        return filmStorage.update(film);
    }

    @Override
    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с ID [" + id + "] не найден"));
    }

    @Override
    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID [" + filmId + "] не найден"));
        User user = userService.getUserById(userId);

        return filmStorage.addLike(film, user);
    }

    @Override
    public Film removeLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID [" + filmId + "] не найден"));
        User user = userService.getUserById(userId);

        return filmStorage.removeLike(film, user);
    }

    @Override
    public Collection<Film> findPopularFilms(int size) {
        return filmStorage.findPopularFilms(size);
    }
}
