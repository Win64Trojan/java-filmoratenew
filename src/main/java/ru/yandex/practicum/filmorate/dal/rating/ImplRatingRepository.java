package ru.yandex.practicum.filmorate.dal.rating;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Optional;

@Repository
public class ImplRatingRepository extends BaseRepository<Rating> implements RatingRepository {

    private static final String FIND_ALL_RATINGS_QUERY = "SELECT * FROM rating";
    private static final String FIND_RATING_BY_ID_QUERY = "SELECT * FROM rating WHERE rating_id = ?";

    public ImplRatingRepository(JdbcTemplate jdbc, RowMapper<Rating> mapper) {
        super(jdbc, mapper);
    }

    public List<Rating> findAllRatings() {

        return findMany(FIND_ALL_RATINGS_QUERY);
    }

    public Optional<Rating> findRatingById(Long id) {

        return findOne(FIND_RATING_BY_ID_QUERY, id);
    }
}
