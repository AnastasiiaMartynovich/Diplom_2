import data.DataUser;
import io.restassured.response.ValidatableResponse;
import object.Order;
import object.User;
import object.UserLogin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;
import rest.OrderRest;
import rest.UserRest;

import java.util.Arrays;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateOrderTests {
    private UserRest userRest;
    private OrderRest orderRest;
    private User user;
    private Order order;
    List<String> ingredients = Arrays.asList(
            "61c0c5a71d1f82001bdaaa74",
            "61c0c5a71d1f82001bdaaa6c",
            "61c0c5a71d1f82001bdaaa77",
            "61c0c5a71d1f82001bdaaa7a");
    private String accessToken;


    @Before
    public void setUp() {
        userRest = new UserRest();
        user = DataUser.getUser();
        userRest.createUser(user);
        orderRest = new OrderRest();
    }

    @After
    public void tearDown() {
        try {
            userRest.deleteUser(accessToken);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и ингредиентами")
    public void createOrderWithAuth() {
        UserLogin userLogin = new UserLogin(user.getEmail(), user.getPassword());
        ValidatableResponse loginResponse = userRest.loginUser(userLogin);
        loginResponse.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
        accessToken = loginResponse.extract().path("accessToken");

        order = new Order(ingredients);
        ValidatableResponse orderResponse = orderRest.createOrderWithAuth(order, accessToken);
        orderResponse.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации и без ингредиентов")
    public void createOrderWithoutAuth() {
        order = new Order(ingredients);
        ValidatableResponse orderResponse = orderRest.createOrderWithoutAuth(order);
        orderResponse.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и без ингредиентов")
    public void createOrderWithoutIngredients() {
        UserLogin userLogin = new UserLogin(user.getEmail(), user.getPassword());
        ValidatableResponse loginResponse = userRest.loginUser(userLogin);
        loginResponse.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
        accessToken = loginResponse.extract().path("accessToken");

        order = new Order(null);
        ValidatableResponse orderResponse = orderRest.createOrderWithAuth(order, accessToken);
        orderResponse.assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и неверным хешем ингредиентов")
    public void createOrderWithWrongHashIngredient() {
        UserLogin userLogin = new UserLogin(user.getEmail(), user.getPassword());
        ValidatableResponse loginResponse = userRest.loginUser(userLogin);
        loginResponse.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
        accessToken = loginResponse.extract().path("accessToken");

        ingredients.set(0, "12345");
        order = new Order(ingredients);
        ValidatableResponse orderResponse = orderRest.createOrderWithAuth(order, accessToken);
        orderResponse.assertThat().statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}
