package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {

    Long id;
    String email;
    String login;
    String name;
    LocalDate birthday;

    public boolean hasEmail() {
        return !(email == null || email.isEmpty());
    }

    public boolean hasLogin() {
        return !(login == null || login.isEmpty());
    }

    public boolean hasName() {
        return !(name == null || name.isEmpty());
    }

    public boolean hasBirthday() {
        return !(birthday == null);
    }
}
