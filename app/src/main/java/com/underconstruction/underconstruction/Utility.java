package com.underconstruction.underconstruction;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Shabab on 12/5/2015.
 *
 * Utility class holds all the information that will be needed throughout the entire app.
 */

public class Utility {


    /**
     * This class will be used to configure settings
     */
    static class Settings
    {
        /**
         * returns the currently active language
         * @param context
         * @return The currently active language
         */
        public static String get_language(Context context)
        {
            String lang;
            SharedPreferences pref = context.getSharedPreferences("LangPref", 0); // 0 - for private mode
            lang = pref.getString("Language", "en");
            return lang;
        }

        /**
         * Sets a new langauge as the app language
         * @param context
         * @param lang the language to set.
         */
        public static void set_language(Context context, String lang)
        {
            SharedPreferences pref = context.getSharedPreferences("LangPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("Language", lang);

            editor.commit();
        }

        public static void set_app_language(String lang, Context context)
        {
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            context.getResources().updateConfiguration(config, null);
        }

    }


    /**
     * Implements dynamic category feature. All the problem categories will be brought back from the main database after
     * login.
     */
    public static class CategoryList
    {
        //Given a category name, returns its ID
        public static HashMap<String, Integer> categoryMap = new HashMap<String, Integer> ();
        //Given an ID, returns the category name
        public static HashMap<Integer, String> IdMap = new HashMap<Integer, String> ();

        /**
         * Sets default option in hashmaps
         */
        public CategoryList() {
            //Add the Others option automatically
            categoryMap.put("Others", -1);
            IdMap.put(-1, "Others");
        }



        /**
         * Adds a new category name and id. It is used when category list is received from server
         * @param name
         * @param id
         */
        public static void add(String name, int id)
        {
            categoryMap.put(name, id);
            IdMap.put(id, name);
        }



        /**
         *  Used when populating category list dynamically
         * @return an arraylist of the names of category
         */
        public static ArrayList<String> getArrayList()
        {
            ArrayList<String> temp = new ArrayList<String>();
            Object [] t = categoryMap.keySet().toArray();
            for (int i = 0 ; i< t.length; i++)
                if (!(((String)t[i]).equals("Others")))
                    temp.add((String)t[i]);
            temp.add("Others"); //display Others as the last element in Category list
            return temp;
        }



        /**
         * given a category id, returns its name
         *  //f(id) = CategoryName
         * @param id the id of the category
         * @return the name of the category
         */
        public static String get(int id)
        {
            return IdMap.get(id);
        }



        /**
         *  given a category name, returns its id
         * f(CategoryName) = id
         * @param cat the name of the category
         * @return the id of the category
         */
        public static int get(String cat)
        {
            return categoryMap.get(cat);
        }
    }

    /**
     * Stores the info of the currently active user
     */

    public static class CurrentUser{

        //The id of the user in string
        private static String userId = "-1";
        //the id of the user in integer
        private static int id=Integer.valueOf(userId);
        //the name of the user
        static String username="Onix";
        //used to check if the provided ip address is ok.Used in debugging
        static boolean ipOK = false;




        public static String getUsername() {
            return username;
        }

        public static void setUsername(String username) {
            CurrentUser.username = username;
        }

        public static String getUserId() {
            return userId;
        }

        public static void setUserId(String userId) {
            CurrentUser.userId = userId;
            CurrentUser.id = Integer.valueOf(userId);
        }


        public static int getId(){return id;}
        public static String getName(){return username;}


        /**
         * Parses the post time in user readable format
         * @param dbString the time in default format
         * @return time in user readable format
         */
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
                    try {
                        date = " " + monthString.substring(0,3) + " " + date.substring(8,10);
                    }
                    catch (Exception e)
                    {
                        Log.d("Time", "Invalid Substring");
                        e.printStackTrace();
                    }
                }
            }

            String timeOfUpdate = hr + ":" + min + timeOfDay + date;
            return timeOfUpdate;
        }

    }

    public static class HazardTags{
        private static String[] hazardTags={"Occupied Footpath","Open Dustbin","Open Manhole","Cluttered Electric Wires","Water Logging","Risky Intersection","No Street Light","Crime Prone Area","Damaged Road","Wro+" +
                "ngway Traffic"};

        public static String[] getHazardTags(){
            return hazardTags;
        }
    }

    /**
     *
     * @param context
     * @return true if the device is connected to the internet, false otherwise
     */
    public static boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in air plan mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }


    /**
     * Used to ease the task of uploading/ not uploading a report based on user input
     */
    public interface UploadDecision{
        int UPLOAD_REPORT = 3;
        int DONT_UPLOAD_REPORT = 4;

    }


}
