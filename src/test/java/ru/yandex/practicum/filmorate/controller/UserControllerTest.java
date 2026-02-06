package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    private UserController controller;

    @BeforeEach
    void setUp() {
        controller = new UserController();
    }

    // Тест 1: Создание пользователя — успех (имя задано)
    @Test
    void createUser_withName_success() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userlogin");
        user.setName("Alice");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        User saved = controller.create(user);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getName()).isEqualTo("Alice");
        assertThat(saved.getEmail()).isEqualTo("user@example.com");
        assertThat(controller.getAll()).hasSize(1);
    }

    // Тест 2: Создание пользователя — имя не задано → берётся из логина
    @Test
    void createUser_withoutName_usesLoginAsName() {
        User user = new User();
        user.setEmail("user2@example.com");
        user.setLogin("boblogin");
        // name не задано

        User saved = controller.create(user);

        assertThat(saved.getName()).isEqualTo("boblogin");  // имя = логин
        assertThat(saved.getLogin()).isEqualTo("boblogin");
    }

    // Тест 3: Создание пользователя — дубликат email → исключение
    @Test
    void createUser_duplicateEmail_throwsException() {
        // Сначала создаём пользователя
        User u1 = new User();
        u1.setEmail("unique@example.com");
        u1.setLogin("login1");
        controller.create(u1);

        // Пытаемся создать второго с тем же email
        User u2 = new User();
        u2.setEmail("unique@example.com");  // тот же email
        u2.setLogin("login2");

        RuntimeException exception = assertThrows(
                ValidationException.class,
                () -> controller.create(u2)
        );

        assertThat(exception.getMessage())
                .contains("Данный Email уже зарегистрирован в системе");
    }

    // Тест 4: Обновление пользователя — успех
    @Test
    void updateUser_success() {
        // Создаём пользователя
        User user = new User();
        user.setEmail("old@example.com");
        user.setLogin("oldlogin");
        user.setName("Old Name");
        user.setBirthday(LocalDate.of(1980, 1, 1));
        User saved = controller.create(user);

        // Готовим обновление
        User update = new User();
        update.setId(saved.getId());
        update.setEmail("new@example.com");
        update.setLogin("newlogin");
        update.setName("New Name");
        update.setBirthday(LocalDate.of(1995, 3, 10));

        User updated = controller.update(update);

        assertThat(updated.getEmail()).isEqualTo("new@example.com");
        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(updated.getBirthday()).isEqualTo(LocalDate.of(1995, 3, 10));
    }

    // Тест 5: Обновление — id не указан → исключение
    @Test
    void updateUser_withoutId_throwsException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");

        Exception exception = assertThrows(
                ValidationException.class,
                () -> controller.update(user)
        );

        assertThat(exception.getMessage())
                .contains("Id пользователя обязательно должен быть указан.");
    }

    // Тест 6: Обновление — несуществующий id → исключение
    @Test
    void updateUser_nonExistingId_throwsException() {
        User user = new User();
        user.setId(999L);  // Такого id нет
        user.setEmail("test@example.com");
        user.setLogin("testlogin");

        Exception exception = assertThrows(
                ValidationException.class,
                () -> controller.update(user)
        );

        assertThat(exception.getMessage())
                .contains("Такого Id нет в базе");
    }

    // Тест 7: Обновление — дубликат email → исключение
    @Test
    void updateUser_duplicateEmail_throwsException() {
        // Создаём первого пользователя
        User u1 = new User();
        u1.setEmail("first@example.com");
        u1.setLogin("login1");
        controller.create(u1);

        // Создаём второго
        User u2 = new User();
        u2.setEmail("second@example.com");
        u2.setLogin("login2");
        User saved2 = controller.create(u2);

        // Пытаемся обновить u2, указав email u1
        User update = new User();
        update.setId(saved2.getId());
        update.setEmail("first@example.com");  // дубликат
        update.setLogin("newlogin");

        Exception exception = assertThrows(
                ValidationException.class,
                () -> controller.update(update)
        );

        assertThat(exception.getMessage())
                .contains("Данный Email уже зарегистрирован в системе");
    }

    // Тест 8: Обновление — имя не задано → берётся из логина
    @Test
    void updateUser_withoutName_usesLoginAsName() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("mylogin");
        user.setName("Original Name");
        User saved = controller.create(user);

        User update = new User();
        update.setId(saved.getId());
        update.setEmail("updated@example.com");
        update.setLogin("newlogin");
        // name не задано

        User updated = controller.update(update);

        assertThat(updated.getName()).isEqualTo("Original Name");  // имя = новый логин
    }

    // Тест 9: Получение всех пользователей
    @Test
    void getAllUsers() {
        User u1 = new User();
        u1.setEmail("a@example.com");
        u1.setLogin("loginA");
        controller.create(u1);

        User u2 = new User();
        u2.setEmail("b@example.com");
        u2.setLogin("loginB");
        controller.create(u2);

        Collection<User> all = controller.getAll();

        assertThat(all).hasSize(2);
        assertThat(all).extracting("email")
                .containsExactlyInAnyOrder("a@example.com", "b@example.com");
    }
}