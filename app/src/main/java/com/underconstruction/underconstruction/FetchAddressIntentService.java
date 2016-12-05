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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by wasif on 12/4/15.
 */

/**
 * Fetches location attribues like street no, locality etc given latitude and longitude
 */
public  class FetchAddressIntentService extends IntentService {




    //The receiver of the addresses fetched by the service.
    protected ResultReceiver mReceiver;

    private static final String TAG = "FetchAddressIS";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public FetchAddressIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }




    @Override
    protected void onHandleIntent(Intent intent) {


        Geocoder geocoder = new Geocoder(this, Locale.getDefault());


        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(
                Constants.LOCATION_DATA_EXTRA);
        Log.d("inside check", location.getLatitude() + " " + location.getLongitude());


        List<String> addresses = null;

        try {
            //gets all the attribues of a address as a List of String
            addresses = getFromLocation(location.getLatitude(), location.getLongitude(),1);

            //The List will be formatted into this string
            String finalResult="";

            //adds all the location attribues by using ~ as a delimeter
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

        }catch (Exception e){

        }
    }


    /**
     * Sends the result String to the activity calling the intent
     * @param resultCode Whether the service was successfull
     * @param message The message to pass to the calling intent
     */
    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        Log.d("final send", message);
        mReceiver.send(resultCode, bundle);
    }


    /**
     *
     * @param lat THe latitude of the desired location
     * @param lng THe longitude of the desired location
     * @param maxResult THe mas number of result to return
     * @return A list of string containg all the parts of the address
     */

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

            retList = new ArrayList<String>();
            for(int i=0;i<geodata.length();i++){
                String tempType=geodata.getJSONObject(i).getJSONArray("types").getString(0);

                //Extracting only the necessary fields of the address we are using

                if(tempType.equals("street_number")||tempType.equals("route")||tempType.equals("neighborhood")||tempType.equals("sublocality_level_1")||tempType.equals("locality"))
                    retList.add(geodata.getJSONObject(i).getJSONArray("types").getString(0)+":"+geodata.getJSONObject(i).getString("long_name"));
            }
            //Log.d("fin_chk",retList.toString());
            return retList;


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
