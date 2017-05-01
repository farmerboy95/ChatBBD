package com.example.farmerboy.chatbbd.classes;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 *  Created by farmerboy on 5/1/2017.
 */
public class ChatListDetails {
    private String name, content, id;
    private Uri image;
    private long time;
    private boolean read;

    public ChatListDetails(String name, String content, long time, Uri image, boolean read, String id) {
        this.name = name;
        this.content = content;
        this.time = time;
        this.image = image;
        this.read = read;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
