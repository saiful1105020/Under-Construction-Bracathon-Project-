package com.underconstruction.underconstruction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.provider.Settings;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends Activity {

    String savedUserName, savedPassword;
    public EditText txtEmail, txtPassword;
    public Button btnLogin;
    public LinearLayout layoutWait;
    public TextView errorText;
    CheckBox chkSave;
    String email, password;
    Context context;
    DBHelper helper;

    @Override
    protected void onResume() {
        Log.d("Resume", "Language set " + Utility.Settings.get_language(getApplicationContext()));

        //Utility.Settings.set_app_language(Utility.Settings.get_language(getApplicationContext()), getApplicationContext());
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //==Language Support (en/bn)=====================
        //Locale locale = new Locale("en");
        Utility.Settings.set_app_language(Utility.Settings.get_language(getApplicationContext()), getApplicationContext());
        //=============================================

        setContentView(R.layout.activity_login);


        txtEmail = (EditText) findViewById(R.id.txtLoginEmail);
        txtPassword = (EditText) findViewById(R.id.txtLoginPassword);
        errorText = (TextView) findViewById(R.id.lblLoginError);
        chkSave = (CheckBox) findViewById(R.id.chkLoginRemember);
        layoutWait = (LinearLayout) findViewById(R.id.layoutLoginWait);
        Button btnRegistration = (Button) findViewById(R.id.btnLoginRegistration);

        restoreInstance();
        requestGPS();

        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent k = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(k);
            }
        });

        TextView lblForgetPassword = (TextView) findViewById(R.id.lblLoginForgot);
        lblForgetPassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Intent k = new Intent(LoginActivity.this, ForgetActivity.class);
                Intent k = new Intent(LoginActivity.this, SettingsActivity.class);
                startActivity(k);
            }
        });

        context = getApplicationContext();
        helper = new DBHelper(context);

        btnLogin = (Button) findViewById(R.id.btnLoginLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = txtEmail.getText().toString();
                password = txtPassword.getText().toString();
                if (incompleteFields())
                    errorText.setText("Please enter email and password.");
                else
                {
                    new UpdateCategoryListTask().execute();

                    if (!chkSave.isChecked())
                    {
                        savedUserName = savedPassword = "";
                    }
                    busy_session(true);
                    LoginTask loginTask = new LoginTask();
                    loginTask.execute();
                }

                //Intent k = new Intent(LoginActivity.this, VerificationActivity.class);
                //startActivity(k);
            }
        });
    }

    private boolean incompleteFields()
    {
        if (email.isEmpty() || password.isEmpty())
            return true;
        return false;
    }
    /**
     * Disable Registration button when the app is working
     * @return
     */
    void busy_session(boolean flag)
    {
        if (flag)
        {
            btnLogin.setEnabled(false);
            layoutWait.setVisibility(View.VISIBLE);
        }
        else
        {
            txtPassword.setText("");
            txtEmail.setText("");
            txtEmail.requestFocus();

            //((EditText) findViewById(R.id.txtRegistrationPassword)).setText("");
            //((EditText) findViewById(R.id.txtRegistrationConfirmPassword)).setText("");
            //((EditText) findViewById(R.id.txtRegistrationUsername)).setText("");

            btnLogin.setEnabled(true);
            layoutWait.setVisibility(View.INVISIBLE);
        }
    }

    class UpdateCategoryListTask extends AsyncTask<String, Void, String> {
        private JSONObject jsonCategoryList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {

            JSONParser jParser = new JSONParser();
            // getting JSON string from URL
            jsonCategoryList = jParser.makeHttpRequest("/getcategorylist", "GET", null);

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute (String file_url){
            if(jsonCategoryList == null) {
                //Utility.CurrentUser.showConnectionError(getApplicationContext());
                Utility.CategoryList categoryList = new Utility.CategoryList();

                categoryList.copyCategoryList(helper.getCategoryList());
                return;
            }

            Log.d("categories received", jsonCategoryList.toString());
            Utility.CategoryList categoryList = new Utility.CategoryList();

            try {
                JSONArray categories = jsonCategoryList.getJSONArray("catList");
                int n = categories.length();
                int curIndex = 1;                       //Skipping first category i.e. Others
                while(curIndex<n) {
                    JSONObject curObj = categories.getJSONObject(curIndex++);

                    String categoryName = curObj.getString("name");
                    int categoryId = curObj.getInt("categoryId");

                    categoryList.add(categoryName, categoryId);
                }

                categoryList.add("Others", -1);

                helper.insertCategory(categoryList);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    class LoginTask extends AsyncTask<String, Void, String> {

        private JSONObject jsonSignUp, jsonLocations;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {

            JSONParser jParser = new JSONParser();
            // Building Parameters
            List<Pair> params = new ArrayList<Pair>();
            params.add(new Pair("email",email));
            params.add(new Pair("password",password));
            // getting JSON string from URL
            jsonSignUp = jParser.makeHttpRequest("/login", "GET", params);

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute (String file_url){
            if(jsonSignUp == null) {
                //Utility.CurrentUser.showConnectionError(getApplicationContext());
                errorText.setText("Please check your internet connection");
                busy_session(false);
                return;
            }
            String userId = new String ("");
            String userName = new String ("");
            String isVerified = new String ("");
            try {
                userId = jsonSignUp.getString("userId");
                userName = jsonSignUp.getString("userName");
                isVerified = jsonSignUp.getString("isVerified");

                //String uid = jsonSignUp.getString("userId");
                //Utility.CurrentUser.setUserId(uid);
            }catch(JSONException e){
                e.printStackTrace();
            }

            if(userId.equals("0")){
                errorText.setText("Incorrect password, please try again.");
                busy_session(false);
                return;
            }

            /**
             * Merging without verification activity for now
             *
             */

//            else if (isVerified.equals("0"))
//            {
//                Utility.CurrentUser.setUserId(userId);
//                Intent k = new Intent(LoginActivity.this, VerificationActivity.class);
//                startActivity(k);
//                finish();
//
//            }
            else {
                errorText.setText("Success! Hello " + userName + " :" + userId);
                Utility.CurrentUser.setUserId(userId);
                Log.d("Logging in", "My ID: " + Utility.CurrentUser.getUserId());
                Utility.CurrentUser.setUsername(userName);
                if (chkSave.isChecked())
                {
                    savedUserName = email;
                    savedPassword = password;
                }
                saveInstance();
                finish();
                //String[] values = new String[]{"Broken Road", "Manhole", "Risky Intersection", "Crime prone area", "Others"};
                Utility.CategoryList.add("~Broken Road", 1);
                Utility.CategoryList.add("~Manhole", 4);
                Utility.CategoryList.add("~Risky Intersection", 7);
                Utility.CategoryList.add("~Crime prone area", 9);
                //Utility.CategoryList.add("Others", -1); No Need, auto added

                /**
                 * Bypassing without linking login user id to home page received // or maybe i don't need to :/
                 *
                 */
                Intent intent=new Intent(LoginActivity.this, TabbedHome.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putString("Email", savedUserName);
        savedInstanceState.putString("Password", savedPassword);
        // etc.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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


    void requestGPS()
    {
        if (!isLocationEnabled(this))
        {
            Toast.makeText(this, "Please enable GPS Service" , Toast.LENGTH_LONG).show();
            Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsOptionsIntent);
        }



    }
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }
    void saveInstance()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("LoginPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("Save", chkSave.isChecked());

        if (chkSave.isChecked())
        {
            editor.putString("Email", savedUserName);
            editor.putString("Password", savedPassword);
        }
        editor.commit();
    }
    void restoreInstance()
    {
        chkSave.setChecked(false);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("LoginPref", 0); // 0 - for private mode
        //SharedPreferences.Editor editor = pref.edit();
        if (pref.getBoolean("Save", false))
        {
            chkSave.setChecked(true);
            txtEmail.setText(pref.getString("Email", null));
            txtPassword.setText(pref.getString("Password", null));
        }
    }

}

