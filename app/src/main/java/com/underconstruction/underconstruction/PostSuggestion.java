package com.underconstruction.underconstruction;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PostSuggestion extends AppCompatActivity implements  Utility.UploadDecision{

    Button btnUpload, btnCancel;
    ListView lvwPostSugg;
    ArrayList<PostSuggestionItem> suggestionItems = new ArrayList<PostSuggestionItem>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_suggestion);

        Toast.makeText(getApplicationContext(), "Possible Duplicates", Toast.LENGTH_LONG).show();

        btnUpload = (Button) findViewById(R.id.btnUploadSuggestionAnyway);
        btnCancel = (Button) findViewById(R.id.btnSkipUpload);
        lvwPostSugg = (ListView) findViewById(R.id.lvwPostSuggestion);

        //populate Arraylist 'suggestionItems' Here, get from intent maybe?

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Don't forget to delete from SQLite db
                //write code for upload here
                Log.d("Inside PostSuggestion", "upload clicked");
                Intent returnIntent = getIntent();
                returnIntent.putExtra("uploadDecision", UPLOAD_REPORT);
                if (getParent() == null) {
                    setResult(AppCompatActivity.RESULT_OK, returnIntent);
                }
                else
                    getParent().setResult(AppCompatActivity.RESULT_OK, returnIntent);
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Don't forget to delete from SQLite db
                Log.d("Inside PostSuggestion","dont upload clicked");
                Intent returnIntent = getIntent();
                returnIntent.putExtra("uploadDecision", DONT_UPLOAD_REPORT);
                setResult(RESULT_OK, returnIntent);
                finish();

            }
        });

        JSONObject jsonPostSuggestions;
        int N=0;

        try {
            jsonPostSuggestions = new JSONObject(getIntent().getStringExtra("jsonPostSuggestions"));
            Log.d("PostSuggestion.java", jsonPostSuggestions.toString());

            JSONArray postsJSONArray = jsonPostSuggestions.getJSONArray("posts");
            N=postsJSONArray.length();

            int curIndex=0;
            suggestionItems.clear();

            while(curIndex<N) {
                JSONObject curObj = postsJSONArray.getJSONObject(curIndex++);

                PostSuggestionItem postSuggestionItem = PostSuggestionItem.createPost(curObj);
                suggestionItems.add(postSuggestionItem);
                Log.d("PostSuggestionArrayList", suggestionItems.toString());
                //ps_adapter.notifyDataSetChanged();
//                    String uname =curObj.getString("userName");
//                    s = s + uname + "\n";

                //Log.d("posts near you", uname);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        PostSuggestionAdapter ps_adapter = new PostSuggestionAdapter();
        lvwPostSugg.setAdapter(ps_adapter);

        //PostSuggestionTask ps = new PostSuggestionTask();
        //ps.execute();
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
            super(getApplicationContext(), R.layout.activity_post_suggestion, suggestionItems);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null)
                v = getLayoutInflater().inflate(R.layout.suggestion_row, parent, false);

            Log.d("PostSuggestionAdapter", "inside getView");

            int voteCount = Integer.valueOf(suggestionItems.get(position).voteCount);
            if(voteCount>0)
                ((TextView) v.findViewById(R.id.lblSuggestionVoteCount)).setText("+" + voteCount);
            else if(voteCount==0)
                ((TextView) v.findViewById(R.id.lblSuggestionVoteCount)).setText(voteCount + "");
            else
                ((TextView) v.findViewById(R.id.lblSuggestionVoteCount)).setText(voteCount + "");

            ((TextView) v.findViewById(R.id.lblSuggestionDate)).setText(Utility.CurrentUser.parsePostTime(suggestionItems.get(position).date));
            ((TextView) v.findViewById(R.id.lblSuggestionInformalLocation)).setText(suggestionItems.get(position).informalLocation);
            ((TextView) v.findViewById(R.id.lblSuggestionInformalProblemDesc)).setText(suggestionItems.get(position).informalProblemDescription);
            ((TextView) v.findViewById(R.id.lblSuggestionUser)).setText(suggestionItems.get(position).username);
            ((ImageView) v.findViewById(R.id.imgSuggestion)).setImageBitmap(BitmapFactory.decodeByteArray(suggestionItems.get(position).img, 0, suggestionItems.get(position).img.length));

            return v;
        }

    }
}
