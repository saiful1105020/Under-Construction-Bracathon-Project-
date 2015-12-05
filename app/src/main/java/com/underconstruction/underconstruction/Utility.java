package com.underconstruction.underconstruction;

import android.content.Context;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Shabab on 12/5/2015.
 */

public class Utility {

    public static class CurrentUser{

        private static int id=1, displayPage = 0;
        private static String apiKey,username="Onix";
        private static boolean valid=false;

        public static void setUser(int i,String uName,String apikey){
            id=i;
            username=uName;
            apiKey=apikey;
            valid=true;

        }

        public static void invalidate(){
            valid=false;
        }

        public static  boolean isTheUserValid(){
            return valid;
        }

        public static int getId(){return id;}
        public static String getName(){return username;}

        public static String getApiKey() {
            return apiKey;
        }

        public static String makeString(){
            return ""+id+" "+username+" "+apiKey;
        }

        public static int getDisplayPage() {
            return displayPage;
        }

        public static void setDisplayPage(int i) {
            displayPage = i;
        }

        public static void showConnectionError(Context context) {
            Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_LONG).show();
        }

        public static String parsePostTime (String dbString) {
            int hr = Integer.parseInt("" + dbString.charAt(11) + dbString.charAt(12));
            String min = "" +  dbString.charAt(14) + dbString.charAt(15);
            String timeOfDay;
            if(hr>12) {
                hr = hr%12;
                timeOfDay = "pm";
            }
            else if(hr == 12) timeOfDay = "pm";
            else timeOfDay = "am";

            if(hr == 0) hr = 12;

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            String today = dateFormat.format(cal.getTime());
            String date = dbString.substring(0, 10);

            if (today.equals(date)) {
                date = " Today";
            }
            else {
                cal.add(Calendar.DAY_OF_MONTH, -1);
                String yesterday = dateFormat.format((cal.getTime()));
//                Log.d("yesterday", yesterday);
                if (yesterday.equals(date)) {
                    date = " Yesterday";
                } else {
                    String monthString = new DateFormatSymbols().getMonths()[Integer.parseInt(date.substring(5,7))-1];
//                    Log.d("monthString", monthString);
                    date = " " + monthString.substring(0,3) + " " + date.substring(8,10);
                }
            }

            String timeOfUpdate = hr + ":" + min + timeOfDay + date;
            return timeOfUpdate;
        }

    }

    public static class HazardTags{
        private static String[] hazardTags={"Occupied Footpath","Open Dustbin","Open Manhole","Cluttered Electric Wires","Water Logging","Risky Intersection","No Street Light","Crime Prone Area","Damaged Road","Wrongway Traffic"};

        public static String[] getHazardTags(){
            return hazardTags;
        }
    }

}
