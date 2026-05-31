package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.List;

@RestController()
@RequestMapping("/users")
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody NewUserRequest request) {
        log.trace("Валидация @Valid прошла успешно");
        return userService.create(request);
    }

    @PutMapping
    public UserDto update(@Valid @RequestBody UpdateUserRequest request) {
        log.trace("Валидация @Valid прошла успешно");
        return userService.update(request);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFriends(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFriend(id, friendId);
    }


    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<UserDto> getUserFriends(@PathVariable Long id) {
        return userService.findAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> findCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.findCommonFriends(id, otherId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriends(@PathVariable Long id, @PathVariable Long friendId) {
        userService.removeFriends(id, friendId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

}
