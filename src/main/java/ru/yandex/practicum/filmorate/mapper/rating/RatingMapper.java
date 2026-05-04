package ru.yandex.practicum.filmorate.mapper.rating;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.model.Rating;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RatingMapper {

    public static RatingDto mapToRatingDto(Rating rating) {

        RatingDto ratingDto = new RatingDto();
        ratingDto.setId(rating.getId());
        ratingDto.setName(rating.getName());

        return ratingDto;
    }
}
