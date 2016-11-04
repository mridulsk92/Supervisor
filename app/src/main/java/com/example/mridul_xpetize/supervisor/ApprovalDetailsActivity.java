package com.example.mridul_xpetize.supervisor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.squareup.picasso.Picasso;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ApprovalDetailsActivity extends AppCompatActivity {

    ProgressDialog pDialog;
    String userId_st;
    PreferencesHelper pref;
    String id, detail_id, assignedBy, createdBy, name_st, desc_st, comments_st, startDate, endDate, status_st, assignedTo_st, comments_updated;
    int response_json;
    Drawer result = null;
    List<String> popupList = new ArrayList<String>();
    List<String> popupListId = new ArrayList<String>();
    EditText assignEdit;
    String assignedByName;
    ImageView decodedImg;
    SharedPreferences prefNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_details);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Inspector");

        //Get Preference Values
        prefNew = getSharedPreferences("LangPref", Activity.MODE_PRIVATE);
        pref = new PreferencesHelper(ApprovalDetailsActivity.this);
        final String acc_name = pref.GetPreferences("UserName");
        userId_st = pref.GetPreferences("UserId");


        //Adding Header to the Navigation Drawer
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName(acc_name).withEmail(acc_name + "@gmail.com").withIcon(getResources().getDrawable(R.drawable.profile))
                ).build();

        //Side Drawer contents
        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .withSelectedItem(-1)
                .withDisplayBelowStatusBar(true)
                .addDrawerItems(
                        new SecondaryDrawerItem().withName(R.string.About).withIcon(getResources().getDrawable(R.drawable.ic_about)).withSelectable(false),
                        new PrimaryDrawerItem().withName(getString(R.string.Language)).withIcon(getResources().getDrawable(R.drawable.language_switch_ic)).withIdentifier(3).withSelectable(false),
                        new SecondaryDrawerItem().withName(R.string.LogOut).withIcon(getResources().getDrawable(R.drawable.ic_logout)).withSelectable(false)
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == 1) {

                                //Clicked About

                            } else if (drawerItem.getIdentifier() == 2) {

                                //Clicked LogOut

                            } else if (drawerItem.getIdentifier() == 3) {

                                SharedPreferences sp = getSharedPreferences("LangPref", Activity.MODE_PRIVATE);
                                int selection = sp.getInt("LanguageSelect", -1);
                                AlertDialog.Builder builder = new AlertDialog.Builder(ApprovalDetailsActivity.this);
                                CharSequence[] array = {"English", "Japanese"};
                                builder.setTitle("Select Language")
                                        .setSingleChoiceItems(array, selection, new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                if (which == 1) {
                                                    String lang = "ja";
                                                    pref.SavePreferences("Language", lang);
                                                    SharedPreferences.Editor editor = prefNew.edit();
                                                    editor.putInt("LanguageSelect", which);
                                                    editor.commit();
                                                    changeLang(lang);
                                                } else {
                                                    String lang = "en";
                                                    pref.SavePreferences("Language", lang);
                                                    SharedPreferences.Editor editor = prefNew.edit();
                                                    editor.putInt("LanguageSelect", which);
                                                    editor.commit();
                                                    changeLang(lang);
                                                }
                                            }
                                        })

                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                // User clicked OK, so save the result somewhere

                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {

                                            }
                                        });

                                builder.create();
                                builder.show();
                            }
                        }
                        return false;
                    }
                })
                .build();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        //Initialise
        decodedImg = (ImageView) findViewById(R.id.imageView_decodedImg);
        pref = new PreferencesHelper(ApprovalDetailsActivity.this);
        Button approve = (Button) findViewById(R.id.button_approve);
        Button reject = (Button) findViewById(R.id.button_reject);
        TextView name = (TextView) findViewById(R.id.name);
        TextView desc = (TextView) findViewById(R.id.desc);
        TextView comments = (TextView) findViewById(R.id.comments);
        TextView assignedTo = (TextView) findViewById(R.id.assigned);
        TextView start = (TextView) findViewById(R.id.start);
        TextView end = (TextView) findViewById(R.id.end);
        TextView status = (TextView) findViewById(R.id.status);
//        TextView desc = (TextView)findViewById(R.id.desc);
//        TextView desc = (TextView)findViewById(R.id.desc);
//        TextView desc = (TextView)findViewById(R.id.desc);

        //Get Intent
        Intent i = getIntent();
        id = i.getStringExtra("Id");
        assignedTo_st = i.getStringExtra("AssignedTo");
        detail_id = i.getStringExtra("DetailsId");
        assignedBy = i.getStringExtra("AssignedById");
        assignedByName = i.getStringExtra("AssignedByName");
        createdBy = i.getStringExtra("CreatedById");
        name_st = i.getStringExtra("Name");
        desc_st = i.getStringExtra("Desc");
        comments_st = i.getStringExtra("Comments");
        startDate = i.getStringExtra("StartDate");
        endDate = i.getStringExtra("EndDate");
        status_st = i.getStringExtra("StatusId");

        //Set TextView Values
        name.setText(name_st);
        desc.setText(desc_st);
        comments.setText(comments_st);
        assignedTo.setText("Assigned By : " + assignedByName);
        start.setText(startDate);
        end.setText(endDate);
        status.setText(status_st);

        //onClick of Approve
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SubmitDialog("Approve");
            }
        });

        //onClick of Reject
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RejectDialog();
            }
        });

        //Load pic
        String url = "http://vikray.in/NImage/SubTask"+id+".jpg";
        Log.d("URL Image", url);
        Picasso.
                with(ApprovalDetailsActivity.this).
                load(url).
                into(decodedImg);
    }

    private void RejectDialog() {

        LayoutInflater factory = LayoutInflater.from(ApprovalDetailsActivity.this);
        final View addView = factory.inflate(
                R.layout.submit_dialog_reject, null);
        final AlertDialog addDialog = new AlertDialog.Builder(ApprovalDetailsActivity.this).create();
        addDialog.setView(addView);

        //Initialise
        final EditText commentBox = (EditText) addView.findViewById(R.id.subimt_comment_text);
        Button submitTask = (Button) addView.findViewById(R.id.button_submit);
        assignEdit = (EditText) addView.findViewById(R.id.editText_assign);

        //onClick of EditText
        assignEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new GetWorkerList().execute();
            }
        });


        //onClick of SubmitButton
        submitTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tempStart = getCurrentTimeStamp();
                String tempEnd = getCurrentTimeStamp();
                String currentTime = getCurrentTimeStamp();
                comments_updated = commentBox.getText().toString();

                if (isNetworkAvailable()) {
                    new PostTask().execute("Reject");
                } else {
                    Toast.makeText(ApprovalDetailsActivity.this, "No Internet Connection. Data stored locally", Toast.LENGTH_SHORT).show();
//                    SQLite entry = new SQLite(ApprovalDetailsActivity.this);
//                    entry.open();
//                    entry.createEntry(detail_id, id, assignedTo_st, tempStart, tempEnd, currentTime, assignedBy, "6", "1", comments_updated, createdBy);
//                    entry.createEntryNotification("Rejected", id, userId_st, assignedTo_st, userId_st);
//                    entry.createEntryAssigned(id, assignedTo_st, assignedBy, "1", "1", comments_updated, createdBy);
//                    String c = entry.getCount();
//                    String n = entry.getCountNotification();
//                    String a = entry.getCountAssigned();
//                    entry.close();
//                    Log.d("Count", "Task :" + c + "Notification :" + n + "Assigned :" + a);
                }
            }
        });

        addDialog.show();
    }

    private void SubmitDialog(String arg0) {

        final String condition = arg0;
        LayoutInflater factory = LayoutInflater.from(ApprovalDetailsActivity.this);
        final View addView = factory.inflate(
                R.layout.submit_dialog, null);
        final AlertDialog addDialog = new AlertDialog.Builder(ApprovalDetailsActivity.this).create();
        addDialog.setView(addView);

        //Initialise
        final EditText commentBox = (EditText) addView.findViewById(R.id.subimt_comment_text);
        Button submitTask = (Button) addView.findViewById(R.id.button_submit);

        //onClick of SubmitButton
        submitTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                comments_updated = commentBox.getText().toString();
                String tempStart = getCurrentTimeStamp();
                String currentTime = getCurrentTimeStamp();
                String tempEnd = getCurrentTimeStamp();

                if (isNetworkAvailable()) {
                    new PostTask().execute(condition);
                } else {
                    Toast.makeText(ApprovalDetailsActivity.this, "No Internet connection. Data stored locally", Toast.LENGTH_SHORT).show();
//                    SQLite entry = new SQLite(ApprovalDetailsActivity.this);
//                    entry.open();
//                    entry.createEntry(detail_id, id, assignedTo_st, tempStart, tempEnd, currentTime, assignedBy, "8", "1", comments_updated, createdBy);
//                    entry.createEntryNotification("Closed", id, userId_st, assignedTo_st, userId_st);
//                    String c = entry.getCount();
//                    String n = entry.getCountNotification();
//                    entry.close();
//                    Log.d("Count", "Task :" + c + "Notification :" + n);
                }
            }
        });

        addDialog.show();
    }

    private class GetWorkerList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            popupList.clear();
            popupListId.clear();
            // Showing progress dialog
            pDialog = new ProgressDialog(ApprovalDetailsActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            String url = getString(R.string.url) + "EagleXpetizeService.svc/UsersListByType/Worker";

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {

                    JSONArray workers = new JSONArray(jsonStr);

                    // looping through Array
                    for (int i = 0; i < workers.length(); i++) {
                        JSONObject c = workers.getJSONObject(i);

                        String id = c.getString("UserId");
                        String name = c.getString("UserName");
                        String type = c.getString("Type");

                        popupList.add(name);
                        popupListId.add(id);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            //Create sequence of items
            final CharSequence[] Animals = popupList.toArray(new String[popupList.size()]);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ApprovalDetailsActivity.this);
            dialogBuilder.setTitle("Animals");
            dialogBuilder.setItems(Animals, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    String selectedId = popupListId.get(item);
                    assignedTo_st = selectedId;
                    assignEdit.setText(popupList.get(item));
                }
            });
            //Create alert dialog object via builder
            AlertDialog alertDialogObject = dialogBuilder.create();
            //Show the dialog
            alertDialogObject.show();
        }
    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        String strDate = sdf.format(now);
        return strDate;
    }

    private class PostTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ApprovalDetailsActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            // Creating service handler class instance

            String check = arg0[0];
            JSONStringer userJson = null;
            HttpPost request = new HttpPost(getString(R.string.url) + "EagleXpetizeService.svc/UpdateAssignedTask");
            String temp_start = getCurrentTimeStamp();
            String temp_end = getCurrentTimeStamp();

            if (check.equals("Approve")) {

                request.setHeader("Accept", "application/json");
                request.setHeader("Content-type", "application/json");

                // Build JSON string
                try {
                    userJson = new JSONStringer()
                            .object()
                            .key("taskDetails")
                            .object()
                            .key("TaskDetailsId").value(detail_id)
                            .key("TaskId").value(id)
                            .key("AssignedToId").value(assignedTo_st)
                            .key("StartDateStr").value(temp_start)
                            .key("EndDateStr").value(temp_end)
                            .key("AssignedById").value(assignedBy)
                            .key("StatusId").value(8)
                            .key("IsSubTask").value(1)
                            .key("Comments").value(comments_updated)
                            .key("CreatedBy").value(createdBy)
                            .endObject()
                            .endObject();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

                request.setHeader("Accept", "application/json");
                request.setHeader("Content-type", "application/json");

                // Build JSON string
                try {
                    userJson = new JSONStringer()
                            .object()
                            .key("taskDetails")
                            .object()
                            .key("TaskDetailsId").value(detail_id)
                            .key("TaskId").value(id)
                            .key("AssignedToId").value(assignedTo_st)
                            .key("StartDateStr").value(temp_start)
                            .key("EndDateStr").value(temp_end)
                            .key("AssignedById").value(assignedBy)
                            .key("StatusId").value(6)
                            .key("IsSubTask").value(1)
                            .key("Comments").value(comments_updated)
                            .key("CreatedBy").value(createdBy)
                            .endObject()
                            .endObject();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Log.d("Json", String.valueOf(userJson));
            StringEntity entity = null;
            try {
                entity = new StringEntity(userJson.toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            entity.setContentType("application/json");

            request.setEntity(entity);

            // Send request to WCF service
            DefaultHttpClient httpClient = new DefaultHttpClient();
            try {
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String response = httpClient.execute(request, responseHandler);
                Log.d("res", response);

                if (response != null) {

                    try {

                        //Get Data from Json
                        JSONObject jsonObject = new JSONObject(response);

                        String message = jsonObject.getString("UpdateAssignedTaskResult");

                        //Save userid and username if success
                        if (message.equals("success")) {
                            response_json = 200;
                        } else {
                            response_json = 201;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return check;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (response_json == 200) {

                if (result.equals("Reject")) {
                    new PostNotification().execute("Rejected");
                    new AssignTask().execute();
                    new PostHistory().execute("Rejected");
                } else {
                    Toast.makeText(ApprovalDetailsActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    new PostNotification().execute("Approved");
                    new PostHistory().execute("Closed");
                }
            } else {
                Toast.makeText(ApprovalDetailsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class PostNotification extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Showing progress dialog
            pDialog = new ProgressDialog(ApprovalDetailsActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            String username = pref.GetPreferences("UserName");
            String status = params[0];
            String noti_message = username + " has "+status+" the Task : "+name_st;

            HttpPost request = new HttpPost(getString(R.string.url) + "EagleXpetizeService.svc/NewNotification");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer userJson = null;
            // Build JSON string
            try {
                userJson = new JSONStringer()
                        .object()
                        .key("notification")
                        .object()
                        .key("Description").value(noti_message)
                        .key("TaskId").value(id)
                        .key("ById").value(userId_st)
                        .key("ToId").value(assignedTo_st)
                        .key("CreatedBy").value(userId_st)
                        .endObject()
                        .endObject();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("Json", String.valueOf(userJson));

            StringEntity entity = null;
            try {
                entity = new StringEntity(userJson.toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            entity.setContentType("application/json");

            request.setEntity(entity);

            // Send request to WCF service
            DefaultHttpClient httpClient = new DefaultHttpClient();
            try {
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String response = httpClient.execute(request, responseHandler);

                Log.d("res", response);

                if (response != null) {

                    try {

                        //Get Data from Json
                        JSONObject jsonObject = new JSONObject(response);

                        String message = jsonObject.getString("NewNotificationResult");

                        //Save userid and username if success
                        if (message.equals("success")) {
                            response_json = 200;
                        } else {
                            response_json = 201;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (response_json == 200) {
                Toast.makeText(ApprovalDetailsActivity.this, "Success", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ApprovalDetailsActivity.this, MainActivity.class);
                startActivity(i);
            } else {
                Toast.makeText(ApprovalDetailsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class PostHistory extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ApprovalDetailsActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            String historyDate = getCurrentTimeStamp();
            String status = params[0];
            String user_name = pref.GetPreferences("UserName");

            HttpPost request = new HttpPost(getString(R.string.url) + "EagleXpetizeService.svc/NewHistory");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer userJson = null;
            // Build JSON string
            try {
                userJson = new JSONStringer()
                        .object()
                        .key("history")
                        .object()
                        .key("TaskId").value(id)
                        .key("IsSubTask").value(1)
                        .key("Notes").value("Reviewed By : " + user_name)
                        .key("Comments").value(status)
//                        .key("HistoryDate").value(historyDate)
//                        .key("CreatedDate").value(createdDate)
                        .key("CreatedBy").value(userId_st)
                        .endObject()
                        .endObject();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("Json", String.valueOf(userJson));

            StringEntity entity = null;
            try {
                entity = new StringEntity(userJson.toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            entity.setContentType("application/json");

            request.setEntity(entity);

            // Send request to WCF service
            DefaultHttpClient httpClient = new DefaultHttpClient();
            try {
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String response = httpClient.execute(request, responseHandler);

                Log.d("res", response);

                if (response != null) {

                    try {

                        //Get Data from Json
                        JSONObject jsonObject = new JSONObject(response);

                        String message = jsonObject.getString("NewHistoryResult");

                        //Save userid and username if success
                        if (message.equals("success")) {
                            response_json = 200;
                        } else {
                            response_json = 201;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (response_json == 200) {
                Toast.makeText(ApprovalDetailsActivity.this, "Success", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ApprovalDetailsActivity.this, ApprovalActivity.class);
                startActivity(i);
            } else {
                Toast.makeText(ApprovalDetailsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class AssignTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ApprovalDetailsActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            HttpPost request = new HttpPost(getString(R.string.url) + "EagleXpetizeService.svc/AssignTask");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            // Build JSON string
            JSONStringer userJson = null;
            try {
                userJson = new JSONStringer()
                        .object()
                        .key("taskDetails")
                        .object()
                        .key("TaskId").value(id)
                        .key("AssignedToId").value(assignedTo_st)
                        .key("AssignedById").value(assignedBy)
                        .key("StatusId").value(1)
                        .key("IsSubTask").value(1)
                        .key("Comments").value(comments_updated)
                        .key("CreatedBy").value(createdBy)
                        .endObject()
                        .endObject();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("Json", String.valueOf(userJson));
            StringEntity entity = null;
            try {
                entity = new StringEntity(userJson.toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            entity.setContentType("application/json");

            request.setEntity(entity);

            // Send request to WCF service
            DefaultHttpClient httpClient = new DefaultHttpClient();
            try {
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String response = httpClient.execute(request, responseHandler);
                Log.d("res", response);
                if (response != null) {

                    try {

                        //Get Data from Json
                        JSONObject jsonObject = new JSONObject(response);

                        String message = jsonObject.getString("AssignTaskResult");

                        if (message.equals("success")) {
                            response_json = 200;
                        } else {
                            response_json = 201;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (response_json == 200) {
                Toast.makeText(ApprovalDetailsActivity.this, "Success", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ApprovalDetailsActivity.this, MainActivity.class);
                startActivity(i);
            } else {
                Toast.makeText(ApprovalDetailsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        //inflate menu
        getMenuInflater().inflate(R.menu.menu_my, menu);

        // Get the notifications MenuItem and LayerDrawable (layer-list)
        MenuItem item_noti = menu.findItem(R.id.action_noti);
        MenuItem item_logOut = menu.findItem(R.id.action_logOut);

        item_logOut.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                return false;
            }
        });

        item_noti.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent i = new Intent(ApprovalDetailsActivity.this, NotificationActivity.class);
                startActivity(i);
                return false;
            }
        });

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        super.finish();
    }

    public void changeLang(String lang) {

        if (lang.equalsIgnoreCase(""))
            return;
        Locale myLocale = new Locale(lang);
//        saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        updateTexts();
    }

    private void updateTexts() {

        Intent i = new Intent(ApprovalDetailsActivity.this, ApprovalDetailsActivity.class);
        startActivity(i);
    }

}
