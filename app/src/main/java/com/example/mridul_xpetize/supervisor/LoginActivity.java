package com.example.mridul_xpetize.supervisor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    Button login;
    EditText username, password;
    String username_st, password_st;
    int response;
    ProgressDialog pDialog;
    PreferencesHelper pref;

    ArrayList<HashMap<String, String>> dataList;

    JSONArray tasks;
    private static String TAG_NAME = "UserName";
    private static String TAG_ID = "UserId";
    private static String TAG_MESSAGE = "Message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialise
        login = (Button) findViewById(R.id.button_login);
        username = (EditText) findViewById(R.id.editText_username);
        password = (EditText) findViewById(R.id.editText_password);
        dataList = new ArrayList<HashMap<String, String>>();
        pref = new PreferencesHelper(LoginActivity.this);

        //onClick of Login button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username_st = username.getText().toString();
                password_st = password.getText().toString();

                new PostLogin().execute();

            }
        });
    }

    private class PostLogin extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            String url = getString(R.string.url) + "EagleXpetizeService.svc/CheckUserLogin/" + username_st + "/" + password_st + "/Supervisor";

            Log.d("url", url);
            // Making a request to url and getting response

            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {

                try {

                    JSONObject jsonObject = new JSONObject(jsonStr);

                    String id = jsonObject.getString(TAG_ID);
                    String message = jsonObject.getString(TAG_MESSAGE);
                    String name = jsonObject.getString(TAG_NAME);

                    if (message.equals("Success")) {
                        response = 200;
                        pref.SavePreferences("UserId",id);
                        pref.SavePreferences("UserName",name);
                    } else {
                        response = 201;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                response = 201;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (response == 200) {
                Intent i = new Intent(LoginActivity.this, DashboardActivity.class);
                startActivity(i);
            } else {
                Toast.makeText(LoginActivity.this, "Please Check Username and Password", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        super.finish();
    }
}
