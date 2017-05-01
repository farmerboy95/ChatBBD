package com.example.farmerboy.chatbbd.classes;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 *  Created by farmerboy on 6/26/2016.
 */
@IgnoreExtraProperties
public class ChatItem {
    private String content, uid;
    private long time;
    private boolean isMine;

    public ChatItem() {

    }

    public ChatItem(String uid, String content, long time, boolean isMine) {
        this.uid = uid;
        this.content = content;
        this.time = time;
        this.isMine = isMine;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public boolean isMine() {
        return isMine;
    }

    public void setIsMine(boolean isMine) {
        this.isMine = isMine;
    }

}
