package test;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class ReverseGeocoding
{
    static String makeJSONString(double lat,double lon)
    {
        String param=String.valueOf(lat)+","+String.valueOf(lon);
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
                final JSONArray geodata = json.getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
                
                for(int i=0;i<geodata.length();i++)
                {
                    System.out.print(geodata.getJSONObject(i).getJSONArray("types").getString(0)+": ");
                    System.out.println(geodata.getJSONObject(i).getString("long_name"));
                }
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
                String urlString = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + 
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
        double lat=23.7801679;
        double lon=90.4071914;
       
        String to_API=makeJSONString(lat,lon);
        getData(to_API);
    }
}
