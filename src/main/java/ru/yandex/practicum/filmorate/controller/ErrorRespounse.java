package ru.yandex.practicum.filmorate.controller;

public class ErrorRespounse extends RuntimeException {

    String error;

    public ErrorRespounse(String error) {
        this.error = error;
    }
}
