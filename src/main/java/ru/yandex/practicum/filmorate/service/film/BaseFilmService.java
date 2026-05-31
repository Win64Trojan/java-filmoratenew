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
        log.trace("Получение списка всех фильмов");
        return filmRepostitory.getAll()
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    @Override
    public FilmDto create(NewFilmRequest request) {
        log.trace("Создание нового фильма: {}", request);

        if (request == null) {
            log.error("Попытка создания фильма с null запросом");
            throw new NotFoundException("Запрос не может быть null");
        }

        Rating rating = validateRating(request.getMpa().getId());
        log.debug("Найден рейтинг: {}", rating);

        LinkedHashSet<Genre> genres = validateGenres(request.getGenres());
        log.debug("Найдены жанры: {}", genres.size());


        Film film = FilmMapper.mapToFilm(request);
        film.setMpa(rating);
        film.setGenres(genres);

        log.debug("Создание фильма: {}", film);
        film = filmRepostitory.create(film);
        log.info("Фильм успешно создан: {}", film.getId());

        return FilmMapper.mapToFilmDto(film);
    }

    @Override
    public FilmDto update(UpdateFilmRequest request) {
        log.trace("Обновление фильма: {}", request);

        if (request == null) {
            log.error("Попытка обновления фильма с null запросом");
            throw new NotFoundException("Запрос не может быть null");
        }

        if (request.getId() == null) {
            log.error("Попытка обновления фильма без ID");
            throw new NotFoundException("ID фильма не может быть null");
        }

        Film updateFilm = filmRepostitory.findFilmById(request.getId())
                .orElseThrow(() -> {
                    log.error("Попытка обновления несуществующего фильма с ID: {}", request.getId());
                    return new NotFoundException("Фильм не найден");
                });

        log.debug("Найден фильм для обновления: {}", updateFilm.getId());

        Film updatedFilm = FilmMapper.updateFilmFields(updateFilm, request);
        updatedFilm = filmRepostitory.update(updatedFilm);
        log.info("Фильм успешно обновлен: {}", updatedFilm.getId());

        return FilmMapper.mapToFilmDto(updatedFilm);
    }

    @Override
    public FilmDto getFilmById(Long id) {
        log.trace("Получение фильма по ID: {}", id);

        if (id == null) {
            log.error("Попытка получения фильма с null ID");
            throw new NotFoundException("ID не может быть null");
        }

        return filmRepostitory.findFilmById(id)
                .map(FilmMapper::mapToFilmDto)
                .orElseThrow(() -> new NotFoundException("Фильм с ID [" + id + "] не найден"));
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        log.trace("Добавление лайка фильму {} пользователем {}", filmId, userId);

        if (filmId == null || userId == null) {
            log.error("Попытка добавления лайка с null ID фильма или пользователя");
            throw new NotFoundException("ID фильма и пользователя не могут быть null");
        }

        Film film = findFilmById(filmId);
        User user = findUserById(userId);
        filmRepostitory.addLike(film, user);
        log.info("Лайк добавлен фильму {} пользователем {}", filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        log.trace("Удаление лайка фильму {} пользователем {}", filmId, userId);

        if (filmId == null || userId == null) {
            log.error("Попытка удаления лайка с null ID фильма или пользователя");
            throw new NotFoundException("ID фильма и пользователя не могут быть null");
        }

        Film film = findFilmById(filmId);
        User user = findUserById(userId);
        filmRepostitory.removeLike(film, user);
        log.info("Лайк удален у фильма {} у пользователя {}", filmId, userId);

    }

    @Override
    public List<FilmDto> findPopularFilms(int size) {
        log.trace("Поиск популярных фильмов, размер: {}", size);

        return filmRepostitory.findPopularFilms(size).stream()
                .filter(film -> film.getLikes() != null && film.getLikes() > 0)
                .map(FilmMapper::mapToFilmDto)
                .sorted(Comparator
                        .comparing(FilmDto::getLikes)
                        .reversed())
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFilm(Long filmId) {
        log.trace("Удаление фильма с ID: {}", filmId);

        if (filmId == null) {
            log.error("Попытка удаления фильма с null ID");
            throw new NotFoundException("ID фильма не может быть null");
        }

        filmRepostitory.deleteFilm(filmId);
        log.info("Фильм успешно удален: {}", filmId);
    }


    private Rating validateRating(Long ratingId) {
        log.trace("Валидация рейтинга: {}", ratingId);
        if (ratingId == null) {
            log.debug("Рейтинг не указан, используется значение по умолчанию");
            return new Rating();
        }
        return ratingRepository.findRatingById(ratingId)
                .orElseThrow(() -> new NotFoundException("Рейтинга с id =" + ratingId + " не существует"));
    }

    private LinkedHashSet<Genre> validateGenres(LinkedHashSet<Genre> genres) {
        log.trace("Валидация жанров: {}", genres);
        return genres.stream()
                .map(genreRequest -> genreRepository.findGenreById(genreRequest.getId())
                        .orElseThrow(() -> new NotFoundException("Жанра с этим айди не существует")))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Film findFilmById(Long filmId) {
        log.trace("Поиск фильма по ID: {}", filmId);
        return filmRepostitory.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID [" + filmId + "] не найден"));
    }

    private User findUserById(Long userId) {
        log.trace("Поиск пользователя по ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID [" + userId + "] не найден"));
    }
}
