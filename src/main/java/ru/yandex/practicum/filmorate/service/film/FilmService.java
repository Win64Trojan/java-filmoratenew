package ru.yandex.practicum.filmorate.service.film;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

@Service
public interface FilmService {

    Film create(Film film);

    Film update(Film film);

    Film getFilmById(Long id);

    Collection<Film> getAll();

    Film addLike(Long filmId, Long userId);

    Film removeLike(Long filmId, Long userId);

    Collection<Film> findPopularFilms(int size);
}
