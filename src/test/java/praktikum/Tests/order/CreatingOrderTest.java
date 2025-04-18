package praktikum.tests.order;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.data.Ingredients;
import praktikum.data.Methods;
import praktikum.data.User;

import java.util.List;

public class CreatingOrderTest {
    private String accessToken;
    private User uniqueUser;
    private List<String> selectedIngredientIds;

    @Before
    public void setUp() {
        List<Ingredients> allIngredients = Ingredients.getAllIngredientObjects();
        selectedIngredientIds = Ingredients.getIngredientIdsByIndexes(allIngredients, 0, 1, 3);
        System.out.println("Выбранные ID ингредиентов: " + selectedIngredientIds);

        uniqueUser = User.random();
        Methods.createUser(uniqueUser);
        Response loginResponse = Methods.loginUser(uniqueUser);

        accessToken = loginResponse.path("accessToken");
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверяет возможность создать заказ без авторизации, используя валидные ингредиенты")
    public void testCreateOrderWithoutAuthorization() {
        Response response = Methods.createOrderWithoutToken(selectedIngredientIds);
        Methods.assertNoAuthorizedOrderCreatedSuccessfully(response);
    }


    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Проверяет возможность создать заказ с авторизацией, используя валидные ингредиенты")
    public void testCreateOrderWithAuthorization(){

        Response response = Methods.createOrderWithToken(accessToken, selectedIngredientIds);
        Methods.assertAuthorizedOrderCreatedSuccessfully(response);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверяет невозможность создать заказ без указания ингредиентов")
    public void testCreateOrderWithoutIngredients() {
        List<String> emptyIngredients = List.of();

        Response response = Methods.createOrderWithoutToken(emptyIngredients);
        Methods.assertFailOrderWithEmptyIngredients(response);
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Проверяет, что создание заказа с неверными или несуществующими ингредиентами приводит к ошибке")
    public void testCreateOrderWithInvalidIngredientHashes() {
        List<String> invalidIngredientIds  = List.of("32csat3424gs4351", "zff24324");

        Response response = Methods.createOrderWithoutToken(invalidIngredientIds );
        Methods.assertFailOrderWithInvalidIngredients(response);

        /*
        *  В реализации данного требования бага. Если невалидный ингредиент имеет значения от invalidHash0 до invalidHash9,то 400 ошибка,
        * {
            "success": false,
            "message": "One or more ids provided are incorrect"
           }
        *  если иначе - 500
        * */

    }

    @After
    @DisplayName("Удаление данных после теста")
    @Description("Выполнение логина и последующего удаления пользователя")
    public void clearData() {
        if (accessToken != null) {
            Methods.deleteUser(accessToken);
        }


    }
}
