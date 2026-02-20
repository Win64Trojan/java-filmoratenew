package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.InvalidOperationException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;


@Service
@RequiredArgsConstructor
public class BaseUserService implements UserService {

    @Autowired
    private final UserStorage userStorage;

    @Override
    public User create(User user) {
        return userStorage.create(user);
    }

    @Override
    public User update(User user) {
        return userStorage.update(user);
    }

    @Override
    public User getUserById(Long id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID [" + id + "] не найден"));
    }

    @Override
    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public void addFriend(Long userId, Long friendId) {

        if (userId.equals(friendId)) {
            throw new ValidationException("Пользователь не может добавить в друзья самого себя");
        }

        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID [" + userId + "] не найден"));
        User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID [" + friendId + "] не найден"));
        userStorage.addFriend(user, friend);

    }

    @Override
    public void removeFriends(Long userId, Long friendId) {

        if (userId.equals(friendId)) {
            throw new InvalidOperationException("Пользователь не может удалить самого себя из друзей");
        }

        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID [" + userId + "] не найден"));
        User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID [" + friendId + "] не найден"));
        userStorage.removeFriends(user, friend);
    }

    @Override
    public Collection<User> findAllFriends(Long userId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID [" + userId + "] не найден"));
        return userStorage.findAllFriends(user);
    }

    @Override
    public Collection<User> findCommonFriends(Long userId, Long friendId) {

        if (userId.equals(friendId)) {
            throw new InvalidOperationException("Пользователь не может иметь общих друзей сам с собой");
        }

        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID [" + userId + "] не найден"));
        User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID [" + friendId + "] не найден"));
        return userStorage.findCommonFriends(user, friend);
    }
}
