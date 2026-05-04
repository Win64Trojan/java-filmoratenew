package ru.yandex.practicum.filmorate.dal.rating;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Optional;

public interface RatingRepository {

    List<Rating> findAllRatings();

    Optional<Rating> findRatingById(Long id);
}
