package com.labcoop.hw.meals.models;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by holcz on 14/03/16.
 */
public class Meal {

    String id;
    Long date;
    Integer calories;
    String text;

    public Meal(String id, Long date, Integer calories, String text) {
        this.id = id;
        this.date = date;
        this.calories = calories;
        this.text = text;
    }

    public Meal(Long date, Integer calories, String text) {
        this(null, date, calories, text);
    }

    public Meal(Date date, Integer calories, String text) {
        this.date = date.getTime();
        this.calories = calories;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getDateInMilis(){
        return date;
    }

    public Date getDate(){
        return new Date(this.date);
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Meal){
            Meal other = (Meal) o;
            return id.equals(other.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Meal{" +
                "id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", calories=" + calories +
                ", text='" + text + '\'' +
                '}';
    }
}
