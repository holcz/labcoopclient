package com.labcoop.hw.meals.controllers.authenticate;

/**
 * Created by holcz on 20/03/16.
 */
public interface Authenticator {
    boolean isRegistered();
    void login(String username, char[] password, AuthenticateCallback callback);
    void register(String username, char[] password, AuthenticateCallback callback);
    void logout(AuthenticateCallback callback);
}
