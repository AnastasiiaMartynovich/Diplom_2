import data.DataUser;
import io.restassured.response.ValidatableResponse;
import object.User;
import object.UserLogin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;
import rest.UserRest;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;

public class LoginUserTests {
    private UserRest userRest;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userRest = new UserRest();
        user = DataUser.getUser();
        userRest.createUser(user);
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
    @DisplayName("Логин под существующим пользователем")
    public void loginExistingUser() {
        UserLogin userLogin = new UserLogin(user.getEmail(), user.getPassword());
        ValidatableResponse loginResponse = userRest.loginUser(userLogin);
        loginResponse.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
        accessToken = loginResponse.extract().path("accessToken");
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    public void loginWrongPassword() {
        UserLogin userLogin = new UserLogin(user.getEmail(), "qwerty");
        ValidatableResponse loginResponse = userRest.loginUser(userLogin);
        loginResponse.assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин с неверным email (логином)")
    public void loginWrongEmail() {
        UserLogin userLogin = new UserLogin("zjh3hk2", user.getPassword());
        ValidatableResponse loginResponse = userRest.loginUser(userLogin);
        loginResponse.assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }
}
