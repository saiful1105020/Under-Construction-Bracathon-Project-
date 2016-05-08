package com.underconstruction.underconstruction;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class PostSuggestion extends AppCompatActivity {

    Button btnUpload, btnCancel;
    ListView lvwPostSugg;
    ArrayList<PostSuggestionItem> suggestionItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_suggestion);

        btnUpload = (Button) findViewById(R.id.btnUploadSuggestionAnyway);
        btnCancel = (Button) findViewById(R.id.btnSkipUpload);
        lvwPostSugg = (ListView) findViewById(R.id.lvwPostSuggestion);

        //populate Arraylist 'suggestionItems' Here, get from intent maybe?

        PostSuggestionAdapter ps_adapter = new PostSuggestionAdapter();
        lvwPostSugg.setAdapter(ps_adapter);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Don't forget to delete from SQLite db
                //write code for upload here
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Don't forget to delete from SQLite db
                finish();

            }
        });

        //PostSuggestionTask ps = new PostSuggestionTask();
        //ps.execute();
    }

    class PostSuggestionTask extends AsyncTask<String, Void, String> {

        private JSONObject jsonPostSuggestion, jsonLocations;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {

            JSONParser jParser = new JSONParser();
            // Building Parameters
            List<Pair> params = new ArrayList<Pair>();
            params.add(new Pair("lat","23.7852"));
            params.add(new Pair("lon","90.4131"));
            params.add(new Pair("time", "2015-12-06 17:21:43"));
            params.add(new Pair("cat", "0"));

            // getting JSON string from URL
            Log.d("PostSuggest", params.toString());
            jsonPostSuggestion = jParser.makeHttpRequest("/getSuggestions", "GET", params);
            Log.d("PostSuggest", jsonPostSuggestion.toString());

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute (String file_url){
            if(jsonPostSuggestion == null) {
                //Utility.CurrentUser.showConnectionError(getApplicationContext());
                //txtRes.setText("Please check your internet connection");
                return;
            }
            String s = new String("");
            try {
                JSONArray postsJSONArray = jsonPostSuggestion.getJSONArray("posts");
                //postArrayList.clear();

                int curIndex=0, N=postsJSONArray.length();

                while(curIndex<N) {
                    JSONObject curObj = postsJSONArray.getJSONObject(curIndex++);
                    Log.d("jsonReturned", curObj.toString());

                    String uname =curObj.getString("userName");
                    s = s + uname + "\n";

                    //Log.d("posts near you", uname);
                }
                //txtRes.setText(s);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_suggestion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class PostSuggestionAdapter extends ArrayAdapter<PostSuggestionItem>
    {

        public PostSuggestionAdapter() {
            super(getApplicationContext(), R.layout.activity_post_suggestion);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null)
                v = getLayoutInflater().inflate(R.layout.activity_post_suggestion, parent, false);

            ((TextView) v.findViewById(R.id.lblSuggestionRating)).setText(suggestionItems.get(position).rating);
            ((TextView) v.findViewById(R.id.lblSuggestionDate)).setText(suggestionItems.get(position).date);
            ((TextView) v.findViewById(R.id.lblSuggestionInformalLocation)).setText(suggestionItems.get(position).informalLocation);
            ((TextView) v.findViewById(R.id.lblSuggestionInformalProblemDesc)).setText(suggestionItems.get(position).informalProblemDescription);
            ((TextView) v.findViewById(R.id.lblSuggestionUser)).setText(suggestionItems.get(position).username);
            ((ImageView) v.findViewById(R.id.imgSuggestion)).setImageBitmap(BitmapFactory.decodeByteArray(suggestionItems.get(position).img, 0, suggestionItems.get(position).img.length));

            return v;
        }
    }
}
