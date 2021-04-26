package com.example.blogger.models;

public class UserModel {

    String name;
    String surname;
    String email;
    String profile;

    public UserModel() {
    }

    public UserModel(String name, String surname, String email, String profile) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
