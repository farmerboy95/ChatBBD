package com.example.farmerboy.chatbbd.classes;

import android.net.Uri;

/**
 *  Created by farmerboy on 7/8/2016.
 */
public class FriendListItem {

    private String uid, name, email;
    private Uri image;
    private int type;

    public FriendListItem(String uid, String name, String email, Uri image, int type) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.image = image;
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
