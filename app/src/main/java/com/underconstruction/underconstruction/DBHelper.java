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

/**
 * THis class handles all the necessary methods to store a user post temporarily in the SQLite database if internet is absent. With this class, we can create a new database, create a new table, insert report temporarily in the database, extract all the reports, delete a particular reports and all the necessary
 * helper methods
 */
public class DBHelper extends SQLiteOpenHelper {

    //Give names to each column of the table. Helps in calling and remembering
    //name of the SQLite database.
    public static final String DATABASE_NAME = "tempReport.db";
    //name of the table
    public static final String REPORT_TABLE_NAME = "tempTable";
    //id of a row which is the primary key of the table
    public static final String REPORT_COLUMN_ID = "id";
    //the id of the user who has inserted the report
    public static final String REPORT_COLUMN_USERID = "userID";
    //the category of the report posted by the user
    public static final String REPORT_COLUMN_CATEGORY = "category";
    //the image captured by the user
    public static final String REPORT_COLUMN_IMAGE = "image";
    //the time of capturing the report
    public static final String REPORT_COLUMN_TIME = "time";
    //user provided description of the location
    public static final String REPORT_COLUMN_INFORMALLOCATION="informalLocation";
    //user provided description of the problem
    public static final String REPORT_COLUMN_PROBDESCR="problemDescription";
    //the latitude of the location
    public static final String REPORT_COLUMN_LATITUDE="latitude";
    //the longitude of the location
    public static final String REPORT_COLUMN_LONGITUDE="longitude";

    /**
     *  This method creates a new database using the super constructor
     */

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    /**
     * //given a writable database, creates a new table
     * @param db a reference to the writable database
     */


    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create new table.
        db.execSQL(
                "create table if not exists tempTable " +
                        "(id integer primary key autoincrement,userID text,category text,image blob,time text,informalLocation text,problemDescription text,latitude text,longitude text)"
        );
        Log.d("table created", "yes we can");
    }


    /**
     * Used to drop a table and replace it with another one.
     * @param db reference to the writable database
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // If table exists, drop it and create a new one. Otherwise just create a new one.
        // TODO Don't drop the table right away. Check how long it has been stagnant. Drop if only it has been sitting for a (given) long time. Otherwise add new entries.
        db.execSQL("DROP TABLE IF EXISTS tempTable");
        onCreate(db);
    }

    /**
     *
     * Given an arraylist of the attributes as string, inserts a new record in SQLite Database.
     * @param results all the attributes of a report,latitude, longitude, image, informal location etc.
     * @param arr  the byte array representation of the captured image
     * @return returns true if the report has been inserted successfully
     */
    public boolean insertRecord  (ArrayList<String> results,byte[] arr)
    {
        //Get hold of a writable database.

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        for(int i=0;i<results.size();i++){
            //format all the fields. They are separated by a :
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
                value=tagAndValueString.substring(tagAndValueString.indexOf(":")+1);
            }

            //edit the tags so that they match the column names in the central database (just two of them didn't match
            contentValues.put(tag, value);

            //Log.d("internalDB_test", tag + " " + value);
        }

        contentValues.put("image", arr);
        contentValues.put("userID",Utility.CurrentUser.getUserId());

        //Log.d("contentValues",contentValues.toString());
        //Insert the report in SQLite
        db.insert("tempTable", null, contentValues);
        //Log.d("after_insert", getAllRecords().toString());

        return true;
    }

    /**
     * get all data about a single user
     * @param userId THe ID of the user who is inserting the report
     * @return An Arraylist of all the Reports by a user
     */

    public ArrayList<Report> getDataForUser(String userId) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from tempTable where userID='" + userId + "'", null);

        return convertCursorIntoRecordList(res);
    }

    /**
     * Converts the cursor into an arraylist of type Report
     * @param res A currsor holding all the reports by a user
     * @return An Arraylist containing all the reports by a user
     */
    private ArrayList<Report> convertCursorIntoRecordList(Cursor res){

        //THe cursor must be reset to point to the first row
        res.moveToFirst();
        ArrayList<Report> allReportsByAUser = new ArrayList<Report>();

        while(res.isAfterLast() == false){
            //Get each attribute of the report from their corresponding column of database

            String recordID = res.getString(res.getColumnIndex(REPORT_COLUMN_ID));
            String userID = res.getString(res.getColumnIndex(REPORT_COLUMN_USERID));
            String time = res.getString(res.getColumnIndex(REPORT_COLUMN_TIME));
            String latitude = res.getString(res.getColumnIndex(REPORT_COLUMN_LATITUDE));
            String longitude = res.getString(res.getColumnIndex(REPORT_COLUMN_LONGITUDE));
            String informalLocation = res.getString(res.getColumnIndex(REPORT_COLUMN_INFORMALLOCATION));
            String problemDesc = res.getString(res.getColumnIndex(REPORT_COLUMN_PROBDESCR));
            byte[] image = res.getBlob(res.getColumnIndex(REPORT_COLUMN_IMAGE));
            String category = res.getString(res.getColumnIndex(REPORT_COLUMN_CATEGORY));

            Report insertedReport = new Report(userID,category,image,informalLocation,latitude,longitude,problemDesc,recordID,time);

            //Log.d("created report:","" + insertedReport.toString());
            allReportsByAUser.add(insertedReport);
            //array_list.add(res.getString(res.getColumnIndex(REPORT_COLUMN_ID)) +" " +res.getString(res.getColumnIndex(REPORT_COLUMN_USERID)) + " " + res.getString(res.getColumnIndex(REPORT_COLUMN_TIME)) + " " + res.getString(res.getColumnIndex(REPORT_COLUMN_LATITUDE))+" "+res.getString(res.getColumnIndex(REPORT_COLUMN_LONGITUDE)));
            res.moveToNext();
        }
        return allReportsByAUser;

    }

    /**
     *
     * @return The number of rows in database
     */
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

    /**
     * Deletes a particular report item
     * @param id id of the report to be deleted
     * @return The number of rows deleted by the query
     */

    public Integer deleteRecord (String id)
    {
        Log.d("before deleting: ",getAllRecords().toString());
        SQLiteDatabase db = this.getWritableDatabase();
        int numRowsDeleted = db.delete("tempTable",
                "id = ? ",
                new String[]{id});
        Log.d("after deleting: ",getAllRecords().toString());
        return numRowsDeleted;
    }

    /**
     * returns the id of all the reports currently in the database
     * @return An Arraylist of String containing all the ids of reports
     */

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

