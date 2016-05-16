package com.underconstruction.underconstruction;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.RadioButton;

public class SettingsActivity extends AppCompatActivity {

    RadioButton rbEn, rbBn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        rbEn = (RadioButton) findViewById(R.id.radioLanguageEn);
        rbBn = (RadioButton) findViewById(R.id.radioLanguageBn);
        if (Utility.Settings.get_language(getApplicationContext()).equals("en"))
            rbEn.setChecked(true);
        else if (Utility.Settings.get_language(getApplicationContext()).equals("bn"))
            rbBn.setChecked(true);

        rbBn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    Utility.Settings.set_language(getApplicationContext(), "bn");
                    Utility.Settings.set_app_language(Utility.Settings.get_language(getApplicationContext()), getApplicationContext());
                    Log.d("Lang", "bn");
                }
            }
        });

        rbEn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    Utility.Settings.set_language(getApplicationContext(), "en");
                    Utility.Settings.set_app_language(Utility.Settings.get_language(getApplicationContext()), getApplicationContext());
                    Log.d("Lang", "en");
                }
            }
        });
    }
}
