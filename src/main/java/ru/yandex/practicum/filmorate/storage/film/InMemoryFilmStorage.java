package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    HashMap<Long, Set<Long>> filmLikes = new HashMap<>();

    @Override
    public Film create(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {

        if (film.getId() == null) {
            log.warn("При обновлении, Id фильма не был указан");
            throw new ValidationException("Id фильма обязательно должен быть указан.");
        } else if (!incorrectId(film)) {
            log.warn("При обновлении, указан несуществующий Id фильма");
            throw new ValidationException("Такого Id нет в базе");
        }

        Film updatedFilm = films.get(film.getId());
        log.trace("Обновляем Название");
        updatedFilm.setName(film.getName());
        log.trace("Обновляем Описание");
        updatedFilm.setDescription(film.getDescription());
        log.trace("Обновляем продолжительность");
        updatedFilm.setDuration(film.getDuration());
        log.trace("Обновляем дату релиза");
        updatedFilm.setReleaseDate(film.getReleaseDate());

        return updatedFilm;
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film addLike(Film film, User user) {

        Set<Long> currentFilm = filmLikes.computeIfAbsent(film.getId(), id -> new HashSet<>());
        if (currentFilm.contains(user.getId())) {
            throw new ValidationException("Данный пользователь уже ставил лайк этому фильму.");
        }
        currentFilm.add(user.getId());

        film.setLikes(film.getLikes() + 1);

        return film;
    }

    @Override
    public Film removeLike(Film film, User user) {

        if (!filmLikes.containsKey(film.getId())) {
            throw new ValidationException("Данный пользователь не ставил лайк этому фильму");
        }
        filmLikes.get(film.getId()).remove(user.getId());
        film.setLikes(film.getLikes() - 1);

        filmLikes.entrySet().removeIf(entry -> entry.getValue().isEmpty());

        return film;

    }

    @Override
    public Collection<Film> findPopularFilms(int size) {

        if (size == 0) {
            return Collections.emptyList();
        }

        return films.values().stream()
                .filter(film -> film.getLikes() != null && film.getLikes() > 0)
                .sorted(Comparator
                        .comparing(Film::getLikes)
                        .reversed())
                .limit(size)
                .collect(Collectors.toList());

    }

    Long generateId() {

        long currentId = films.keySet().stream().mapToLong(id -> id).max().orElse(0L);

        return ++currentId;
    }

    boolean incorrectId(Film film) {
        return films.keySet().stream().anyMatch(id -> id.equals(film.getId()));
    }
}
