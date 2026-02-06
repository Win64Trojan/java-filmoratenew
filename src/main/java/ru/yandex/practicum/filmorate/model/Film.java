package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.DateRange;
import ru.yandex.practicum.filmorate.annotation.PositiveTime;

import java.time.LocalDate;

@Data
public class Film {

    Long id;
    @NotBlank(message = "название не может быть пустым или состоять только из пробелов")
    String name;
    @Size(max = 200, message = "максимальная длина описания — 200 символов")
    String description;
    @DateRange(minDate = "1895-12-28")
    LocalDate releaseDate;
    @PositiveTime
    Integer duration;

}
