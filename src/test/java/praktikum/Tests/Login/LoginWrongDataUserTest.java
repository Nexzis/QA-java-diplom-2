package praktikum.Tests.Login;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import praktikum.Data.Methods;
import praktikum.Data.User;
import java.util.Arrays;
import java.util.Collection;
import com.github.javafaker.Faker;

import java.net.HttpURLConnection;

@RunWith(Parameterized.class)
public class LoginWrongDataUserTest {

    private final String email;
    private final String password;
    private static User uniqueUser;
    private String accessToken;
    private Response response;

    public LoginWrongDataUserTest(String email, String password) {
        this.email = email;
        this.password = password;
    }


    @Before
    public void setUp() {
        uniqueUser  = User.random();
        Response response = Methods.createUser(uniqueUser);
        Methods.assertUserCreatedSuccessfully(response, uniqueUser);
    }

    // Параметры для тестов
    @Parameterized.Parameters(name = "email={0}, password={1}")
    public static Collection<Object[]> data() {
        Faker faker = new Faker();
        String baseEmail  = faker.internet().emailAddress();
        String basePassword  = faker.internet().password();

        return Arrays.asList(new Object[][]{
                { baseEmail +"1", basePassword     }, // неправильный мейл
                { baseEmail     , basePassword +"1"}, // неправильный пароль
                { ""            , basePassword     }, //  пустой мейл
                { baseEmail     , ""               }, //  пустой пароль
        });
    }


    @Test
    @DisplayName("Тест логина пользователя с неправильными данными ")
    @Description("Тест проверяет, что логин пользователя по данным уже созданного пользователя возвращает 401 Unauthorized и корректное message \"email or password are incorrect\"")
    public void testLoginUserWithWrongData() {
        User testUser = new User(email, password, uniqueUser.getName());
        response = Methods.loginUser(testUser);
        Methods.checkLoginWithWrongData(response);
    }


    @After
    @DisplayName("Удаление данных после теста")
    @Description("Выполнение логина и последующего удаления пользователя")
    public void clearData() {
        Response loginResponse = Methods.loginUser(uniqueUser);

        if (loginResponse.statusCode() == HttpURLConnection.HTTP_OK && loginResponse.path("accessToken") != null) {
            accessToken = loginResponse.path("accessToken");
            System.out.println(accessToken );
            Methods.deleteUser(accessToken);
        }
    }

}
