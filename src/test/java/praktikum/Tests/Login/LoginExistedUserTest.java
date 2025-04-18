package praktikum.tests.login;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.data.Methods;
import praktikum.data.User;

import java.net.HttpURLConnection;

public class LoginExistedUserTest {

    private User uniqueUser;
    private String accessToken;
    private Response response;

    @Before
    public void setUp() {
        uniqueUser  = User.random();

        Methods.createUser(uniqueUser);
    }


    @Test
    @DisplayName("Тест логина существующего пользователя")
    @Description("Тест проверяет, что логин пользователя по данным уже созданного пользователя возвращает ОК 200 и корректное тело ответа")
    public void testLoginExistingUser() {
        response = Methods.loginUser(uniqueUser);
        Methods.assertUserCreatedSuccessfully(response,uniqueUser);
    }


    @After
    @DisplayName("Удаление данных после теста")
    @Description("Выполнение логина и последующего удаления пользователя")
    public void clearData() {
        if (response.statusCode() == HttpURLConnection.HTTP_OK && response.path("accessToken") != null) {
            accessToken = response.path("accessToken");
            System.out.println(accessToken );
            Methods.deleteUser(accessToken);
        }
    }

}
