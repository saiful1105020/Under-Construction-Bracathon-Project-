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
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends Activity {

    //necessary Variables to store user input
    public String email, username, password, errorMessage, repeatPassword;
    //variable to show error to user
    public TextView errorText;
    //user presses this button to initiate registration
    public Button btnRegistration;

    //variable for enhancing user experience
    public LinearLayout layoutWait;

    //This will match whether the user is giving  a well formed email
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    //flag will be set to indicate any error in registration process
    boolean signUpError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        layoutWait = (LinearLayout) findViewById(R.id.layoutRegistrationWait);
        btnRegistration = (Button) findViewById(R.id.btnRegistrationSignup);
        signUpError=false;
        errorText = (TextView) findViewById(R.id.lblRegistrationError);

        //User wants to register
        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //so collect all the inputs
                email = ((EditText) findViewById(R.id.txtRegistrationEmail)).getText().toString();
                password = ((EditText) findViewById(R.id.txtRegistrationPassword)).getText().toString();
                username = ((EditText) findViewById(R.id.txtRegistrationUsername)).getText().toString();
                repeatPassword= ((EditText) findViewById(R.id.txtRegistrationConfirmPassword)).getText().toString();


                //handling all teh corner cases. See all the methods
                if(showSignUpError()){
                    errorText.setText("One or more required fields are missing!\n");
                }else if(!validate(email)) {
                    errorText.setText("Please enter a valid email address.\n");
                }
                else if(passwordMissMatch()){
                    errorText.setText("Password Mismatch!\n");
                }
                else
                {
                    //Everything is fine.

                    errorText.setText("");
                    //a new session has been initiated
                    busy_session(true);

                    //start talking to database
                    SignUpTask signUpTask = new SignUpTask();
                    signUpTask.execute();

                }

            }
        });
    }


    /**
     * Sends server request to complete registration
     */
    class SignUpTask extends AsyncTask<String, Void, String> {

        private JSONObject jsonSignUp;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {

            JSONParser jParser = new JSONParser();

            // Building Parameters
            List<Pair> params = new ArrayList<Pair>();
            params.add(new Pair("userName",username));
            params.add(new Pair("email",email));
            params.add(new Pair("password",password));

            // getting JSON  from URL
            jsonSignUp = jParser.makeHttpRequest("/register", "GET", params);

            return null;
        }


        protected void onPostExecute (String file_url){

            if(jsonSignUp == null) {
                //Utility.CurrentUser.showConnectionError(getApplicationContext());
                errorText.setText("Please check your internet connection");
                btnRegistration.setEnabled(true);
                layoutWait.setVisibility(View.INVISIBLE);
                return;
            }

            //Extract all the necessary fields
            String loginStatus = new String ("");
            try {
                loginStatus = jsonSignUp.getString("status");
                String uid = jsonSignUp.getString("userId");
                Utility.CurrentUser.setUserId(uid);
            }catch(JSONException e){
                e.printStackTrace();
            }

            //check if we have got the correct response
            if(loginStatus.equals("0")){
                errorText.setText("Sorry, this email already exists.");
                busy_session(false);
                return;
            }
            else{

                //yes we have got the correct response
                finish();

                /**
                 * Merging without verification activity for now
                 * still need to link current login user id with the home page received both from Login and Registration activities
                 *
                 */

//                Intent intent=new Intent(RegistrationActivity.this,VerificationActivity.class);
                //Intent intent=new Intent(RegistrationActivity.this, TabbedHome.class);


                //so prompt the user for logging in
                Intent intent=new Intent(RegistrationActivity.this, LoginActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("password", password);
                startActivity(intent);
            }
        }
    }

    /**
     * Disable Registration button when the app is working
     * @return
     */
    void busy_session(boolean flag)
    {
        if (flag)
        {
            btnRegistration.setEnabled(false);
            layoutWait.setVisibility(View.VISIBLE);
        }
        else
        {
            ((EditText) findViewById(R.id.txtRegistrationEmail)).requestFocus();
            ((EditText) findViewById(R.id.txtRegistrationEmail)).selectAll();
            //((EditText) findViewById(R.id.txtRegistrationPassword)).setText("");
            //((EditText) findViewById(R.id.txtRegistrationConfirmPassword)).setText("");
            //((EditText) findViewById(R.id.txtRegistrationUsername)).setText("");

            btnRegistration.setEnabled(true);
            layoutWait.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Check if password == repeat password
     * @return
     */
    private boolean passwordMissMatch() {
        //Log.d("signupcheck",password +" "+repeatPassword);
        if(password.equals(repeatPassword))return false;
        return true;

    }

    /**
     * Check if email is valid
     * @return
     */
    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    /**
     * Check if all fields are proper
     * @return
     */
    private boolean showSignUpError(){


        if(email.isEmpty() || password.isEmpty() || username.isEmpty() || repeatPassword.isEmpty()){

            return true;
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
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
