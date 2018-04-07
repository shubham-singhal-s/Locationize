package ado.fun.code.locationize;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by user on 2/24/2018.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Table.db";
    private static final String TABLE_NAME="timeTable";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table if not exists " + TABLE_NAME + "(days text, first_from text, first_to, second_from text, second_to text)");
}

    public boolean createTab(){
        try{
            SQLiteDatabase db=this.getWritableDatabase();
            db.execSQL("create table if not exists " + TABLE_NAME + "(days text, first_from text, first_to, second_from text, second_to text)");
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean dropTab(){
        try{
            SQLiteDatabase db=this.getWritableDatabase();
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    boolean isTableExist(){
        SQLiteDatabase db=this.getReadableDatabase();
        if(!db.isReadOnly()){
            db.close();
            db=getReadableDatabase();
        }

        Cursor c=db.rawQuery("SELECT name from sqlite_master WHERE type='table' AND name='"+ TABLE_NAME+ "'", null);
        if(c!=null){
            if(c.getCount()>0){
                c.close();
                return true;
            }
            c.close();
        }
        return false;
    }

    String returnFirstHalfTime(String day){
        String d=null;
        try{
            SQLiteDatabase db=this.getReadableDatabase();
            Cursor c=db.rawQuery("select first_from " + TABLE_NAME + " where days='"+day +"'", null);
            c.moveToFirst();
            d=c.getString(0);
            return d;
        }
        catch(Exception e){
            e.printStackTrace();
            return "fail";
        }
    }

    String returnFirstHalfEndTime(String day){
        String d=null;
        try{
            SQLiteDatabase db=this.getReadableDatabase();
            Cursor c=db.rawQuery("select first_to " + TABLE_NAME + " where days='"+day +"'", null);
            c.moveToFirst();
            d=c.getString(0);
            return d;
        }
        catch(Exception e){
            e.printStackTrace();
            return "fail";
        }
    }

    public boolean addToDB(ArrayList<String> d, ArrayList<String> first_from, ArrayList<String> first_to, ArrayList<String> second_from, ArrayList<String> second_to) {
        try{
            Log.d("Days: ", d.toString());
            Log.d("Fisrt from: ", first_from.toString());
            Log.d("First to: ", first_to.toString());
            Log.d("Second from: ", second_from.toString());
            Log.d("Second to: ", second_to.toString());
            SQLiteDatabase db = this.getWritableDatabase();
            for(int i=0; i<d.size(); i++){
                ContentValues cv=new ContentValues();
                cv.put("days", d.get(i));
                if(first_from.get(i)!=null)
                    cv.put("first_from", first_from.get(i));
                if(first_to.get(i)!=null)
                    cv.put("first_to", first_to.get(i));
                if(second_from.get(i)!=null)
                    cv.put("second_from", second_from.get(i));
                if(second_to.get(i)!=null)
                    cv.put("second_to", second_to.get(i));
                db.insert(TABLE_NAME, null, cv);
            }
            String tableString = String.format("Table %s:\n", TABLE_NAME);
            Cursor allRows = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            if (allRows.moveToFirst() ){
                String[] columnNames = allRows.getColumnNames();
                do {
                    for (String name: columnNames) {
                        tableString += String.format("%s: %s\n", name,
                                allRows.getString(allRows.getColumnIndex(name)));
                    }
                    tableString += "\n";

                } while (allRows.moveToNext());
            }
            Log.d("Rows country: ", tableString);
            return true;
        }
        catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public String returnSecondHalfTime(String day) {
        String d=null;
        try{
            SQLiteDatabase db=this.getReadableDatabase();
            Cursor c=db.rawQuery("select second_from " + TABLE_NAME + " where days='"+day +"'", null);
            c.moveToFirst();
            d=c.getString(0);
            return d;
        }
        catch(Exception e){
            e.printStackTrace();
            return "fail";
        }
    }

    public String returnSecondHalfEndTime(String day) {
        String d=null;
        try{
            SQLiteDatabase db=this.getReadableDatabase();
            Cursor c=db.rawQuery("select second_to " + TABLE_NAME + " where days='"+day +"'", null);
            c.moveToFirst();
            d=c.getString(0);
            return d;
        }
        catch(Exception e){
            e.printStackTrace();
            return "fail";
        }
    }
}
