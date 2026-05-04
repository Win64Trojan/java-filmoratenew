package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.film.FilmRepostitory;
import ru.yandex.practicum.filmorate.dal.genre.GenreRepository;
import ru.yandex.practicum.filmorate.dal.rating.RatingRepository;
import ru.yandex.practicum.filmorate.dal.user.UserRepository;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.film.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BaseFilmService implements FilmService {


    @Autowired
    private final FilmRepostitory filmRepostitory;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final RatingRepository ratingRepository;
    @Autowired
    private final GenreRepository genreRepository;

    @Override
    public List<FilmDto> getAll() {
        return filmRepostitory.getAll()
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    @Override
    public FilmDto create(NewFilmRequest request) {


        log.trace("Добавление фильма");

        Rating rating = new Rating();

        if (request.getMpa().getId() != null) {
            rating = ratingRepository.findRatingById(request.getMpa().getId())
                    .orElseThrow(() -> new NotFoundException("Рейтинга с id =" + request.getMpa().getId() + " не существует"));

        }

        LinkedHashSet<Genre> genres = request.getGenres().stream()
                .map(genreRequest -> genreRepository.findGenreById(genreRequest.getId()).orElseThrow(() -> new NotFoundException("Жанра с этим айди не существует")))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));


        Film film = FilmMapper.mapToFilm(request);

        film.setMpa(rating);
        film.setGenres(genres);

        film = filmRepostitory.create(film);

        return FilmMapper.mapToFilmDto(film);
    }

    @Override
    public FilmDto update(UpdateFilmRequest request) {

        Film updateFilm = filmRepostitory.findFilmById(request.getId())
                .map(film -> FilmMapper.updateFilmFields(film, request))
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        updateFilm = filmRepostitory.update(updateFilm);

        return FilmMapper.mapToFilmDto(updateFilm);
    }

    @Override
    public FilmDto getFilmById(Long id) {
        return filmRepostitory.findFilmById(id)
                .map(FilmMapper::mapToFilmDto)
                .orElseThrow(() -> new NotFoundException("Фильм с ID [" + id + "] не найден"));
    }

    @Override
    public void addLike(Long filmId, Long userId) {

        Film film = filmRepostitory.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID [" + filmId + "] не найден"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID [" + userId + "] не найден"));

        filmRepostitory.addLike(film, user);

    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        Film film = filmRepostitory.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID [" + filmId + "] не найден"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID [" + userId + "] не найден"));

        filmRepostitory.removeLike(film, user);

    }

    @Override
    public List<FilmDto> findPopularFilms(int size) {
        return filmRepostitory.findPopularFilms(size).stream()
                .filter(film -> film.getLikes() != null && film.getLikes() > 0)
                .map(FilmMapper::mapToFilmDto)
                .sorted(Comparator
                        .comparing(FilmDto::getLikes)
                        .reversed())
                .collect(Collectors.toList());
    }
}
