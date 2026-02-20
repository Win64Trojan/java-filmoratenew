package ru.yandex.practicum.filmorate.service.user;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Service
public interface UserService {

    User create(User user);

    User update(User user);

    User getUserById(Long id);

    Collection<User> getAll();

    void addFriend(Long userId, Long friendId);

    void removeFriends(Long userId, Long friendId);

    Collection<User> findAllFriends(Long userid);

    Collection<User> findCommonFriends(Long userId, Long friendId);
}
