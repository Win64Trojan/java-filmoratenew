package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    User create(User user);

    User update(User user);

    Optional<User> getUserById(Long id);

    Collection<User> getAll();

    void addFriend(User user, User friend);

    void removeFriends(User user, User friend);

    Collection<User> findAllFriends(User user);

    Collection<User> findCommonFriends(User user1, User user2);
}
