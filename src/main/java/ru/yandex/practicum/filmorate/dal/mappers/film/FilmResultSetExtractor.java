package ru.yandex.practicum.filmorate.dal.mappers.film;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Component
public class FilmResultSetExtractor implements ResultSetExtractor<List<Film>> {


    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException {
        Map<Long, Film> filmMap = new LinkedHashMap<>();

        while (rs.next()) {
            long filmId = rs.getLong("film_id");

            Film film = filmMap.get(filmId);
            if (film == null) {
                film = createFilmFromRow(rs);
                filmMap.put(filmId, film);
            }

            Long genreId = rs.getObject("genre_id", Long.class);
            if (genreId != null && genreId > 0) {
                Genre genre = new Genre();
                genre.setId(genreId);
                genre.setName(rs.getString("genre_name"));
                film.getGenres().add(genre);
            }
        }

        return new ArrayList<>(filmMap.values());
    }

    private Film createFilmFromRow(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("film_name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getObject("release_date", LocalDate.class));
        film.setDuration(rs.getInt("duration"));
        film.setLikes(rs.getInt("likes"));

        Rating rating = new Rating();
        rating.setId(rs.getLong("rating_id"));
        rating.setName(rs.getString("rating_name"));
        film.setMpa(rating);


        film.setGenres(new LinkedHashSet<>());

        return film;
    }
}