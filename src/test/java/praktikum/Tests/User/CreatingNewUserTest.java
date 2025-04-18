package praktikum.tests.user;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import praktikum.data.Methods;
import praktikum.data.User;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;

import java.net.HttpURLConnection;

public class CreatingNewUserTest {
    private User uniqueUser;
    private String accessToken;

    @Test
    @DisplayName("Тест создания нового пользователя")
    @Description("Тест проверяет, что создание пользователя возвращает 200 и корректное тело ответа")
    public void testCreateNewUser() {
        uniqueUser  = User.random();

        Response response = Methods.createUser(uniqueUser);

        Methods.assertUserCreatedSuccessfully(response, uniqueUser);
    }


    @After
    @DisplayName("Удаление данных после теста")
    @Description("Выполнение логина и последующего удаления пользователя")
    public void clearData() {
        Response response = Methods.loginUser(uniqueUser);

        System.out.println("Проверка, что токен не пустой");

        if (response.statusCode() == HttpURLConnection.HTTP_OK && response.path("accessToken") != null) {
            accessToken = response.path("accessToken");

            Methods.deleteUser(accessToken);
        }
    }

}
