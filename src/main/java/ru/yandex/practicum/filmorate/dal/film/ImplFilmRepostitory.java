package ru.yandex.practicum.filmorate.dal.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.dal.mappers.film.FilmResultSetExtractor;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;


@Repository
public class ImplFilmRepostitory extends BaseRepository<Film> implements FilmRepostitory {

    private static final String FIND_ALL_FILMS = """
            SELECT f.film_id, f.film_name, f.rating_id, r.rating_name, f.description, f.release_date, f.duration, count(l.film_id) AS likes, g.genre_id, g.genre_name
            FROM films AS f
            LEFT JOIN public.likes l ON l.film_id = f.film_id
            JOIN public.rating r ON f.rating_id = r.rating_id
            JOIN public.films_genres fg ON f.film_id = fg.film_id
            JOIN public.genres g ON g.genre_id = fg.genre_id
            GROUP BY f.film_id, f.film_name, f.rating_id, r.rating_name, f.description, f.release_date, f.duration, g.genre_id, g.genre_name""";


    private static final String FIND_FILM_BY_ID_QUERY = """
            SELECT f.film_id, f.film_name, f.rating_id, r.rating_name, f.description, f.release_date, f.duration, count(l.film_id) AS likes, g.genre_id, g.genre_name
            FROM films AS f
                     LEFT JOIN public.likes l ON l.film_id = f.film_id
                     JOIN public.rating r ON f.rating_id = r.rating_id
                     LEFT JOIN public.films_genres fg ON f.film_id = fg.film_id
                     LEFT JOIN public.genres g ON g.genre_id = fg.genre_id
            WHERE f.film_id = ?
            GROUP BY f.film_id, f.film_name, f.rating_id, r.rating_name, f.description, f.release_date, f.duration, g.genre_id, g.genre_name""";

    private static final String FIND_POPULAR_FILM_DESC_QUERY = """
            SELECT f.film_id, f.film_name, f.rating_id, r.rating_name, f.description, f.release_date, f.duration, count(l.film_id) AS likes, g.genre_id, g.genre_name
            FROM films AS f
                     LEFT JOIN public.likes l ON l.film_id = f.film_id
                     JOIN public.rating r ON f.rating_id = r.rating_id
                     LEFT JOIN public.films_genres fg ON f.film_id = fg.film_id
                     LEFT JOIN public.genres g ON g.genre_id = fg.genre_id
            GROUP BY f.film_id, f.film_name, f.rating_id, r.rating_name, f.description, f.release_date, f.duration, g.genre_id, g.genre_name
            ORDER BY likes DESC
            LIMIT ?""";

    private static final String INSERT_FILM_QUERY = "INSERT INTO films (film_name, rating_id, description, release_date, duration) VALUES (?, ?, ?, ?, ?)";

    private static final String CHECK_LIKE_QUERY = "SELECT COUNT(*) FROM LIKES WHERE film_id = ? AND user_id = ?";

    private static final String INSERT_LIKE_QUERY = "INSERT INTO LIKES (film_id, user_id) VALUES (?, ?)";

    private static final String REMOVE_LIKE_QUERY = "DELETE FROM LIKES WHERE film_id = ? AND user_id = ?";

    private static final String UPDATE_QUERY = "UPDATE films SET film_name = ?, rating_id = ?, description = ?, release_date = ?, duration = ? WHERE film_id = ?";

    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO FILMS_GENRES (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_LIKES_QUERY = "DELETE FROM LIKES WHERE film_id = ?";
    private static final String DELETE_FILM_GENRES_QUERY = "DELETE FROM FILMS_GENRES WHERE film_id = ?";
    private static final String DELETE_FILM_QUERY = "DELETE FROM films WHERE film_id = ?";

    @Autowired
    private FilmResultSetExtractor filmResultSetExtractor;

    public ImplFilmRepostitory(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Film> getAll() {
        return findMany(FIND_ALL_FILMS);
    }

    @Override
    public Film create(Film film) {


        Long id = insert(INSERT_FILM_QUERY,
                film.getName(),
                film.getMpa().getId(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration());

        film.setId(id);

        for (Genre genre : film.getGenres()) {
            update(INSERT_FILM_GENRE_QUERY, film.getId(), genre.getId());
        }

        return film;
    }

    @Override
    public Optional<Film> findFilmById(Long filmId) {
        System.out.println(findOne(FIND_FILM_BY_ID_QUERY, filmId));
        return findOne(FIND_FILM_BY_ID_QUERY, filmId);
    }

    @Override
    public void addLike(Film film, User user) {

        if (jdbcTemplate.queryForObject(CHECK_LIKE_QUERY, Integer.class, film.getId(), user.getId()) > 0) {
            throw new ValidationException("Данный пользователь уже ставил лайк этому фильму");
        }

        int result = jdbcTemplate.update(INSERT_LIKE_QUERY, film.getId(), user.getId());

        if (result != 1) {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    @Override
    public void removeLike(Film film, User user) {
        if (jdbcTemplate.queryForObject(CHECK_LIKE_QUERY, Integer.class, film.getId(), user.getId()) == 0) {
            throw new ValidationException("Удаление лайка не удалось, ошибочные данные в айди фильма или в айди пользователя");
        }

        delete(REMOVE_LIKE_QUERY, film.getId(), user.getId());
    }

    @Override
    public Film update(Film film) {
        update(UPDATE_QUERY,
                film.getName(),
                film.getMpa().getId(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId()
        );

        return film;
    }

    @Override
    public List<Film> findPopularFilms(int size) {

        return jdbcTemplate.query(FIND_POPULAR_FILM_DESC_QUERY, filmResultSetExtractor, size);

    }

    @Override
    public void deleteFilm(Long filmId) {
        delete(DELETE_LIKES_QUERY, filmId);
        delete(DELETE_FILM_GENRES_QUERY, filmId);
        delete(DELETE_FILM_QUERY, filmId);
    }


}


