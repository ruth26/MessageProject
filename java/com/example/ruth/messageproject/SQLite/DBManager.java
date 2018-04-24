package com.example.ruth.messageproject.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DBManager extends SQLiteOpenHelper {
    public static final String DB_NAME="myDb";
    public DBManager(Context context){
        super(context,DB_NAME,null,1);
    }




    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS users_activity (id STRING PRIMARY KEY, name c(50) NOT NULL, email VARCHAR(15) NOT NULL, `password` VARCHAR(100) NOT NULL);");
        db.execSQL("CREATE TABLE IF NOT EXISTS chats (id STRING PRIMARY KEY, chatName VARCHAR NOT NULL, OWNER VARCHAR NOT NULL DEFAULT 1);");
        db.execSQL("CREATE TABLE IF NOT EXISTS massages (id STRING PRIMARY KEY, owner VARCHAR(30), content VARCHAR(250))");
    }

    @Override//called when existing db with bigger version requested
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //
    }
}