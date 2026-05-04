package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.DateRange;
import ru.yandex.practicum.filmorate.annotation.PositiveTime;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;
import java.util.LinkedHashSet;

@Data
public class UpdateFilmRequest {

    Long id;
    @NotBlank(message = "название не может быть пустым или состоять только из пробелов")
    String name;
    @Size(max = 200, message = "максимальная длина описания — 200 символов")
    String description;
    @DateRange(minDate = "1895-12-28")
    LocalDate releaseDate;
    @PositiveTime
    Integer duration;
    Integer likes;
    LinkedHashSet<Genre> genres = new LinkedHashSet<>();
    Rating mpa;

    public boolean hasName() {
        return !(name == null || name.isEmpty());
    }

    public boolean hasDescription() {
        return !(description == null || description.isEmpty());
    }

    public boolean hasReleaseDate() {
        return releaseDate != null && releaseDate.isBefore(LocalDate.now());
    }

    public boolean hasDuration() {
        return duration != null && duration > 0;
    }

    public boolean hasMpa() {
        return mpa != null;
    }

    public boolean hasGenres() {
        return !(genres == null || genres.isEmpty());
    }
}
