package com.underconstruction.underconstruction;

import android.app.IntentService;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by wasif on 12/4/15.
 */
public  class FetchAddressIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    //protected ResultReceiver mReceiver;
    protected ResultReceiver mReceiver;
    private static final String TAG = "FetchAddressIS";

    public FetchAddressIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }
   /*
    public void onCreate() {
        super.onCreate();
        Log.d("Server", ">>>onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, startId, startId);
        Log.i("LocalService", "Received start id " + startId + ": " + intent);

        return START_STICKY;
    }
    */





    @Override
    protected void onHandleIntent(Intent intent) {
        //Toast.makeText(this, "Inside on handle intent", Toast.LENGTH_LONG).show();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        String errorMessage = "";
        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(
                Constants.LOCATION_DATA_EXTRA);
        Log.d("inside check", location.getLatitude() + " " + location.getLongitude());


        List<String> addresses = null;

        try {
            Log.d("inside service", "check");
            if (isInternetAvailable()) {
                Log.d("internet", "found");
            } else {
                Log.d("internet", "not found");
            }
            /*
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address.
                    1);
                   */

            addresses = getFromLocation(location.getLatitude(), location.getLongitude(),1);
            //addresses = getFromLocation(24.7904106, 91.4171203, 1);
            //Log.d("returned value", addresses.toString());
            String finalResult="";
            if(addresses!=null){
                if(addresses.size()>0){
                    finalResult+=addresses.get(0);
                }

                for(int i=1;i<addresses.size();i++){
                    finalResult=finalResult+"~"+addresses.get(i);
                }
            }
            Log.d("final result", finalResult.toString());

            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                    finalResult);

            //List<Address> addresses = null;
            //Geocoder geocoder = new Geocoder(this);
            //addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            //if (addresses == null || addresses.isEmpty())
            // addresses = MyGeoCoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);


       /*}catch (IOException ioException) {
            // Catch network or other I/O problems.
            //errorMessage = getString(R.string.service_not_available);
            Log.e("inside", "service not avail", ioException);*/
            /*
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
                /*
                errorMessage = getString(R.string.invalid_lat_long_used);

                Log.e("inside", "invalid" + ". " +
                        "Latitude = " + location.getLatitude() +
                        ", Longitude = " +
                        location.getLongitude(), illegalArgumentException);

        }catch (Exception e){
            Log.e("inside","exception found");
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                //errorMessage = getString(R.string.no_address_found);
                errorMessage="Address not Found";
                Log.e("addreport", errorMessage);
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        } else {
           // Log.d("print result",addresses.toString());
            Address address = addresses.get(0);


            ArrayList<String> addressFragments = new ArrayList<String>();
            //for(int i=0;i<addresses.size();i++)
               // Log.d("print result", addresses.get(i).getAddressLine(0).toString());
            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                Log.d("print ",address.getAddressLine(i).toString());
                //Log.d("This is a string"," ");
                addressFragments.add(address.getAddressLine(i));
            }
            //Log.i(TAG, getString(R.string.address_found));
            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"),
                            addressFragments));
        }
        */
        }catch (Exception e){

        }
    }




    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        Log.d("final send", message);
        mReceiver.send(resultCode, bundle);
    }

/*
    public static JSONObject getLocationInfo(String address) {
        StringBuilder stringBuilder = new StringBuilder();
        try {

            address = address.replaceAll(" ","%20");

            HttpPost httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false");
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            stringBuilder = new StringBuilder();


            response = client.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }
    private static List<Address> getAddrByWeb(JSONObject jsonObject){
        List<Address> res = new ArrayList<Address>();
        try
        {
            JSONArray array = (JSONArray) jsonObject.get("results");
            for (int i = 0; i < array.length(); i++)
            {
                Double lon = new Double(0);
                Double lat = new Double(0);
                String name = "";
                try
                {
                    lon = array.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                    lat = array.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    name = array.getJSONObject(i).getString("formatted_address");
                    Address addr = new Address(Locale.getDefault());
                    addr.setLatitude(lat);
                    addr.setLongitude(lon);
                    addr.setAddressLine(0, name != null ? name : "");
                    res.add(addr);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();

                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();

        }

        return res;
    }
    */

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name

            if (ipAddr.equals("")) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

    }

    public static List<String> getFromLocation(double lat, double lng, int maxResult){

        String address = String.format(Locale.ENGLISH,"http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language="+Locale.getDefault().getCountry(), lat, lng);
        HttpGet httpGet = new HttpGet(address);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        List<String> retList = null;

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject = new JSONObject(stringBuilder.toString());
            final JSONArray geodata=jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
            //Log.d("geodata:",geodata.toString());
            retList = new ArrayList<String>();
            for(int i=0;i<geodata.length();i++){
                String tempType=geodata.getJSONObject(i).getJSONArray("types").getString(0);
                //Log.d("tempType:",tempType);
                //Log.d("see_res",geodata.getJSONObject(i).getJSONArray("types").getString(0)+": "+geodata.getJSONObject(i).getString("long_name"));
                if(tempType.equals("street_number")||tempType.equals("route")||tempType.equals("neighborhood")||tempType.equals("sublocality_level_1")||tempType.equals("locality"))
                    retList.add(geodata.getJSONObject(i).getJSONArray("types").getString(0)+":"+geodata.getJSONObject(i).getString("long_name"));
            }
            Log.d("fin_chk",retList.toString());
            return retList;
            /*retList = new ArrayList<String>();

            JSONArray results = jsonObject.getJSONArray("results");
            JSONArray addressComponentArray=results.getJSONArray(0);
            for(int i=0;i<addressComponentArray.length();i++){

            }

            if("OK".equalsIgnoreCase(jsonObject.getString("status"))){
                JSONArray results = jsonObject.getJSONArray("results");
                for (int i=0;i<results.length();i++ ) {
                    JSONObject result = results.getJSONObject(i);
                    Log.d("JSON result:",result.toString());
                    //String indiStr = result.getString("formatted_address");
                    //Log.d("JSON result:",indiStr);
                    JSONArray selectTypeArray=result.getJSONArray("types");
                    if(selectTypeArray.getString(0).equals("neighborhood")||selectTypeArray.getString(0).equals("locality")||selectTypeArray.getString(0).equals("route")){
                        String type=selectTypeArray.getString(0);
                        String wholeAddress=result.getString("address_components");
                        //Log.d("whole",wholeAddress);
                       // String parsedString=wholeAddress.substring(0, wholeAddress.indexOf(","));
                        //String finalString=type+":"+parsedString;
                        //Log.d("parsed:",finalString);
                        //retList.add(finalString);
                        //Log.d("selected res",result.getString("formatted_address"));
                    }

                    //Address addr = new Address(Locale.getDefault());
                    //addr.setAddressLine(i, indiStr);
                    //retList.add(addr);
                }
            }*/


        } catch (ClientProtocolException e) {
            //Log.e(MyGeocoder.class.getName(), "Error calling Google geocode webservice.", e);
        } catch (IOException e) {
            //Log.e(MyGeocoder.class.getName(), "Error calling Google geocode webservice.", e);
        } catch (JSONException e) {
            //Log.e(MyGeocoder.class.getName(), "Error parsing Google geocode webservice response.", e);
        }

        return retList;
    }


}
