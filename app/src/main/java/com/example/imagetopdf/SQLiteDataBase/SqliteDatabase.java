package com.example.imagetopdf.SQLiteDataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.imagetopdf.Class.FavoritePDF;

import java.util.ArrayList;
import java.util.List;

public class SqliteDatabase extends SQLiteOpenHelper {

    private Context context;
    private static int DATABASE_VERSION = 1;
    private static String DATABASE_NAME = "FavoritePDF.db";
    private static String TABLE_NAME = "FavoriteItem";
    private static String KEY_ID = "id";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + KEY_ID + " TEXT PRIMARY KEY," +" TEXT)";

    public SqliteDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context  = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertInfoTheDatabase(String id) {
        SQLiteDatabase db;
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_ID, id);


        long i = db.insert(TABLE_NAME,null, cv);

    }

    public boolean checkIfUserExit(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        String where = KEY_ID +" LIKE '%"+id+"%'";
        Cursor c = db.query(true, TABLE_NAME, null,
                where, null, null, null, null, null);
        if(c.getCount()>0)
            return true;
        else
            return false;

    }

    public boolean deleteData(String id)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select *  from FavoriteItem where id = ?", new String[] {id});
        if (cursor.getCount() > 0)
        {
            long result = DB.delete("FavoriteItem", "id=?", new String[]{id});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        }else
        {
            return false;
        }
    }

    public List<FavoritePDF> favoritePdf(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<FavoritePDF> favoriteModel = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null, null);
        if (cursor != null && cursor.moveToFirst()){
            cursor.moveToFirst();
            do {
                FavoritePDF favoritePDF = new FavoritePDF();
                favoritePDF.setKey(cursor.getString(0));
                favoriteModel.add(favoritePDF);

            }while (cursor.moveToNext());
        }
        else {
            favoriteModel = null;
        }
        return favoriteModel;
    }
}
