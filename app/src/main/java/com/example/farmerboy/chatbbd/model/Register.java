package com.example.farmerboy.chatbbd.model;

import java.io.Serializable;

public class Register implements Serializable{

    private int idUser;
    private String username;
    private String password;
    private String fullname;
    private String email;

    public Register() {  }

    public Register(int idUser, String username, String password, String fullname, String email) {
        this.idUser = idUser;
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.email = email;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Register{" +
                "idUser=" + idUser +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", fullname='" + fullname + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
