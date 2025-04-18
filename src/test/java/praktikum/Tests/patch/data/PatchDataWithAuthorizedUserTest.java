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

public class PatchDataWithAuthorizedUserTest {

    private User uniqueUser;
    private String accessToken;
    private Response response;

    Faker faker = new Faker();

    @Before
    public void setUp() {
        uniqueUser = User.random();
        Methods.createUser(uniqueUser);
        response = Methods.loginUser(uniqueUser);

        if (response.statusCode() == HttpURLConnection.HTTP_OK && response.path("accessToken") != null) {
            accessToken = response.path("accessToken");
            System.out.println(accessToken);
        }

    }

    @Test
    @DisplayName("Изменение email авторизованного пользователя")
    @Description("Проверка, что пользователь может изменить свой email будучи авторизованным")
    public void testPatchEmailWithAuthorizedUser() {
        uniqueUser = new User(faker.internet().emailAddress(), uniqueUser.getPassword(),uniqueUser.getName());

        System.out.println("new email:  " + uniqueUser.getEmail());
        Response response = Methods.patchUserData(uniqueUser, accessToken);
        Methods.assertPatchSuccess(response, uniqueUser);

    }


    @Test
    @DisplayName("Изменение password авторизованного пользователя")
    @Description("Проверка, что пользователь может изменить свой password будучи авторизованным")
    public void testPatchPasswordWithAuthorizedUser() {
        uniqueUser = new User(uniqueUser.getEmail(), faker.internet().password(), uniqueUser.getName());

        System.out.println("new password:  " + uniqueUser.getPassword());

        Response response = Methods.patchUserData(uniqueUser, accessToken);
        Methods.assertPatchSuccess(response, uniqueUser);

        System.out.println("Проверим, что может залогиниться после изменения данных");
        Methods.loginUser(uniqueUser);
    }

    @Test
    @DisplayName("Изменение name авторизованного пользователя")
    @Description("Проверка, что пользователь может изменить свой name будучи авторизованным")
    public void testPatchNameWithAuthorizedUser() {
        uniqueUser = new User(uniqueUser.getEmail(), uniqueUser.getPassword(), faker.name().firstName());

        System.out.println("new name:  " + uniqueUser.getName());
        Response response = Methods.patchUserData(uniqueUser, accessToken);
        Methods.assertPatchSuccess(response, uniqueUser);

    }

    @After
    @DisplayName("Удаление данных после теста")
    @Description("Выполнение логина и последующего удаления пользователя")
    public void clearData() {

            System.out.println(accessToken );
            Methods.deleteUser(accessToken);
    }

}