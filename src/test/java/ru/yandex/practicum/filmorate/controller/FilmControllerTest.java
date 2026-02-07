package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    private FilmController controller;

    private Validator validator;

    @BeforeEach
    void setUp() {
        controller = new FilmController();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    @Test
    void testNameCannotBeBlank() {
        Film film = new Film();
        film.setName("");
        film.setDuration(200);
        film.setDescription("X".repeat(190));
        film.setReleaseDate(LocalDate.of(1975, 10, 10));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());

        ConstraintViolation<Film> violation = violations.iterator().next();

        assertEquals("название не может быть пустым или состоять только из пробелов", violation.getMessage());
        assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    void testDurationCannotBeNegative() {
        Film film = new Film();
        film.setName("Титаник");
        film.setDuration(-200);
        film.setDescription("X".repeat(190));
        film.setReleaseDate(LocalDate.of(1975, 10, 10));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());

        ConstraintViolation<Film> violation = violations.iterator().next();

        assertEquals("Длительность должна быть положительной (больше нуля)", violation.getMessage());
        assertEquals("duration", violation.getPropertyPath().toString());

    }

    @Test
    void testDescriptionSize201() {
        Film film = new Film();
        film.setName("Титаник");
        film.setDuration(200);
        film.setDescription("X".repeat(201));
        film.setReleaseDate(LocalDate.of(1975, 10, 10));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());

        ConstraintViolation<Film> violation = violations.iterator().next();

        assertEquals("максимальная длина описания — 200 символов", violation.getMessage());
        assertEquals("description", violation.getPropertyPath().toString());
    }

    @Test
    void testReleaseDate1894() {
        Film film = new Film();
        film.setName("Титаник");
        film.setDuration(200);
        film.setDescription("X".repeat(190));
        film.setReleaseDate(LocalDate.of(1375, 10, 10));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());

        ConstraintViolation<Film> violation = violations.iterator().next();

        assertEquals("Дата должна быть в диапазоне от 1895-12-28 до сегодняшнего дня", violation.getMessage());
        assertEquals("releaseDate", violation.getPropertyPath().toString());
    }

    // Тест 1: Создание фильма — успех
    @Test
    void createFilm_success() {
        Film film = new Film();
        film.setName("Inception");
        film.setDescription("A mind‑bending thriller.");
        film.setDuration(148);
        film.setReleaseDate(LocalDate.of(2010, 7, 16));

        Film savedFilm = controller.create(film);

        assertThat(savedFilm).isNotNull();
        assertThat(savedFilm.getId()).isEqualTo(1L);
        assertThat(savedFilm.getName()).isEqualTo("Inception");
        assertThat(controller.getAll()).hasSize(1);
    }

    // Тест 2: Создание нескольких фильмов — ID автогенерируются
    @Test
    void createMultipleFilms_generateIds() {
        Film f1 = new Film();
        f1.setName("Film 1");
        f1.setDuration(90);
        f1.setReleaseDate(LocalDate.now());

        Film f2 = new Film();
        f2.setName("Film 2");
        f2.setDuration(120);
        f2.setReleaseDate(LocalDate.now());

        Film saved1 = controller.create(f1);
        Film saved2 = controller.create(f2);

        assertThat(saved1.getId()).isEqualTo(1L);
        assertThat(saved2.getId()).isEqualTo(2L);
        assertThat(controller.getAll()).hasSize(2);
    }

    // Тест 3: Обновление фильма — успех
    @Test
    void updateFilm_success() {
        // Создаём фильм
        Film film = new Film();
        film.setName("Old Name");
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        Film saved = controller.create(film);

        // Готовим обновление
        Film update = new Film();
        update.setId(saved.getId());
        update.setName("New Name");
        update.setDescription("Updated description.");
        update.setDuration(150);
        update.setReleaseDate(LocalDate.of(2023, 1, 1));

        Film updated = controller.update(update);

        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(updated.getDescription()).isEqualTo("Updated description.");
        assertThat(updated.getDuration()).isEqualTo(150);
        assertThat(updated.getReleaseDate()).isEqualTo(LocalDate.of(2023, 1, 1));
    }

    // Тест 4: Обновление без ID — исключение
    @Test
    void updateFilm_withoutId_throwsException() {
        Film film = new Film();
        film.setName("No ID");

        RuntimeException exception = assertThrows(
                ValidationException.class,
                () -> controller.update(film)
        );

        assertThat(exception.getMessage())
                .contains("Id фильма обязательно должен быть указан.");
    }

    // Тест 5: Обновление несуществующего ID — NullPointerException (или ваша логика)
    @Test
    void updateFilm_nonExistingId_throwsException() {
        Film film = new Film();
        film.setId(999L);  // Такого ID нет
        film.setName("Update");

        assertThrows(
                ValidationException.class,  // Или ваше кастомное исключение
                () -> controller.update(film)
        );
    }

    // Тест 6: Получение всех фильмов
    @Test
    void getAllFilms() {
        // Добавляем 2 фильма
        Film f1 = new Film();
        f1.setName("Film A");
        f1.setDuration(80);
        controller.create(f1);

        Film f2 = new Film();
        f2.setName("Film B");
        f2.setDuration(100);
        controller.create(f2);

        Collection<Film> all = controller.getAll();

        assertThat(all).hasSize(2);
        assertThat(all).extracting("name")
                .containsExactlyInAnyOrder("Film A", "Film B");
    }


}