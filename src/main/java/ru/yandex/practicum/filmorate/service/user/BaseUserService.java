package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.user.UserRepository;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exceptions.InvalidOperationException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.mapper.user.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class BaseUserService implements UserService {

    @Autowired
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll() {
        log.info("Получение списка всех пользователей");
        return userRepository.getAll()
                .stream()
                .map(UserMapper::mapToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto create(NewUserRequest request) {
        log.info("Создание нового пользователя с email: {}", request.getEmail());
        validateEmailUniqueness(request.getEmail());

        if (request.getName() == null || request.getName().isBlank()) {
            log.debug("Имя пользователя не указано, используется логин: {}", request.getLogin());
            request.setName(request.getLogin());
        }

        User user = UserMapper.mapToUser(request);
        user = userRepository.create(user);
        log.info("Пользователь успешно создан с ID: {}", user.getId());
        return UserMapper.mapToUserDTO(user);
    }

    @Override
    public UserDto update(UpdateUserRequest request) {

        log.info("Обновление пользователя с ID: {}", request.getId());
        User updateUser = findUserById(request.getId());
        User updatedUser = UserMapper.updateUserFields(updateUser, request);
        updatedUser = userRepository.update(updatedUser);
        log.info("Пользователь успешно обновлён с ID: {}", updatedUser.getId());
        return UserMapper.mapToUserDTO(updatedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("Получение пользователя по ID: {}", id);
        User user = findUserById(id);
        return UserMapper.mapToUserDTO(user);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        log.info("Добавление друга {} пользователю {}", friendId, userId);
        if (userId.equals(friendId)) {
            throw new ValidationException("Пользователь не может добавить себя в друзья");
        }

        User user = findUserById(userId);
        User friend = findUserById(friendId);
        userRepository.addFriend(user.getId(), friend.getId());
        log.info("Пользователь {} добавлен в друзья к пользователю {}", friendId, userId);
    }

    @Override
    public void removeFriends(Long userId, Long friendId) {
        log.info("Удаление друга {} у пользователя {}", friendId, userId);
        if (userId.equals(friendId)) {
            throw new InvalidOperationException("Пользователь не может удалить самого себя из друзей");
        }

        User user = findUserById(userId);
        User friend = findUserById(friendId);
        userRepository.removeFriends(user, friend);
        log.info("Пользователь {} удалён из друзей пользователя {}", friendId, userId);
    }

    @Override
    public List<UserDto> findAllFriends(Long userId) {
        log.info("Получение списка друзей пользователя {}", userId);
        User user = findUserById(userId);

        return userRepository.findAllFriends(user)
                .stream()
                .map(UserMapper::mapToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> findCommonFriends(Long userId, Long friendId) {
        log.info("Поиск общих друзей между пользователями {} и {}", userId, friendId);
        if (userId.equals(friendId)) {
            throw new InvalidOperationException("Пользователь не может иметь общих друзей сам с собой");
        }

        User user = findUserById(userId);
        User friend = findUserById(friendId);
        return userRepository.findCommonFriends(user, friend)
                .stream()
                .map(UserMapper::mapToUserDTO)
                .collect(Collectors.toList());
    }

    private User findUserById(Long userId) {
        log.debug("Поиск пользователя по ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID [" + userId + "] не найден"));
    }

    private void validateEmailUniqueness(String email) {
        log.debug("Проверка уникальности email: {}", email);
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            log.warn("Попытка создания пользователя с уже существующим email: {}", email);
            throw new ValidationException("Данный имейл уже используется");
        }
    }
}
