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

public class GetOrderTests {
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
    @DisplayName("Получение заказов авторизованного пользователя")
    public void getOrderWithAuth() {
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

        ValidatableResponse getOrderResponseWithAuth = orderRest.getOrderWithAuth(accessToken);
        getOrderResponseWithAuth.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Получение заказа неавторизованного пользователя")
    public void getOrderWithoutAuth() {
        order = new Order(ingredients);
        ValidatableResponse orderResponseWithoutAuth = orderRest.createOrderWithoutAuth(order);
        orderResponseWithoutAuth.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("order.number", notNullValue());

        ValidatableResponse getOrderResponseWithoutAuth = orderRest.getOrderWithoutAuth();
        getOrderResponseWithoutAuth.assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("You should be authorised"));
    }
}
