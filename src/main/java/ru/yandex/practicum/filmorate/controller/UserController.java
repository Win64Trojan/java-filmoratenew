package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController()
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final Map<Long, User> users = new HashMap<>();


    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.trace("Проверка валидации и создание пользователя({})", user);
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Не было указано имя. Имя будет присвоено из логина");
            user.setName(user.getLogin());
        }
        log.trace("Проверка на дубликат Email при добавлении");
        if (searchDuplicateEmail(user)) {
            log.warn("Добавить данного пользователя невозможно, данный Email уже используется");
            throw new ValidationException("Данный Email уже зарегистрирован в системе, используйте другой");
        }
        log.trace("Генерация Id");
        user.setId(generateId());
        log.trace("Добавление пользователя в базу");
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (user.getId() == null) {
            log.warn("Id пользователя не указали при обновлении");
            throw new ValidationException("Id пользователя обязательно должен быть указан.");
        } else if (!incorrectId(user)) {
            log.warn("При обновлении, указан несуществующий Id пользователя");
            throw new ValidationException("Такого Id нет в базе");
        }
        User updatedUser = users.get(user.getId());
        log.trace("Проверка на дубликат Email при обновлении");
        if (searchDuplicateEmail(user)) {
            User existingUser = findUserByEmail(user);
            if (existingUser != null
                    && existingUser.getId().equals(updatedUser.getId())) {
                log.debug("Email не изменён при обновлении, проверка пропущена");
            } else {
                log.warn("Обновить Email невозможно, данный Email уже используется");
                throw new ValidationException("Данный Email уже зарегистрирован в системе, используйте другой");
            }
        }

        log.trace("Обновление Email");
        updatedUser.setEmail(user.getEmail());
        log.trace("Дополнительная проверка, на имя пользователя");
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Не было указано имя. Имя будет присвоено из первоначальное регистрации");
            updatedUser.setName(updatedUser.getName());
        } else {
            log.trace("Обновление имени");
            updatedUser.setName(user.getName());
        }

        log.trace("Дополнительная проверка, на логин");
        if (searchDuplicateLogin(user, updatedUser)) {
            log.trace("Данный логин уже занят");
            throw new ValidationException("Данный логин занят, укажите другой");
        } else {
            log.trace("Обновление логина");
            updatedUser.setLogin(user.getLogin());
        }


        log.trace("Обновление дня рождения");
        if (user.getBirthday() == null) {
            log.trace("День рождения не было передано, будет использовано старое значение.>");
        } else {
            updatedUser.setBirthday(user.getBirthday());
        }
        return updatedUser;
    }

    @GetMapping
    public Collection<User> getAll() {
        return users.values();
    }

    private Long generateId() {

        long currentId = users.keySet().stream().mapToLong(id -> id).max().orElse(0L);

        return ++currentId;
    }

    private boolean searchDuplicateEmail(User user) {
        return users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()));
    }

    boolean incorrectId(User user) {
        return users.keySet().stream().anyMatch(id -> id.equals(user.getId()));
    }

    private User findUserByEmail(User user) {
        return users.values().stream().filter(u -> u.getEmail().equals(user.getEmail())).findFirst().orElse(null);
    }

    private boolean searchDuplicateLogin(User user, User updatedUser) {
        User existingUser = users.values().stream().filter(u -> u.getLogin().equals(user.getLogin())).findFirst().orElse(null);

        return existingUser != null && !existingUser.getId().equals(updatedUser.getId());


    }
}
