package com.example.wallpaper_master;

public class WallpapersModel {
    private String url;
    private int id;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public WallpapersModel(String url, int id) {
        this.url = url;
        this.id = id;
    }

    public WallpapersModel(String string, String string1) {
    }
}
