package com.labcoop.hw.meals.models;

/**
 * Created by holcz on 11/03/16.
 */
public class User {

    public enum Gender{
        MALE,
        FEAMLE
    }

    String id;
    String username;
    String email;
    String firstName;
    String lastName;
    Integer maxCalories;
    Gender gender;

    public User(String id, String username, String email, String firstName, String lastName, Integer maxCalories, String gender) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.maxCalories = maxCalories;
        try {
            this.gender = Gender.valueOf(gender);
        }catch (IllegalArgumentException e){
            this.gender = Gender.MALE;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getMaxCalories() {
        return maxCalories;
    }

    public void setMaxCalories(Integer maxCalories) {
        this.maxCalories = maxCalories;
    }

    public Gender getGender() {
        return gender;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", maxCalories=" + maxCalories +
                ", gender=" + gender +
                '}';
    }
}
