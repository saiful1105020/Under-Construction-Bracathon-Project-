package com.underconstruction.underconstruction;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

public class ReportProblem extends AppCompatActivity {

    ListView list;
    TextView txtCateDesc;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static int camera =0;
    ImageView mImageView;
    String mCurrentPhotoPath;
    Bitmap imageBitmap;
    Button btnAddReport,btnSaveReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_problem);

        if (camera == 0)
        {
            dispatchTakePictureIntent();
            camera++;
        }
        else
            finish();



        mImageView=(ImageView)findViewById(R.id.addReportImageImageView);
        btnAddReport=(Button)(findViewById(R.id.addReportNewReportButton));
        btnSaveReport=(Button)(findViewById(R.id.addReportSaveReportButton));

        list = (ListView) findViewById(R.id.listView);
        txtCateDesc = (TextView) findViewById(R.id.txtCategoryDesc);

        String[] values = new String[]{"Broken Road", "Manhole", "Risky Intersection", "Crime prone area", "Others"};
        ArrayAdapter<String> adapt = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, values);
        list.setAdapter(adapt);
        list.setItemChecked(0, true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list.getItemAtPosition(position).equals("Others"))
                {
                    txtCateDesc.setVisibility(View.VISIBLE);
                    txtCateDesc.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(txtCateDesc, InputMethodManager.SHOW_IMPLICIT);
                }
                else
                {
                    txtCateDesc.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_report_problem, menu);
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

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
            //FormatAndPopulateLocationTextView();
            //setPic();
            Intent intent =new Intent(this, ReportProblem.class);
            startActivity(intent);
            btnSaveReport.setClickable(true);
            btnAddReport.setClickable(true);
        }
    }
}
