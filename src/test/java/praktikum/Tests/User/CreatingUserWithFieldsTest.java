package praktikum.Tests.User;

import io.restassured.response.Response;
import org.junit.After;
import praktikum.Data.Methods;
import praktikum.Data.User;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Collection;



@RunWith(Parameterized.class)
public class CreatingUserWithFieldsTest {
    private final String email;
    private final String password;
    private final String name;

    private User user;
    private String accessToken;

    public CreatingUserWithFieldsTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }


        @Parameterized.Parameters(name = "email={0}, password={1}, name={2}")
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {null, "hg08lwkwy7xdd", "Lacy"},
                    {"", "hg08lwkwy7xdd", "Lacy"},
                    {"neal.wunsch@hotmail.com", null, "Lacy"},
                    {"neal.wunsch@hotmail.com", "", "Lacy"},
                    {"neal.wunsch@hotmail.com", "hg08lwkwy7xdd", null},
                    {"neal.wunsch@hotmail1111.com", "hg08lwkwy7xdd", ""}
            });
    }

        @Test
        @DisplayName("Тест ошибки при создании пользователя с незаполненными необходимыми полями ")
        @Description("Тест проверяет, что создание пользователя не со  всеми требуемыми данными (Email, password или name) возвращает ошибку 403 и message \"Email, password and name are required fields\"")
        public void testCreateUserWithInvalidFields() {
            user = new User(email, password, name);


            Response response = Methods.createUser(user);
            Methods.assertFailedCreatingWithWrongData(response);
        }


    @After
    @DisplayName("Удаление данных после теста")
    @Description("Выполнение логина и последующего удаления пользователя")
    public void clearData() {

        if (user != null && user.getEmail() != null && !user.getEmail().isEmpty() && user.getPassword() != null && !user.getPassword().isEmpty()) {
            Response response = Methods.loginUser(user);

            if (response.statusCode() == HttpURLConnection.HTTP_OK && response.path("accessToken") != null) {
                accessToken = response.path("accessToken");

                Methods.deleteUser(accessToken);
                System.out.println("Пользователь был создан несмотря на ожидание ошибки — удалён вручную.");
            }
        }
    }


}
