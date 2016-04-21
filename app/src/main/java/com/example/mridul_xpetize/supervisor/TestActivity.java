package com.example.mridul_xpetize.supervisor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class TestActivity extends AppCompatActivity {

    ProgressDialog pDialog;
    EditText name, loc, desig, username, pass, created;
    String name_st, loc_st, desig_st, username_st, pass_st, create_st;
    Button user_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        user_add = (Button)findViewById(R.id.button_useradd);
        name = (EditText) findViewById(R.id.editText_name);
        loc = (EditText) findViewById(R.id.editText_loc);
        desig = (EditText) findViewById(R.id.editText_desig);
        username = (EditText) findViewById(R.id.editText_username);
        pass = (EditText) findViewById(R.id.editText_passs);
        created = (EditText) findViewById(R.id.editText_create);

        user_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name_st = name.getText().toString();
                loc_st = loc.getText().toString();
                desig_st = desig.getText().toString();
                username_st = username.getText().toString();
                pass_st = pass.getText().toString();
                create_st = created.getText().toString();

                new CreateUser().execute();
            }
        });
    }

    private class CreateUser extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(TestActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

//            String url = "http://vikray.in/MyService.asmx/GetEmployessJSONNewN";
            String url = "http://vikray.in/MyService.asmx/InsertUserDetails?Name="+name_st+"&Designation="+desig_st+"&location="+loc_st+"&username="+username_st+"&password="+pass_st+"&createdby="+create_st;
            // Making a request to url and getting response

            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            Log.d("Response: ", "> " + jsonStr);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            Intent i = new Intent(TestActivity.this, MainActivity.class);
            startActivity(i);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        super.finish();
    }
}
