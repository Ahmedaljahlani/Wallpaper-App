package com.example.wallpaper_master;

public class Constants {
    static final String ROW_ID = "id";
    static final String URL = "url";

    static final String DB_NAME = "wallpapers.db";
    static final String TB_NAME = "dd_TB";
    static final int DB_VERSION = 1;

    static final String CREATE_TB = "CREATE TABLE dd_TB(id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "url TEXT NOT NULL);";

    static final String DROP_TB = "DROP TABLE IF EXISTS " + TB_NAME;

}