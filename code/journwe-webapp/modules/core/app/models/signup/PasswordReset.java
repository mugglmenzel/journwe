package models.signup;

import controllers.core.html.AccountController;

/**
 * Created by mugglmenzel on 22/02/14.
 */
public class PasswordReset extends AccountController.PasswordChange {

    public PasswordReset() {
    }

    public PasswordReset(final String token) {
        this.token = token;
    }

    public String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}