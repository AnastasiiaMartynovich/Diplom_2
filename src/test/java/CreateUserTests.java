import data.DataUser;
import io.restassured.response.ValidatableResponse;
import object.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;
import rest.UserRest;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;

public class CreateUserTests {
    private UserRest userRest;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userRest = new UserRest();
        user = DataUser.getUser();
    }

    @After
    public void cleanUp() {
        try {
            userRest.deleteUser(accessToken);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    public void createNewUser() {
        ValidatableResponse createResponse = userRest.createUser(user);
        createResponse.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
        accessToken = createResponse.extract().path("accessToken");
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    public void createExistingUser() {
        ValidatableResponse createResponseFirst = userRest.createUser(user);
        ValidatableResponse createResponseSecond = userRest.createUser(user);
        createResponseSecond.assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("User already exists"));
        accessToken = createResponseFirst.extract().path("accessToken");
    }

    @Test
    @DisplayName("Создание пользователя с незаполненным полем name")
    public void createUserWithoutName() {
        user.setName(null);
        ValidatableResponse createResponse = userRest.createUser(user);
        createResponse.assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя с не заполненным полем email")
    public void createUserWithoutEmail() {
        user.setEmail(null);
        ValidatableResponse createResponse = userRest.createUser(user);
        createResponse.assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя с не заполненным полем password")
    public void createUserWithoutPassword() {
        user.setPassword(null);
        ValidatableResponse createResponse = userRest.createUser(user);
        createResponse.assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
