package com.labcoop.hw.meals.controllers;

import com.labcoop.hw.meals.models.User;

/**
 * Created by holcz on 11/03/16.
 */
public interface UserCallback {
    //TODO: ERROR handling!
    void onUserDataAvailable(User user, String error);
}
