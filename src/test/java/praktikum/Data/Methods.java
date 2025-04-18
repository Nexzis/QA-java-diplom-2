package praktikum.data;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import praktikum.EnvConfig;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsEmptyCollection.empty;


public class Methods {

    @Step("Создание пользователя")
    public static Response createUser(User uniqueUser) {
        return EnvConfig.getSpec()
                .body(uniqueUser)
                .when()
                .post(EnvConfig.API_REGISTER)
                .then().log().status().log().body()
                .extract().response();
    }

    @Step("Проверка успешного создания пользователя")
    public static void assertUserCreatedSuccessfully(Response response, User uniqueUser) {
        response.then()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(uniqueUser.getEmail()))
                .body("user.name", equalTo(uniqueUser.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Step("Проверка ошибки при создании пользователя при использовании данных уже существующего")
    public static void assertDuplicateUserCreation(Response response) {
        response.then()
                .statusCode(HttpURLConnection.HTTP_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Step("Логин пользователя")
    public static Response loginUser(User uniqueUser) {
        LoginModel loginModel = new LoginModel(uniqueUser.getEmail(), uniqueUser.getPassword());

        return EnvConfig.getSpec()
                .body(loginModel)
                .when()
                .post(EnvConfig.API_LOGIN)
                .then().log().status().log().body()
                .extract().response();

    }

    @Step("Проверка ошибки при логине пользователя с неправильными данными")
    public static void checkLoginWithWrongData(Response response) {
        response.then().statusCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Step("Удаление пользователя")
    public static Response deleteUser(String accessToken) {
        return EnvConfig.getSpecWithToken(accessToken)
                .when()
                .delete(EnvConfig.API_DELETE)
                .then().log().status().log().body()
                .extract().response();

    }

    @Step("Проверка успешного удаления пользователя")
    public static void assertDeleteUserSuccessful(Response response) {
        response.then()
                .statusCode(HttpURLConnection.HTTP_ACCEPTED)
                .body("success", equalTo(true))
                .body("message", equalTo("User successfully removed"));
    }

    @Step("Проверка ошибки при создании пользователя с неправильными данными")
    public static void assertFailedCreatingWithWrongData(Response response) {
        response.then()
                .statusCode(HttpURLConnection.HTTP_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Step("Обновление данных пользователя авторизованным пользователем")
    public static Response patchUserData(User user, String accessToken) {
        return EnvConfig.getSpecWithToken(accessToken)
                .body(user)
                .when()
                .patch(EnvConfig.API_PATCH)
                .then().log().status().log().body()
                .extract().response();
    }

    @Step("Проверка успешного обновления данных авторизованным пользователем")
    public static void assertPatchSuccess(Response response, User expectedUser) {
        response.then()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(expectedUser.getEmail()))
                .body("user.name", equalTo(expectedUser.getName()));
    }

    @Step("Обновление данных пользователя неавторизованным пользователем")
    public static Response patchUserDataWithoutToken(User user) {
        return EnvConfig.getSpec()
                .body(user)
                .when()
                .patch(EnvConfig.API_PATCH)
                .then().log().status().log().body()
                .extract().response();
    }

    @Step("Проверка успешного обновления данных неавторизованным пользователем")
    public static void assertPatchUnauthorized(Response response) {
        response.then()
                .statusCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Step("Создание заказа неавторизованным пользователем")
    public static Response createOrderWithoutToken(List<String> ingredientIds) {
        return EnvConfig.getSpec()
                .body(Map.of("ingredients", ingredientIds))
                .when()
                .post(EnvConfig.API_ORDERS)
                .then().log().status().log().body()
                .extract().response();
    }

    @Step("Создание заказа авторизованным пользователем")
    public static Response createOrderWithToken(String accessToken, List<String> ingredientIds) {
        return EnvConfig.getSpecWithToken(accessToken)
                .body(Map.of("ingredients", ingredientIds))
                .when()
                .post(EnvConfig.API_ORDERS)
                .then().log().status().log().body()
                .extract().response();
    }

    @Step("Проверка успешного создания заказа неавторизованным пользователем")
    public static void assertNoAuthorizedOrderCreatedSuccessfully(Response response) {
        response.then()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Step("Проверка успешного создания заказа авторизованным пользователем")
    public static void assertAuthorizedOrderCreatedSuccessfully(Response response) {
        response.then()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order", notNullValue())
                .body("order._id", notNullValue())
                .body("order.number", notNullValue())
                .body("order.ingredients", notNullValue())
                .body("order.ingredients.size()", greaterThan(0))
                .body("order.owner", notNullValue())
                .body("order.owner.name", notNullValue())
                .body("order.owner.email", notNullValue())
                .body("order.status", notNullValue())
                .body("order.price", greaterThan(0))
                .body("order.createdAt", notNullValue())
                .body("order.updatedAt", notNullValue());
    }

    @Step("Проверка ошибки создания заказа без ингредиентов")
    public static void assertFailOrderWithEmptyIngredients(Response response) {
        response.then()
                .statusCode(HttpURLConnection.HTTP_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Step("Проверка ошибки создания заказа с невалидными ингредиентами")
    public static void assertFailOrderWithInvalidIngredients(Response response) {
        response.then()
                .statusCode(HttpURLConnection.HTTP_SERVER_ERROR);

    }

    @Step("Получение заказа неавторизованным пользователем")
    public static Response getOrdersWithoutToken() {
        return EnvConfig.getSpec()
                .when()
                .get(EnvConfig.API_ORDERS)
                .then().log().status().log().body()
                .extract().response();
    }

    @Step("Получение заказа авторизованным пользователем")
    public static Response getOrderWithToken(String accessToken) {
        return EnvConfig.getSpecWithToken(accessToken)
                .when()
                .get(EnvConfig.API_ORDERS)
                .then().log().status().log().body()
                .extract().response();
    }

    @Step("Проверка ошибки получения заказа неавторизованным пользователем")
    public static void assertFailGettingOrderWithoutToken(Response response) {
        response.then()
                .statusCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Step("Проверка успешного получения заказа авторизованным пользователем")
    public static void assertSuccessGettingOrderWithToken(Response response) {
        response.then()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true))
                .body("orders", not(empty()))  // orders не должен быть пустым
                .body("total", notNullValue())
                .body("totalToday", notNullValue());
    }

}


