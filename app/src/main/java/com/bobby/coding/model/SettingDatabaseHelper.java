package com.bobby.coding.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bobby.coding.utils.DatabaseScope;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

//Utility class for SQLite that saves bookmarks
@Module
public class SettingDatabaseHelper extends SQLiteOpenHelper {
    private static final String Database_Name="setting.db";
    private static final String Table_Name="setting_table";
    private static final String COL_1="ID";
    private static final String COL_2="count";
    Context context;

    @Provides
    @DatabaseScope
    SettingDatabaseHelper provideDatabase(Context context){
        return new SettingDatabaseHelper(context);
    }

    @Provides
    @DatabaseScope
    Context getContext(){
        return context;
    }

    private SQLiteDatabase db;
    public SettingDatabaseHelper(Context context) {
        super(context, Database_Name, null,1);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table setting_table(ID INTEGER PRIMARY KEY AUTOINCREMENT,count TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Table_Name);
        onCreate(db);
    }

    public boolean insertdata(String count)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues content=new ContentValues();
        content.put(COL_2,count);

        long range=db.insert(Table_Name, null, content);
        if(range == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public Cursor getalldata()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor b1=db.rawQuery("Select * from "+Table_Name,null);
        return b1;
    }


    public boolean deletedata(int id)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        long range=db.delete(Table_Name, null,null);
        if(range>0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}

