package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.service.rating.RatingService;

import java.util.List;

@RestController()
@RequestMapping("/mpa")
@RequiredArgsConstructor()
public class RatingController {

    @Autowired
    private final RatingService ratingService;

    @GetMapping
    public List<RatingDto> findAllRatings() {
        return ratingService.findAllRatings();
    }

    @GetMapping("{id}")
    public RatingDto findRatingById(@PathVariable Long id) {
        return ratingService.findRatingById(id);
    }
}
