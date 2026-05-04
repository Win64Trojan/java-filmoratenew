package ru.yandex.practicum.filmorate.dal.rating;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.service.rating.BaseRatingService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({ImplRatingRepository.class, BaseRatingService.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan("ru.yandex.practicum.filmorate.dal.mappers.rating")
public class ImplRatingRepositoryTest {
    private final BaseRatingService ratingService;

    @Test
    @DisplayName("получить список всех Mpa")
    public void shouldGetAllMPA() {
        List<RatingDto> listMPA = ratingService.findAllRatings();

        assertThat(listMPA.size())
                .isEqualTo(5);
    }

    @Test
    @DisplayName("получить рейтинг по айди")
    public void testGetRatingById() {
        RatingDto rating = ratingService.findRatingById(1L);

        assertThat(rating.getName())
                .as("Rating by ID")
                .isEqualTo("G");
    }
}