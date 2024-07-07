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


public class ChangeDataUserTests {
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
    @DisplayName("Изменение данных с авторизацией")
    public void changeDataUserWithAuth() {
        UserLogin userLogin = new UserLogin(user.getEmail(), user.getPassword());
        ValidatableResponse loginResponse = userRest.loginUser(userLogin);
        accessToken = loginResponse.extract().path("accessToken");
        ValidatableResponse updateResponse = userRest.updateUserWithAuth(DataUser.getUser(), accessToken);
        updateResponse.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Изменение данных без авторизации")
    public void changeDataUserWithoutAuth() {
        ValidatableResponse updateResponse = userRest.updateUserWithoutAuth(DataUser.getUser());
        updateResponse.assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("You should be authorised"));
    }
}
