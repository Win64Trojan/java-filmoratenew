package ru.yandex.practicum.filmorate.service.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.genre.GenreRepository;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.genre.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor()
public class BaseGenreService implements GenreService {

    @Autowired
    private final GenreRepository genreRepository;

    @Override
    public List<GenreDto> findAllGenres() {
        return genreRepository.findAllGenres().stream()
                .map(GenreMapper::genreToGenreDto)
                .collect(Collectors.toList());
    }

    @Override
    public GenreDto findGenreById(Long id) {
        Genre genre = genreRepository.findGenreById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с ID [" + id + "] не найден."));

        return GenreMapper.genreToGenreDto(genre);
    }
}
