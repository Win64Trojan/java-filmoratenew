package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
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
public class BaseUserService implements UserService {

    @Autowired
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll()
                .stream()
                .map(UserMapper::mapToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto create(NewUserRequest request) {

        Optional<User> alredyExistsUser = userRepository.findByEmail(request.getEmail());
        if (alredyExistsUser.isPresent()) {
            throw new ValidationException("Данный имейл уже используется");
        }

        if (request.getName() == null || request.getName().isBlank()) {
            request.setName(request.getLogin());
        }

        User user = UserMapper.mapToUser(request);

        user = userRepository.create(user);

        return UserMapper.mapToUserDTO(user);
    }

    @Override
    public UserDto update(UpdateUserRequest request) {

        User updateUser = userRepository.findById(request.getId())
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        updateUser = userRepository.update(updateUser);

        return UserMapper.mapToUserDTO(updateUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID [" + id + "] не найден"));

        return UserMapper.mapToUserDTO(user);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID [" + userId + "] не найден"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID [" + friendId + "] не найден"));
        userRepository.addFriend(user.getId(), friend.getId());

    }

    @Override
    public void removeFriends(Long userId, Long friendId) {

        if (userId.equals(friendId)) {
            throw new InvalidOperationException("Пользователь не может удалить самого себя из друзей");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID [" + userId + "] не найден"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID [" + friendId + "] не найден"));
        userRepository.removeFriends(user, friend);
    }

    @Override
    public List<UserDto> findAllFriends(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID [" + userId + "] не найден"));

        return userRepository.findAllFriends(user)
                .stream().map(UserMapper::mapToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> findCommonFriends(Long userId, Long friendId) {

        if (userId.equals(friendId)) {
            throw new InvalidOperationException("Пользователь не может иметь общих друзей сам с собой");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID [" + userId + "] не найден"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID [" + friendId + "] не найден"));
        return userRepository.findCommonFriends(user, friend)
                .stream().
                map(UserMapper::mapToUserDTO)
                .collect(Collectors.toList());
    }
}
