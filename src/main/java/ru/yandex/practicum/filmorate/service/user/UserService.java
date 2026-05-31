package ru.yandex.practicum.filmorate.service.user;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;

import java.util.List;

@Service
public interface UserService {

    List<UserDto> getAll();

    UserDto create(NewUserRequest request);

    UserDto update(UpdateUserRequest request);

    UserDto getUserById(Long id);

    void addFriend(Long userId, Long friendId);

    void removeFriends(Long userId, Long friendId);

    List<UserDto> findAllFriends(Long userid);

    List<UserDto> findCommonFriends(Long userId, Long friendId);

    void deleteUser(Long userId);
}
