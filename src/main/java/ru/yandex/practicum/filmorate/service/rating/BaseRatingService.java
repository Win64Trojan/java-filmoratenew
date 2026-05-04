package ru.yandex.practicum.filmorate.service.rating;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.rating.RatingRepository;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.rating.RatingMapper;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BaseRatingService implements RatingService {

    @Autowired
    private final RatingRepository ratingRepository;

    @Override
    public List<RatingDto> findAllRatings() {

        return ratingRepository.findAllRatings().stream()
                .map(RatingMapper::mapToRatingDto)
                .collect(Collectors.toList());
    }

    public RatingDto findRatingById(Long id) {

        Rating rating = ratingRepository.findRatingById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг с ID [" + id + "] не найден."));
        return RatingMapper.mapToRatingDto(rating);
    }
}
