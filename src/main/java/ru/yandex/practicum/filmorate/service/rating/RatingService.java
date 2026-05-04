package ru.yandex.practicum.filmorate.service.rating;

import ru.yandex.practicum.filmorate.dto.RatingDto;

import java.util.List;

public interface RatingService {

    List<RatingDto> findAllRatings();

    RatingDto findRatingById(Long id);
}
