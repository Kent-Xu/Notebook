package com.example.notebook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DBService extends SQLiteOpenHelper {
    public static final String TABLE = "notes";
    public static final String ID = "_id";
    public static final String TITLE ="title";
    public static final String AUTHOR ="author";
    public static final String CONTENT = "content";
    public static final String TIME = "time";

  public static final  String sql = "CREATE TABLE notes"+"( "+ID+
          " INTEGER PRIMARY KEY AUTOINCREMENT, "+
          AUTHOR +" VARCHAR(30) ,"+
          TITLE +" VARCHAR(30) ,"+
          CONTENT + " TEXT , "+
          TIME + " DATETIME NOT NULL )";
    public DBService(Context context) {
        super(context,"notepad.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

}