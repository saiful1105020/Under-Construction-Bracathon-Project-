package com.underconstruction.underconstruction;

import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by Shabab on 12/4/2015.
 */

public class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String jsonString = "";

    // constructor
    public JSONParser() {

    }

    // function get json from url
    // by making HTTP POST or GET mehtod
    public JSONObject  makeHttpRequest(String urlParameter, String method,
                                       List<Pair> params) {

//        final String BASE_URL = "http://" + Utility.CurrentUser.getIp() +
//                "/uc_brac_git/uc_server/index.php/home";

//        final String BASE_URL = "http://" + "172.20.62.23" +
//                "/uc_brac_git/uc_server/index.php/home";

        final String BASE_URL = "http://" + "172.20.62.33" +                //Alternate Database
                "/hackThon/UC_Server/index.php/home";

        Log.d("base url", BASE_URL);
        URL url;
//        List<Pair> paramaters = new ArrayList<Pair>();
        // Making HTTP request
        try{

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
//            System.setProperty("http.keepAlive", "false");

            if (method == "POST") {
                url = new URL(BASE_URL + urlParameter);
                Log.d("Test: ", "1");
                urlConnection = (HttpURLConnection) url.openConnection();
                Log.d("Test: ", "2");
                urlConnection.setRequestMethod(method);
                Log.d("Test: ", "3");
                urlConnection.setDoOutput(true);
//                urlConnection.setInstanceFollowRedirects(false);
                Log.d("Test: ", "4");

                if(params != null) {
                    OutputStream os = urlConnection.getOutputStream();
                    Log.d("Test: ", "5");
                    OutputStreamWriter writer = new OutputStreamWriter(os);
                    Log.d("Test: ", "6");
                    writer.write(getQuery(params));
                    Log.d("output params: ",getQuery(params));
                    writer.close();
                }
            }
            else if(method == "GET") {
                url = new URL(BASE_URL + urlParameter + "?" + getQuery(params));

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(method);
                urlConnection.setDoInput(true);
                urlConnection.connect();
//                urlConnection.setInstanceFollowRedirects(false);
            }

            is = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (is == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }
            // Log.d("the buffer string: ", buffer.length()+"");
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            jsonString = buffer.toString();
            //Log.d("Input Stream: ", jsonString);
            urlConnection.disconnect();

        } catch (IOException e) {
            Log.d("JSONParser", "IOException");
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("JSONParser", "Unknown Exception");
        }

        // try parse the string to a JSON object
        try {
            //System.out.println(jsonString);
            // Log.d("jsonString: ", jsonString);
            jObj = new JSONObject(jsonString);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
            Log.d("JSON Parser inputstream", jsonString);
//            try {
//                FileOutputStream fos = new FileOutputStream(new File("a.txt"));
//                //PrintWriter out = new PrintWriter("filename.txt");
//                //out.println(jsonString);
//                //out.close();
//                try {
//                    fos.write(jsonString.getBytes());
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//            } catch (FileNotFoundException e1) {
//                e1.printStackTrace();
//            }

//            BufferedWriter writer = null;
//            try
//            {
//                writer = new BufferedWriter( new FileWriter( "a.txt"));
//                writer.write( jsonString);
//
//            }
//            catch ( IOException g)
//            {
//                g.printStackTrace();
//            }
//            finally
//            {
//                try
//                {
//                    if ( writer != null)
//                        writer.close();
//                }
//                catch ( IOException f) {
//                    f.printStackTrace();
//                }
//            }
        }

        // return JSON String

        if(jObj != null) {
            Utility.CurrentUser.ipOK = true;
        }

        return jObj;

    }
    private String getQuery(List<Pair> params) throws Exception
    {
        if (params == null)
            return "";

        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Pair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.first.toString(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.second.toString(), "UTF-8"));
        }

        return result.toString();
    }
}
