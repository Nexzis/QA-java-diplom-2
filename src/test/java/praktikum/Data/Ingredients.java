package praktikum.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.qameta.allure.Step;
import praktikum.EnvConfig;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Ingredients {
    @JsonProperty("_id")
    private String id;
    private String name;
    private String type;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    // Метод для получения объектов Ingredients
    @Step("Получение объектов Ingredients")
    public static List<Ingredients> getAllIngredientObjects() {
        IngredientsResponse response = EnvConfig.getSpec()
                .when()
                .get(EnvConfig.API_INGREDIENTS)
                .then()
                .statusCode(HttpURLConnection.HTTP_OK)
                .extract()
                .as(IngredientsResponse.class);

        return response.getData();
    }

    // Метод для получения ингредиентов по индексам
    @Step("Получение ингредиентов по индексам")
    public static List<String> getIngredientIdsByIndexes(List<Ingredients> ingredients, Integer... indexes) {
        return List.of(indexes).stream()
                .map(ingredients::get)
                .map(Ingredients::getId)
                .collect(Collectors.toList());
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IngredientsResponse {
        private boolean success;
        private List<Ingredients> data;

        public boolean isSuccess() {
            return success;
        }

        public List<Ingredients> getData() {
            return data;
        }
    }
}
