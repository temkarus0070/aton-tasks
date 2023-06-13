package DataBusAndMicroservices;

import java.nio.charset.StandardCharsets;

public class UserDataWrapper extends UserData {

    private UserData userData;
    private String str;

    public UserDataWrapper(UserData userData) {
        this.userData = userData;
    }

    public String getName() {
        if (str == null) {
            str = new String(userData.getNameBytes(), StandardCharsets.UTF_8);
        }
        return str;
    }
}
