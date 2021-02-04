package com.homeout.SQL;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.homeout.Memo;

import java.util.ArrayList;

public class SQLiteHelper {

    private static final String dbName = "memoDb";
    private static final String table ="memoTable";
    private static final int dbVersion =1;

    private OpenHelper openHelper;
    private SQLiteDatabase db;

    private Context context;

    public SQLiteHelper(Context context) {
        // DB 생성 (생성된 db가 없을 경우에 한번만 호출)
        this.context = context;
        this.openHelper = new OpenHelper(context, dbName, null, dbVersion);
        db = openHelper.getWritableDatabase();
    }

    private class OpenHelper extends SQLiteOpenHelper{

        public OpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //테이블 생성
            String create ="CREATE TABLE " + table + "(" +
                    "number integer PRIMARY KEY AUTOINCREMENT," +
                    "context text, " +
                    "memoDate text, " +
                    "state text);";
            sqLiteDatabase.execSQL(create);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + table);
            onCreate(sqLiteDatabase);
        }
    }


    //Insert
    public void insertMemo(Memo memo){
        String sql = "INSERT INTO " + table +" (context, memoDate, state)" + "VALUES('"+ memo.getContext() + "','" +memo.getDate() + "','" +
                memo.getState() + "');";
        db.execSQL(sql);
    }

    //Delete
    public void deleteMemo(int postion){
        String sql = "DELETE FROM " + table + "WHERE number =" + postion +";";
        db.execSQL(sql);
    }

    //Select
    public ArrayList<Memo> selectAll(){
        String sql = "SELECT * FROM " + table +";";

        ArrayList<Memo> list = new ArrayList<Memo>();

        //db에서 데이터를 찾아와서 list에 추가하는 작업
        Cursor result = db.rawQuery(sql, null);
        result.moveToFirst();

        while (!result.isAfterLast()){
            //int number, String context, String date, String state
            Memo memo = new Memo(result.getInt(0), result.getString(1), result.getString(2), result.getString(3));
            list.add(memo);
            result.moveToNext();
        }
        result.close();
        return list;
    }

}
