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

public class DBHelper extends SQLiteOpenHelper {

    //Give names to each column of the table. Helps in calling and remembering

    public static final String DATABASE_NAME = "tempReport.db";
    public static final String REPORT_TABLE_NAME = "tempTable";
    public static final String REPORT_COLUMN_ID = "id";
    public static final String REPORT_COLUMN_USERID = "userID";
    public static final String REPORT_COLUMN_CATEGORY = "category";
    public static final String REPORT_COLUMN_IMAGE = "image";
    public static final String REPORT_COLUMN_TIME = "time";
    public static final String REPORT_COLUMN_INFORMALLOCATION="informalLocation";
    public static final String REPORT_COLUMN_PROBDESCR="problemDescription";
    //    public static final String REPORT_COLUMN_STREETNO="streetNo";
//    public static final String REPORT_COLUMN_ROUTE="route";
//    public static final String REPORT_COLUMN_NEIGHBORHOOD="neighborhood";
//    public static final String REPORT_COLUMN_SUBLOCALITY="sublocality";
//    public static final String REPORT_COLUMN_LOCALITY="locality";
    public static final String REPORT_COLUMN_LATITUDE="latitude";
    public static final String REPORT_COLUMN_LONGITUDE="longitude";
    //private HashMap hp;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create new table.
        db.execSQL(
                "create table tempTable " +
                        "(id integer primary key,userID text,category text,image blob,time text,informalLocation text,problemDescription text,latitude text,longitude text)"
        );
        Log.d("Database created", "yes we can");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // If table exists, drop it and create a new one. Otherwise just create a new one.
        // TODO Don't drop the table right away. Check how long it has been stagnant. Drop if only it has been sitting for a (given) long time. Otherwise add new entries.
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

            //Time is special as it has multiple ":"

            if(!tag.equals("time")) {
                if (tagAndValueString.split(":").length == 1) {
                    value = "";
                } else {
                    value = tagAndValueString.split(":")[1];
                }
            }
            else{
                //take everything starting from 1st ":" [HH:MM:SS]
                value=tagAndValueString.substring(tagAndValueString.indexOf(":"));
            }

            //edit the tags so that they match the column names in the central database (just two of them didn't match)
//            if(tag.equals("street_number"))
//                tag="streetNo";
//            else if(tag.equals("sublocality_level_1"))
//                tag="sublocality";
            contentValues.put(tag, value);

            Log.d("internalDB_test", tag + " " + value);
        }
        contentValues.put("image", arr);
        contentValues.put("userID",Utility.CurrentUser.getUserId());
        //Log.d("before_insert"," ");
        Log.d("contentValues",contentValues.toString());
        db.insert("tempTable", null, contentValues);
        Log.d("after_insert", getAllRecords().toString());

        return true;
    }

    //get all data about a single user
    public ArrayList<Report> getDataForUser(String userId) {
        // handle using ID NOT user name
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from tempTable where userID=\"" + userId + "\"", null);

        return convertCursorIntoRecordList(res);
    }

    private ArrayList<Report> convertCursorIntoRecordList(Cursor res){

        //SQLiteDatabase db = this.getReadableDatabase();
        //Cursor res =  db.rawQuery( "select * from tempTable", null );
        res.moveToFirst();
        ArrayList<Report> allReportsByAUser = new ArrayList<Report>();

        while(res.isAfterLast() == false){
            String recordID = res.getString(res.getColumnIndex(REPORT_COLUMN_ID));
            String userID = res.getString(res.getColumnIndex(REPORT_COLUMN_USERID));
            String time = res.getString(res.getColumnIndex(REPORT_COLUMN_TIME));
            String latitude = res.getString(res.getColumnIndex(REPORT_COLUMN_LATITUDE));
            String longitude = res.getString(res.getColumnIndex(REPORT_COLUMN_LONGITUDE));
            String informalLocation = res.getString(res.getColumnIndex(REPORT_COLUMN_INFORMALLOCATION));
            String problemDesc = res.getString(res.getColumnIndex(REPORT_COLUMN_PROBDESCR));
            byte[] image = res.getBlob(res.getColumnIndex(REPORT_COLUMN_IMAGE));
            String category = res.getString(res.getColumnIndex(REPORT_COLUMN_CATEGORY));

            Report insertedReport = new Report(recordID,category,image,time,informalLocation,problemDesc,latitude,longitude);
            Log.d("created report:","" + insertedReport.toString());
            allReportsByAUser.add(insertedReport);
            //array_list.add(res.getString(res.getColumnIndex(REPORT_COLUMN_ID)) +" " +res.getString(res.getColumnIndex(REPORT_COLUMN_USERID)) + " " + res.getString(res.getColumnIndex(REPORT_COLUMN_TIME)) + " " + res.getString(res.getColumnIndex(REPORT_COLUMN_LATITUDE))+" "+res.getString(res.getColumnIndex(REPORT_COLUMN_LONGITUDE)));
            res.moveToNext();
        }
        return allReportsByAUser;

    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, REPORT_TABLE_NAME);
        return numRows;
    }



    /*public boolean updateContact (Integer id, String name, String phone, String email, String street,String place)
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
    */

    public Integer deleteRecord (Integer id)
    {
        Log.d("before deleting: ",getAllRecords().toString());
        SQLiteDatabase db = this.getWritableDatabase();
        int returnId = db.delete("tempTable",
                "id = ? ",
                new String[]{Integer.toString(id)});
        Log.d("after deleting: ",getAllRecords().toString());
        return returnId;
    }

    public ArrayList<String> getAllRecords()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from tempTable", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            //Log.d("User ID: ",""+ res.getString(res.getColumnIndex(REPORT_COLUMN_USERID)));
            //Log.d("Time: ","" + res.getString(res.getColumnIndex(REPORT_COLUMN_TIME)));
            //Log.d("Latitude: ",""+res.getString(res.getColumnIndex(REPORT_COLUMN_LATITUDE)));
            //Log.d("Longitude: ",""+res.getString(res.getColumnIndex(REPORT_COLUMN_LONGITUDE)));
            array_list.add(res.getString(res.getColumnIndex(REPORT_COLUMN_ID)) +" " +res.getString(res.getColumnIndex(REPORT_COLUMN_USERID)) + " " + res.getString(res.getColumnIndex(REPORT_COLUMN_TIME)) + " " + res.getString(res.getColumnIndex(REPORT_COLUMN_LATITUDE))+" "+res.getString(res.getColumnIndex(REPORT_COLUMN_LONGITUDE)));
            res.moveToNext();
        }
        return array_list;
    }
}

