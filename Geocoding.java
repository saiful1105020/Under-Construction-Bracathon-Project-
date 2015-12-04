package test;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class Geocoding
{
    static String makeJSONString(String param)
    {
        String jsonString = readJSONString(param);
        return jsonString;
    }
    
    static void getData(String jsonString)
    {
        if (jsonString == null)
        {
                System.out.println("Error loading data");
        }
        else
        {
            try 
            {
                JSONObject json = new JSONObject(jsonString);
                final JSONArray geodata = json.getJSONArray("results");
                final JSONObject person = geodata.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                System.out.println("Latitude: "+person.getDouble("lat"));
                System.out.println("Longitude: "+person.getDouble("lng"));
            }
            catch (JSONException exception)
            {
                System.out.println("Error loading data");
                exception.printStackTrace();
            }
        }
    }
    
    private static String readJSONString(String address)
    {
        try {
                String urlString = "https://maps.googleapis.com/maps/api/geocode/json?address=" + 
                                URLEncoder.encode(address, "utf-8") +
                                "&key=AIzaSyBDN9PhMVRDH1gPYNi3d3fEXZZcGmOnsMA";
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();

                // The IOUtils.toString method fetches all data from an input stream and converts it into a string.
                String response = IOUtils.toString(in);
                return response;
        } catch (Exception e) {
                e.printStackTrace();
                return null;
        }
    }
    
    public static void main(String[] args)
    {
        String junction="BRAC University";
       
        String to_API=makeJSONString(junction+",Dhaka,Bangladesh");
        getData(to_API);
    }
}
