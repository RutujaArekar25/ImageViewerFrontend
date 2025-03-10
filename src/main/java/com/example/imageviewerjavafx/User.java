package com.example.imageviewerjavafx;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

    private String id;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("image")
    private String image;


    public String getUserName() {

        return userName;
    }

    public void setUserName(String userName) {

        this.userName = userName;
    }

    public String getImage() {

        return image;
    }

    public void setImage(String  image) {

        this.image = image;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }
}
