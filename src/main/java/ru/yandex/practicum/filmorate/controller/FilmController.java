package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController()
@RequestMapping("/films")
@Slf4j()
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.trace("Валидация прошла успешно, идёт присваивание ID");
        film.setId(generateId());
        log.trace("Добавление фильма");
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.trace("Валидация прошла успешно");

        if (film.getId() == null) {
            log.warn("При обновлении, Id фильма не был указан");
            throw new ValidationException("Id фильма обязательно должен быть указан.");
        } else if (!incorrectId(film)) {
            log.warn("При обновлении, указан несуществующий Id фильма");
            throw new ValidationException("Такого Id не в базе");
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

    @GetMapping
    public Collection<Film> getAll() {
        return films.values();
    }

    Long generateId() {

        long currentId = films.keySet().stream().mapToLong(id -> id).max().orElse(0L);

        return ++currentId;
    }

    boolean incorrectId(Film film) {
        return films.keySet().stream().anyMatch(id -> id.equals(film.getId()));
    }
}
