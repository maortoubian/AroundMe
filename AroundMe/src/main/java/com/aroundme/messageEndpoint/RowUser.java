package com.aroundme.messageEndpoint;

import android.graphics.Bitmap;

/**
 * Created by maor on 19/06/2015.
 */
public class RowUser {
    private int id;
    private String title;
    private String newMsg;
    private int imageId;
    private Bitmap icon;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getImageId() {
        return imageId;
    }
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getnewMsg() {
        return newMsg;
    }
    public void setnewMsg(String newMsg) {
        this.newMsg = newMsg;
    }

    public Bitmap getIcon() {
        return icon;
    }
    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

}
