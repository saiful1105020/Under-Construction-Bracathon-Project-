package com.underconstruction.underconstruction;

/**
 * Created by wasif on 12/5/15.
 */

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.DatabaseUtils;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;

        import java.util.ArrayList;
        import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "tempReport.db";
    public static final String REPORT_TABLE_NAME = "tempTable";
    public static final String REPORT_COLUMN_ID = "id";
    public static final String REPORT_COLUMN_NAME = "userName";
    public static final String REPORT_COLUMN_CATEGORY = "category";
    public static final String REPORT_COLUMN_IMAGE = "image";
    public static final String REPORT_COLUMN_VIDEO = "video";
    public static final String REPORT_COLUMN_TIME = "time";
    public static final String REPORT_COLUMN_INFORMALLOCATION="informalLocation";
    public static final String REPORT_COLUMN_PROBDESCR="problemDescription";
    public static final String REPORT_COLUMN_STREETNO="streetNo";
    public static final String REPORT_COLUMN_ROUTE="route";
    public static final String REPORT_COLUMN_NEIGHBORHOOD="neighborhood";
    public static final String REPORT_COLUMN_SUBLOCALITY="sublocality";
    public static final String REPORT_COLUMN_LOCALITY="locality";
    public static final String REPORT_COLUMN_LATITUDE="latitude";
    public static final String REPORT_COLUMN_LONGITUDE="longitude";
    private HashMap hp;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table tempTable " +
                        "(id integer primary key,userName text,category text,image blob,video text,time text,informalLocation text,problemDescription text,streetNo text,route text,neighborhood text,sublocality text,locality text,latitude text,longitude text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS tempTable");
        onCreate(db);
    }

    public boolean insertRecord  (ArrayList<String> results,byte[] arr)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for(int i=0;i<results.size();i++){

            String tagAndValueString=results.get(i);
            String tag= tagAndValueString.split(":")[0];
            String value;
            if(!tag.equals("time")) {
                if (tagAndValueString.split(":").length == 1) {
                    value = "";
                } else {
                    value = tagAndValueString.split(":")[1];
                }
            }
            else{
                value=tagAndValueString.substring(tagAndValueString.indexOf(":"));
            }

            if(tag.equals("street_number"))
                tag="streetNo";
            else if(tag.equals("sublocality_level_1"))
                tag="sublocality";
            contentValues.put(tag, value);

            Log.d("internalDB_test", tag + " " + value);
        }
        contentValues.put("image",arr);
        //contentValues.put("name", name);
        //contentValues.put("phone", phone);
        //contentValues.put("email", email);
        //contentValues.put("street", street);
        //contentValues.put("place", place);
        Log.d("before_insert"," ");
        db.insert("tempTable", null, contentValues);
        Log.d("after_insert", getAllRecords().toString());

        return true;
    }

    public Cursor getData(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from tempTable where userName=\""+name+"\"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, REPORT_TABLE_NAME);
        return numRows;
    }



    public boolean updateContact (Integer id, String name, String phone, String email, String street,String place)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteRecord (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("tempTable",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getAllRecords()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from tempTable", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(REPORT_COLUMN_NAME))+" "+res.getString(res.getColumnIndex(REPORT_COLUMN_TIME)));
            res.moveToNext();
        }
        return array_list;
    }
}


