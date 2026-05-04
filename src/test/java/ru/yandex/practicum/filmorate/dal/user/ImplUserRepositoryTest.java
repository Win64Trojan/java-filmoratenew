package ru.yandex.practicum.filmorate.dal.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.service.user.BaseUserService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JdbcTest
@AutoConfigureTestDatabase
@Import({ImplUserRepository.class, BaseUserService.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan("ru.yandex.practicum.filmorate")
class ImplUserRepositoryTest {


    @Autowired
    BaseUserService userService;

    static NewUserRequest newUserRequest() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("Test");
        newUserRequest.setEmail("test@yandex.ru");
        newUserRequest.setBirthday(LocalDate.of(1980, 1, 1));
        newUserRequest.setLogin("test");

        return newUserRequest;
    }


    @Test
    public void testCreateUserAndGetUserById() {

        UserDto user = userService.create(newUserRequest());

        assertNotNull(user);

        UserDto userDto = userService.getUserById(user.getId());

        assertThat(userDto)
                .isEqualTo(user);
    }

    @Test
    public void testUpdateUser() {
        UserDto user = userService.create(newUserRequest());
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setId(user.getId());
        updateUserRequest.setName("TestUpdate");
        updateUserRequest.setBirthday(LocalDate.of(1999, 1, 1));
        updateUserRequest.setLogin("testUpdate");


        UserDto userUp = userService.update(updateUserRequest);

        assertThat(userUp.getName())
                .isEqualTo("TestUpdate");

        assertThat(userUp.getBirthday())
                .isEqualTo(LocalDate.of(1999, 1, 1));

        assertThat(userUp.getLogin())
                .isEqualTo("testUpdate");
    }

    @Test
    public void addFriendAndRemoveFriend() {
        UserDto user1 = userService.create(newUserRequest());

        NewUserRequest newUserRequest = newUserRequest();
        newUserRequest.setName("Test2");
        newUserRequest.setEmail("test2@yandex.ru");
        newUserRequest.setLogin("test2");

        UserDto user2 = userService.create(newUserRequest);

        userService.addFriend(user1.getId(), user2.getId());

        List<UserDto> userFriends = userService.findAllFriends(user1.getId());

        assertThat(userFriends.size())
                .isEqualTo(1);

        assertThat(userFriends.getFirst()).isEqualTo(user2);

        userService.removeFriends(user1.getId(), user2.getId());

        List<UserDto> userFriendsRemoved = userService.findAllFriends(user1.getId());

        assertThat(userFriendsRemoved.size()).isEqualTo(0);

    }


}