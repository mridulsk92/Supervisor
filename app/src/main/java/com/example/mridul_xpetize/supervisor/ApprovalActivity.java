package com.example.mridul_xpetize.supervisor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApprovalActivity extends AppCompatActivity {

    ProgressDialog pDialog;
    private Drawer result = null;
    private static String TAG_DATA = "check";
    ArrayList<HashMap<String, String>> taskList;
    ListView tasks;
    TextView desc, type, loc, start, end;
    Button approve, reject;
    List<String> paramList = new ArrayList<String>();
    PreferencesHelper pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval);

        //Add Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        pref = new PreferencesHelper(ApprovalActivity.this);
        String name = pref.GetPreferences("Name");

        //Adding Header to the Navigation Drawer
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName(name).withEmail(name+"@gmail.com").withIcon(getResources().getDrawable(R.drawable.profile))
                ).build();

        //Drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .withDisplayBelowStatusBar(true)
                .addDrawerItems(
                        new SecondaryDrawerItem().withName("About").withIcon(getResources().getDrawable(R.drawable.ic_about)).withSelectable(false),
                        new SecondaryDrawerItem().withName("Log Out").withIcon(getResources().getDrawable(R.drawable.ic_logout)).withSelectable(false)
                ).build();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        //Initialise
        taskList = new ArrayList<HashMap<String, String>>();
        approve = (Button) findViewById(R.id.button_approve);
        reject = (Button) findViewById(R.id.button_reject);
        tasks = (ListView) findViewById(R.id.listView_check);
        start = (TextView) findViewById(R.id.start);
        desc = (TextView) findViewById(R.id.desc);
        type = (TextView) findViewById(R.id.type);
        loc = (TextView) findViewById(R.id.location);
        end = (TextView) findViewById(R.id.end);

        //Load Task
        new LoadTaskList().execute();

        //onItemClickListener for ListView
        tasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Get clicked item and pass it to another activity
                String task = ((TextView) view.findViewById(R.id.textView_task)).getText().toString();
                Intent i = new Intent(ApprovalActivity.this, TaskDetailsActivity.class);
                i.putExtra("task", task);
                startActivity(i);
            }
        });

        //Approve Task Button
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new PostTask().execute();
            }
        });

        //Reject Task Button
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    //AsyncTask to get Tasks(to be edited)
    private class LoadTaskList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ApprovalActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            // Creating service handler class instance
//            ServiceHandler sh = new ServiceHandler();
//
//            // Making a request to url and getting response
//            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

//            Log.d("Response: ", "> " + jsonStr);

//            if (jsonStr != null) {
//                try {
//                    JSONObject jsonObj = new JSONObject(jsonStr);
//
//                    // Getting JSON Array node
//                    contacts = jsonObj.getJSONArray(TAG_CONTACTS);
//
//                    // looping through All Contacts
//                    for (int i = 0; i < contacts.length(); i++) {
//                        JSONObject c = contacts.getJSONObject(i);
//
//                        String id = c.getString(TAG_ID);
//                        String name = c.getString(TAG_NAME);
//                        String email = c.getString(TAG_EMAIL);
//                        String address = c.getString(TAG_ADDRESS);
//                        String gender = c.getString(TAG_GENDER);
//
//                        // Phone node is JSON Object
//                        JSONObject phone = c.getJSONObject(TAG_PHONE);
//                        String mobile = phone.getString(TAG_PHONE_MOBILE);
//                        String home = phone.getString(TAG_PHONE_HOME);
//                        String office = phone.getString(TAG_PHONE_OFFICE);

            // tmp hashmap for single contact
            HashMap<String, String> contact = new HashMap<String, String>();
            contact.put(TAG_DATA, "task1");
            taskList.add(contact);

            HashMap<String, String> temp = new HashMap<String, String>();
            temp.put(TAG_DATA, "task2");
            taskList.add(temp);

//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                Log.e("ServiceHandler", "Couldn't get any data from the url");
//            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            ListAdapter adapter = new SimpleAdapter(
                    ApprovalActivity.this, taskList,
                    R.layout.layout_task_list, new String[]{TAG_DATA
            }, new int[]{R.id.textView_task
            });

            tasks.setAdapter(adapter);
        }
    }

    private class PostTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            paramList.add("mridul");
            paramList.add("ajay");
            pDialog = new ProgressDialog(ApprovalActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            String url = "http://vikray.in/MyService.asmx/PostEmployessJSONNewNN";
//            String url = "http://requestb.in/13ae0821";
            List<NameValuePair> paramse = new ArrayList<NameValuePair>();
            for (int i = 0; i < paramList.size(); i++) {
                paramse.add(new BasicNameValuePair("name", paramList.get(i)));
            }
            ServiceHandler sh = new ServiceHandler();
            String res = sh.makeServiceCall(url, ServiceHandler.POST, paramse);
            Log.d("Response", res);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
