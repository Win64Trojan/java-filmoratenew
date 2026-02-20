package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;

@RestController()
@RequestMapping("/films")
@Slf4j()
@RequiredArgsConstructor()
public class FilmController {

    @Autowired
    private final FilmService filmService;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.trace("Валидация @Valid прошла успешно, идёт присваивание ID");
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.trace("Валидация @Valid прошла успешно");
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film updateLike(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        return filmService.addLike(filmId, userId);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @GetMapping
    public Collection<Film> getAll() {
        return filmService.getAll();
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(name = "count", defaultValue = "10") Integer size) {

        if (size < 0) {
            throw new ValidationException("Занчение не может быть меньше нуля.");
        }

        return filmService.findPopularFilms(size);
    }

    @DeleteMapping("{id}/like/{userId}")
    public Film removeLike(@PathVariable("id") Long filmId, @PathVariable Long userId) {

        return filmService.removeLike(filmId, userId);
    }

}
