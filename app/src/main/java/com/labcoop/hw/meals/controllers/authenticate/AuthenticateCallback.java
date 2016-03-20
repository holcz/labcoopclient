package com.labcoop.hw.meals.controllers.authenticate;

/**
 * Created by holcz on 20/03/16.
 */
public interface AuthenticateCallback {
    void authenticated(boolean success, String err);
}
