package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.service.genre.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreConroller {

    @Autowired
    private final GenreService genreService;

    @GetMapping
    public List<GenreDto> findAllGenres() {
        return genreService.findAllGenres();
    }

    @GetMapping("{id}")
    public GenreDto findGenreById(@PathVariable Long id) {
        return genreService.findGenreById(id);
    }
}
