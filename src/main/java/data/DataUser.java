package data;

import object.User;
import org.apache.commons.lang3.RandomStringUtils;

public class DataUser {
    public static User getUser() {
        String name = RandomStringUtils.randomAlphabetic(8);
        String email = RandomStringUtils.randomAlphabetic(8) + "@yandex.ru";
        String password = RandomStringUtils.randomAlphabetic(8);
        return new User(name, email, password);
    }
}
