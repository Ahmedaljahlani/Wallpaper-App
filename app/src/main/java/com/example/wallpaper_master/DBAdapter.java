package com.example.wallpaper_master;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBAdapter {

    Context c;
    SQLiteDatabase db;
    DBHelper helper;

    public DBAdapter(Context c) {
        this.c = c;
        helper = new DBHelper(c);
    }

    //OPEN DB
    public DBAdapter openDB() {
        try {
            db = helper.getWritableDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this;
    }

    //CLOSE DB
    public void closeDB() {
        try {
            helper.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //SAVE
    public long add(String url) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(Constants.URL, url);
            String sql = " INSERT INTO dd_TB (url) values ('" + url + "');";
            db.execSQL(sql);
            db.close();

            return 1;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void delete(String url) {
        openDB();
        db.delete(Constants.TB_NAME, "url=?", new String[]{url});
//        db.execSQL("delete from dd_TB");
        closeDB();
    }

    //update
    public void update(String url) {
        openDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.URL, url);
        //sqlite example
        //UPDATE dd_TB SET url="https:// youtube.com" WHERE url="https://google.com"
//        db.update(Constants.TB_NAME, contentValues, "url=?", new String[]{url});
        closeDB();
    }

    //RETRIEVE
    public Cursor getTvshow() {
        String[] columns = {Constants.ROW_ID, Constants.URL};

        return db.query(Constants.TB_NAME, columns, null, null, null, null, null);
    }
}