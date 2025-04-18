package praktikum.tests.user;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import praktikum.data.Methods;
import praktikum.data.User;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.HttpURLConnection;

public class CreatingExistUserTest {
    private User uniqueUser;
    private String accessToken;
    private Response response;

    @Before
    public void setUp() {
        uniqueUser  = User.random();

        Methods.createUser(uniqueUser);
    }


    @Test
    @DisplayName("Тест создания существующего пользователя")
    @Description("Тест проверяет, что создание пользователя по данным уже созданного пользователя возвращает ошибку 403 и message \"User already exists\"")
    public void testCreateExistingUser() {
        User duplicateUser = new User(uniqueUser.getEmail(), uniqueUser.getPassword(), uniqueUser.getName());

        System.out.println("Повторное создание пользователя по имеющимся данным");

        response = Methods.createUser(duplicateUser);

        Methods.assertDuplicateUserCreation(response);
    }


    @After
    @DisplayName("Удаление данных после теста")
    @Description("Выполнение логина и последующего удаления пользователя")
    public void clearData() {
        response = Methods.loginUser(uniqueUser);

        System.out.println("Проверка, что токен не пустой");

        if (response.statusCode() == HttpURLConnection.HTTP_OK && response.path("accessToken") != null) {
            accessToken = response.path("accessToken");

            Methods.deleteUser(accessToken);
        }
    }

}
