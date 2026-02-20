package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    HashMap<Long, Set<Long>> userFriendsIds = new HashMap<>();

    @Override
    public User create(User user) {
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

    @Override
    public User update(User user) {
        if (user.getId() == null) {
            log.warn("Id пользователя не указали при обновлении");
            throw new ValidationException("Id пользователя обязательно должен быть указан.");
        } else if (!incorrectId(user)) {
            log.warn("При обновлении, указан несуществующий Id пользователя");
            throw new NotFoundException("Такого Id нет в базе");
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

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public void addFriend(User user, User friend) {

        Set<Long> uFriends = userFriendsIds.computeIfAbsent(user.getId(), id -> new HashSet<>());
        if (uFriends.contains(friend.getId())) {
            throw new ValidationException("Данный пользователь уже дружит с другим пользователем");
        }
        uFriends.add(friend.getId());

        Set<Long> fFriends = userFriendsIds.computeIfAbsent(friend.getId(), id -> new HashSet<>());
        fFriends.add(user.getId());
    }

    @Override
    public void removeFriends(User user, User friend) {


        if (userFriendsIds.containsKey(user.getId())) {
            userFriendsIds.get(user.getId()).remove(friend.getId());
        }

        if (userFriendsIds.containsKey(friend.getId())) {
            userFriendsIds.get(friend.getId()).remove(user.getId());
        }

        userFriendsIds.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    @Override
    public Collection<User> findAllFriends(User user) {
        return userFriendsIds.getOrDefault(user.getId(), Collections.emptySet()).stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> findCommonFriends(User user1, User user2) {

        Set<Long> friendsUser1 = userFriendsIds.getOrDefault(user1.getId(), Collections.emptySet());
        Set<Long> friendsUser2 = userFriendsIds.getOrDefault(user2.getId(), Collections.emptySet());


        friendsUser1.retainAll(friendsUser2);

        return friendsUser1.stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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
