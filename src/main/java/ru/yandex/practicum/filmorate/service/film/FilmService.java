package ru.yandex.practicum.filmorate.service.film;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;

import java.util.List;

@Service
public interface FilmService {

    List<FilmDto> getAll();

    FilmDto create(NewFilmRequest request);

    FilmDto update(UpdateFilmRequest request);

    FilmDto getFilmById(Long id);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    List<FilmDto> findPopularFilms(int size);
}
