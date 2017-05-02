package com.underconstruction.underconstruction;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VerificationActivity extends Activity {

    public Button btnVerify;
    public String VerificationCode;
    public TextView errorText;

    public EditText txtVerify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        btnVerify = (Button) findViewById(R.id.btnVerificationVerify);
        //lblTest = (TextView) findViewById(R.id.lblVerificationError);

        errorText = (TextView) findViewById(R.id.lblVerificationError);
        txtVerify = (EditText) findViewById(R.id.txtVerificationCode);

        //lblTest.setText(Utility.CurrentUser.getUserId());

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerificationCode = txtVerify.getText().toString();
                VerifyTask verifyTask = new VerifyTask();
                verifyTask.execute();
            }
        });
    }

    class VerifyTask extends AsyncTask<String, Void, String> {

        private JSONObject jsonSignUp, jsonLocations;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {

            JSONParser jParser = new JSONParser();
            // Building Parameters
            List<Pair> params = new ArrayList<Pair>();
            params.add(new Pair("userId",Utility.CurrentUser.getUserId()));
            params.add(new Pair("verCode",VerificationCode));
            // getting JSON string from URL
            jsonSignUp = jParser.makeHttpRequest("/verify_registration", "GET", params);

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute (String file_url){
            if(jsonSignUp == null) {
                //Utility.CurrentUser.showConnectionError(getApplicationContext());
                errorText.setText("Please check your internet connection");
                return;
            }
            String verificationStatus = new String ("");
            try {
                verificationStatus  = jsonSignUp.getString("status");
                //String uid = jsonSignUp.getString("userId");
                //Utility.CurrentUser.setUserId(uid);
            }catch(JSONException e){
                e.printStackTrace();
            }

            if(verificationStatus.equals("0")){
                errorText.setText("Incorrect code, please try again.");
                //busy_session(false);
                return;
            }
            else{
                finish();
                Intent intent=new Intent(VerificationActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_verification, menu);
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
}
