package com.labcoop.hw.meals.views;

import java.text.SimpleDateFormat;

/**
 * Created by holcz on 16/03/16.
 */
public class MealDateFormatHelper {

    public static SimpleDateFormat dateFormater(){
        return new SimpleDateFormat("dd-MM-yyyy");
    }

    public static SimpleDateFormat timeFormater(){
        return new SimpleDateFormat("HH:mm");
    }

    public static SimpleDateFormat dateTimeFormater(){
        return new SimpleDateFormat("dd-MM-yyyy HH:mm");
    }
}
