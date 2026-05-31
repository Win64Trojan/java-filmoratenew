package ru.yandex.practicum.filmorate.dal.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> getAll();

    User create(User user);

    User update(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    void addFriend(Long userId, Long friendId);

    List<User> findAllFriends(User user);

    void removeFriends(User user, User friend);

    List<User> findCommonFriends(User user1, User user2);

    void deleteUser(Long userId);
}
