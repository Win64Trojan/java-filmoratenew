package ru.yandex.practicum.filmorate.dal.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public class ImplUserRepository extends BaseRepository<User> implements UserRepository {

    private static final String FIND_ALL_USERS_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String INSERT_USER_QUERY = "INSERT INTO users (user_name, email, login, birthdate) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET user_name = ?, email = ?, login = ?, birthdate = ? WHERE user_id = ?";
    private static final String ADD_FRIENDS_QUERY = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, 'CONFIRMED')";
    private static final String FIND_ALL_FRIENDS_QUERY = "SELECT u.* FROM users u JOIN friends f ON u.user_id = f.friend_id WHERE f.user_id = ?";
    private static final String REMOVE_FRIENDS_QUERY = "DELETE FROM FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?";
    private static final String FIND_COMMON_FRIENDS_QUERY = "SELECT * FROM (SELECT u.* FROM users u JOIN friends f ON u.user_id = f.friend_id WHERE f.user_id = ?) where user_id = (SELECT u.user_id FROM users u JOIN friends f ON u.user_id = f.friend_id WHERE f.user_id = ?)";


    public ImplUserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }


    @Override
    public List<User> getAll() {
        return findMany(FIND_ALL_USERS_QUERY);
    }

    @Override
    public User create(User user) {
        Long id = insert(INSERT_USER_QUERY,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        update(UPDATE_QUERY,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId()
        );

        return user;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return findOne(FIND_BY_EMAIL_QUERY, email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("Пользователь не может добавить себя в друзья");
        }

        if (areFriends(userId, friendId)) {
            throw new ValidationException("Пользователи уже являются друзьями");
        }

        update(ADD_FRIENDS_QUERY, userId, friendId);

    }

    @Override
    public void removeFriends(User user, User friend) {

        delete(REMOVE_FRIENDS_QUERY, user.getId(), friend.getId());

    }

    @Override
    public List<User> findAllFriends(User user) {
        return findMany(FIND_ALL_FRIENDS_QUERY, user.getId());
    }

    private boolean areFriends(Long userId, Long friendId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM friends WHERE user_id = ? AND friend_id = ?",
                Integer.class, userId, friendId);
        return count != null && count > 0;
    }


    @Override
    public List<User> findCommonFriends(User user1, User user2) {


        return findMany(FIND_COMMON_FRIENDS_QUERY, user1.getId(), user2.getId());
    }
}
