package praktikum.Tests.GettingOrder;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import praktikum.Data.Ingredients;
import praktikum.Data.Methods;
import praktikum.Data.User;

import java.util.List;

public class GettingOrderTest {
    private String accessToken;
    private User uniqueUser;
    private List<String> selectedIngredientIds;

    @Test
    @DisplayName("Получение заказа без авторизации")
    @Description("Проверяет возможность получить список  заказов без авторизации")
    public void testGettingOrderWithoutAuthorization() {
        Response response = Methods.getOrdersWithoutToken();
        Methods.assertFailGettingOrderWithoutToken(response);

    }


    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Проверяет возможность создать заказ с авторизацией, используя валидные ингредиенты")
    public void testGettingOrderWithAuthorization(){
        uniqueUser = User.random();
        Methods.createUser(uniqueUser);
        Response loginResponse = Methods.loginUser(uniqueUser);

        accessToken = loginResponse.path("accessToken");

        List<Ingredients> allIngredients = Ingredients.getAllIngredientObjects();
        selectedIngredientIds = Ingredients.getIngredientIdsByIndexes(allIngredients, 0, 1, 3);
        Response createOrderResponse = Methods.createOrderWithToken(accessToken, selectedIngredientIds);

        Response response = Methods.getOrderWithToken(accessToken);
        Methods.assertSuccessGettingOrderWithToken(response);
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
