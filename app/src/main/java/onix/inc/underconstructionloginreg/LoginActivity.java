package onix.inc.underconstructionloginreg;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
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
import java.util.Locale;

public class LoginActivity extends Activity {

    public EditText txtEmail, txtPassword;
    public Button btnLogin;
    public LinearLayout layoutWait;
    public TextView errorText;
    String email, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //==hardcode to use bangla (en/bn)=====================
        Locale locale = new Locale("en");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getApplicationContext().getResources().updateConfiguration(config, null);
        //=============================================

        setContentView(R.layout.activity_login);

        txtEmail = (EditText) findViewById(R.id.txtLoginEmail);
        txtPassword = (EditText) findViewById(R.id.txtLoginPassword);
        errorText = (TextView) findViewById(R.id.lblLoginError);
        layoutWait = (LinearLayout) findViewById(R.id.layoutLoginWait);
        Button btnRegistration = (Button) findViewById(R.id.btnLoginRegistration);
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
                Intent k = new Intent(LoginActivity.this, ForgetActivity.class);
                startActivity(k);
            }
        });

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
            else if (isVerified.equals("0"))
            {
                Utility.CurrentUser.setUserId(userId);
                Intent k = new Intent(LoginActivity.this, VerificationActivity.class);
                startActivity(k);
                finish();

            } else {
                errorText.setText("Success! Hello " + userName + " :" + userId);
                Utility.CurrentUser.setUserId(userId);
                Utility.CurrentUser.setUsername(userName);

                //finish();
                //Intent intent=new Intent(LoginActivity.this,LoginActivity.class);
                //startActivity(intent);
            }
        }
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


}
