package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserTest {

    @Test
    void creatObjectUser() {
        User user = new User();
        user.setEmail("email");
        user.setLogin("login");
        user.setName("name");
        user.setId(1L);
        user.setBirthday(LocalDate.now());

        assertNotNull(user);
    }
}