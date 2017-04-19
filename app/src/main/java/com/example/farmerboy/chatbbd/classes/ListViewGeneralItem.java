package com.example.farmerboy.chatbbd.classes;

/**
 *  Created by farmerboy on 4/18/2017.
 */
public class ListViewGeneralItem {
    private String name;
    private int icon;

    public ListViewGeneralItem(int icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
