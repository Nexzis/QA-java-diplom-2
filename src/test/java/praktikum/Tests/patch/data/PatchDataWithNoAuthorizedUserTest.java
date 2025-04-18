package praktikum.tests.patch.data;

import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.data.Methods;
import praktikum.data.User;

import java.net.HttpURLConnection;


public class PatchDataWithNoAuthorizedUserTest {

    private User uniqueUser;
    private String accessToken;
    private Response response;

    Faker faker = new Faker();

    @Before
    public void setUp() {
        uniqueUser = User.random();
        Methods.createUser(uniqueUser);
    }

    @Test
    @DisplayName("Изменение email без авторизации")
    @Description("Проверка, что пользователь не может изменить email без авторизации")
    public void testPatchEmailWithoutAuthorization() {
        uniqueUser = new User(faker.internet().emailAddress(), uniqueUser.getPassword(), uniqueUser.getName());

        System.out.println("new email:  " + uniqueUser.getEmail());

        Response response = Methods.patchUserDataWithoutToken(uniqueUser);
        Methods.assertPatchUnauthorized(response);

    }

    @Test
    @DisplayName("Изменение password без авторизации")
    @Description("Проверка, что пользователь не может изменить password без авторизации")
    public void testPatchPasswordWithoutAuthorization() {
        uniqueUser = new User(uniqueUser.getEmail(), faker.internet().password(), uniqueUser.getName());

        System.out.println("new password:  " + uniqueUser.getPassword());

        Response response = Methods.patchUserDataWithoutToken(uniqueUser);
        Methods.assertPatchUnauthorized(response);

    }

    @Test
    @DisplayName("Изменение name без авторизации")
    @Description("Проверка, что пользователь не может изменить name без авторизации")
    public void testPatchNameWithoutAuthorization() {
        uniqueUser = new User(uniqueUser.getEmail(), uniqueUser.getPassword(), faker.name().firstName());

        System.out.println("new name:  " + uniqueUser.getName());

        Response response = Methods.patchUserDataWithoutToken(uniqueUser);
        Methods.assertPatchUnauthorized(response);

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