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
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.List;

@RestController()
@RequestMapping("/films")
@Slf4j()
@RequiredArgsConstructor()
public class FilmController {

    @Autowired
    private final FilmService filmService;


    @GetMapping
    public List<FilmDto> getAll() {
        return filmService.getAll();
    }

    @PostMapping
    public FilmDto create(@Valid @RequestBody NewFilmRequest request) {
        log.trace("Валидация @Valid прошла успешно, идёт присваивание ID");
        return filmService.create(request);
    }

    @PutMapping
    public FilmDto update(@Valid @RequestBody UpdateFilmRequest request) {
        log.trace("Валидация @Valid прошла успешно");
        return filmService.update(request);
    }

    @PutMapping("/{id}/like/{userId}")
    public void updateLike(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        filmService.addLike(filmId, userId);
    }

    @GetMapping("/{id}")
    public FilmDto getFilmById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<FilmDto> getPopularFilms(@RequestParam(name = "count", defaultValue = "10") Integer size) {

        if (size < 0) {
            throw new ValidationException("Занчение не может быть меньше нуля.");
        }

        return filmService.findPopularFilms(size);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void removeLike(@PathVariable("id") Long filmId, @PathVariable Long userId) {

        filmService.removeLike(filmId, userId);
    }

}
