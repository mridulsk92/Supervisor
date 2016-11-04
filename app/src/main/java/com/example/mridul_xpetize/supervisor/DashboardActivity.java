package com.example.mridul_xpetize.supervisor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    ImageButton view_inspectors, view_workers, approval;
    private Drawer result = null;
    PreferencesHelper pref;
    ListView hidden_not;
    int count;
    ProgressDialog pDialog;
    ArrayList<HashMap<String, Object>> dataList;
    LayoutInflater inflater;
    String db_desc, db_read, db_intent, db_rowId;
    List<String> popupList = new ArrayList<String>();
    ArrayList<Integer> posList = new ArrayList<Integer>();
    ArrayList<String> nameList = new ArrayList<String>();
    SharedPreferences prefNew;
    MenuItem menuItem;
    CardView inspector, operator, approve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Supervisor");

        prefNew = getSharedPreferences("LangPref", Activity.MODE_PRIVATE);
        pref = new PreferencesHelper(DashboardActivity.this);
        String name = pref.GetPreferences("UserName");

        //Adding Header to the Navigation Drawer
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName(name).withEmail(name + "@gmail.com").withIcon(getResources().getDrawable(R.drawable.profile))
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
                        new PrimaryDrawerItem().withName("Manage Templates").withIcon(getResources().getDrawable(R.drawable.ic_templates)).withIdentifier(4).withSelectable(false),
                        new SecondaryDrawerItem().withName(R.string.LogOut).withIcon(getResources().getDrawable(R.drawable.ic_logout)).withSelectable(false)
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == 1) {

                                //Clicked About

                            } else if (drawerItem.getIdentifier() == 2) {

                                //Clicked LogOut
                                pref.SavePreferences("IsLoggedIn", "No");
                                System.exit(0);

                            } else if (drawerItem.getIdentifier() == 3) {

                                SharedPreferences sp = getSharedPreferences("LangPref", Activity.MODE_PRIVATE);
                                int selection = sp.getInt("LanguageSelect", -1);
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DashboardActivity.this);
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
                            }else if(drawerItem.getIdentifier() == 4){

                                final android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(DashboardActivity.this);
                                final CharSequence items[] = {"Create Template", "Edit existing Template"};
                                alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (which == 0) {
                                            Intent i = new Intent(DashboardActivity.this, CreateSubTaskActivity.class);
                                            startActivity(i);
                                        } else {
                                            new GetSubTaskList().execute("All");
                                        }
                                    }
                                });
                                alertDialogBuilder.setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {

                                            }
                                        });

                                android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();

                            }
                        }
                        return false;
                    }
                })
                .build();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        //Initialise
        inspector = (CardView) findViewById(R.id.card_inspector);
        operator = (CardView) findViewById(R.id.card_operator);
        approve = (CardView) findViewById(R.id.card_approve);
        hidden_not = (ListView) findViewById(R.id.listView_hidden_notification);
        dataList = new ArrayList<HashMap<String, Object>>();
        view_inspectors = (ImageButton) findViewById(R.id.imageButton_inspectors);
        view_workers = (ImageButton) findViewById(R.id.imageButton_workers);
        approval = (ImageButton) findViewById(R.id.imageButton_approval);

        //Notification List onClick
        hidden_not.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                count--;
                if (count <= 0) {
                    count = 0;
                }

                String rowId = ((TextView) view.findViewById(R.id.rowId_notification)).getText().toString();
                String intent = ((TextView) view.findViewById(R.id.intent_notification)).getText().toString();
                String description = ((TextView) view.findViewById(R.id.description_notification)).getText().toString();

                SQLite entry = new SQLite(getApplicationContext());
                entry.open();
                entry.updateEntryNotification(rowId, description, "Yes", intent);
                entry.close();

                menuItem.setIcon(buildCounterDrawable(count, R.drawable.blue_bell_small));
                parent.getChildAt(position - hidden_not.getFirstVisiblePosition()).setBackgroundColor(Color.TRANSPARENT);

            }
        });

        //onClick of view inspectors
        inspector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(DashboardActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        //onClick of view worker
        operator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(DashboardActivity.this, WorkerListActivity.class);
                startActivity(i);
            }
        });

        //onClick of approval
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(DashboardActivity.this, ApprovalActivity.class);
                startActivity(i);
            }
        });

//        new GetNotiList().execute();

//        new PostToken().execute();

        new GetNotiListServer().execute();

        new AddToken().execute();
    }

    private class GetSubTaskList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dataList.clear();
            popupList.clear();
//            empty.setVisibility(View.GONE);
            // Showing progress dialog
            pDialog = new ProgressDialog(DashboardActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {

            String url;
            String check = arg0[0];

            if (check.equals("User")) {

                HttpPost request = new HttpPost(getString(R.string.url) + "EagleXpetizeService.svc/TaskAssigned");
                request.setHeader("Accept", "application/json");
                request.setHeader("Content-type", "application/json");

                // Build JSON string
                JSONStringer userJson = null;
                try {
                    userJson = new JSONStringer()
                            .object()
                            .key("taskDetails")
                            .object()
                            .key("TaskDetailsId").value(0)
                            .key("TaskId").value(0)
                            .key("AssignedToId").value(0)
                            .key("AssignedById").value(0)
                            .key("IsSubTask").value(1)
                            .key("StatusId").value(1)
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

                            JSONObject json1 = new JSONObject(response);
                            JSONArray tasks = json1.getJSONArray("TaskAssignedResult");

                            // Looping through Array
                            for (int i = 0; i < tasks.length(); i++) {
                                JSONObject c = tasks.getJSONObject(i);

                                String id = c.getString("TaskId");
                                String name = c.getString("TaskName");
                                String desc = c.getString("TaskDescription");
                                String comments = c.getString("Comments");
                                String isSub = c.getString("IsSubTask");
                                String status = c.getString("Status");
                                String createdBy = c.getString("CreatedBy");
                                int statusId = c.getInt("StatusId");
                                String assignedBy = c.getString("AssignedByName");

                                //adding each child node to HashMap key => value
                                HashMap<String, Object> taskMap = new HashMap<String, Object>();
                                taskMap.put("TaskDescription", desc);
                                taskMap.put("CreatedBy", createdBy);
                                taskMap.put("TaskId", id);
                                taskMap.put("TaskName", name);
                                taskMap.put("IsSub", isSub);
                                taskMap.put("AssignedByName", assignedBy);
                                taskMap.put("StatusId", statusId);
                                taskMap.put("Comments", comments);
                                taskMap.put("Status", status);

                                dataList.add(taskMap);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("ServiceHandler", "Couldn't get any data from the url");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {

                // Creating service handler class instance
                ServiceHandler sh = new ServiceHandler();
                url = getString(R.string.url) + "EagleXpetizeService.svc/SubTasks/0/0/0/1/1";
                Log.d("Url", url);

                // Making a request to url and getting response
                String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
                Log.d("Response: ", "> " + jsonStr);

                if (jsonStr != null) {
                    try {

                        JSONArray tasks = new JSONArray(jsonStr);

                        // looping through All Contacts
                        for (int i = 0; i < tasks.length(); i++) {
                            JSONObject c = tasks.getJSONObject(i);

                            String id = c.getString("TaskId");
                            String createdBy = c.getString("CreatedBy");
                            String name = c.getString("SubTaskName");
                            String desc = c.getString("Description");
                            String comments = c.getString("Comments");
                            String statusId = c.getString("StatusId");
                            String priority = c.getString("Priority");
                            String subId = c.getString("SubTaskId");

                            //tmp hashmap for single contact
                            HashMap<String, Object> taskMap = new HashMap<String, Object>();

                            //adding each child node to HashMap key => value
                            taskMap.put("TaskId", id);
                            taskMap.put("CreatedBy", createdBy);
                            taskMap.put("SubTaskName", name);
                            taskMap.put("Description", desc);
                            taskMap.put("StatusId", statusId);
                            taskMap.put("Comments", comments);
                            taskMap.put("SubTaskId", subId);
                            taskMap.put("Priority", priority);
                            popupList.add(name);
                            dataList.add(taskMap);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                }
            }

            return check;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (result.equals("User")) {
//                cardAdapter = new CustomAdapter(WorkerActivity.this, R.layout.task_list, dataList);
//                added_list.setAdapter(cardAdapter);
            } else {
                CharSequence[] items = popupList.toArray(new CharSequence[popupList.size()]);
                android.app.AlertDialog.Builder builderSingle = new android.app.AlertDialog.Builder(DashboardActivity.this);
                builderSingle.setTitle("Select A Template");

                builderSingle.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builderSingle.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String id = dataList.get(which).get("SubTaskId").toString();
                        String name_st = dataList.get(which).get("SubTaskName").toString();
                        String comments = dataList.get(which).get("Comments").toString();
                        String description = dataList.get(which).get("Description").toString();
                        String createdBy = dataList.get(which).get("CreatedBy").toString();
                        Intent i = new Intent(DashboardActivity.this, ManageTemplateActivity.class);
                        i.putExtra("Id", id);
                        i.putExtra("Name", name_st);
                        i.putExtra("Description", description);
                        i.putExtra("Comments", comments);
                        i.putExtra("CreatedBy", createdBy);
                        startActivity(i);
                    }
                });
                builderSingle.show();
            }
        }
    }

    private class CustomAdapterNot extends ArrayAdapter<HashMap<String, Object>> {

        public CustomAdapterNot(Context context, int textViewResourceId, ArrayList<HashMap<String, Object>> Strings) {

            //let android do the initializing :)
            super(context, textViewResourceId, Strings);
        }

        //class for caching the views in a row
        private class ViewHolder {

            TextView desc, intent, read, rowId;
            LinearLayout noti_linear;
        }

        //Initialise
        ViewHolder viewHolder;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                //inflate the custom layout
                convertView = inflater.from(parent.getContext()).inflate(R.layout.notification_layout, parent, false);
                viewHolder = new ViewHolder();

                //cache the views
                viewHolder.rowId = (TextView) convertView.findViewById(R.id.rowId_notification);
                viewHolder.desc = (TextView) convertView.findViewById(R.id.description_notification);
                viewHolder.noti_linear = (LinearLayout) convertView.findViewById(R.id.not_layout);
                viewHolder.intent = (TextView) convertView.findViewById(R.id.intent_notification);
                viewHolder.read = (TextView) convertView.findViewById(R.id.read_notification);

                //link the cached views to the convertview
                convertView.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) convertView.getTag();

            //set the data to be displayed
            viewHolder.rowId.setText(dataList.get(position).get("RowId").toString());
            viewHolder.read.setText(dataList.get(position).get("Read").toString());
            viewHolder.intent.setText(dataList.get(position).get("Intent").toString());
            viewHolder.desc.setText(dataList.get(position).get("Description").toString());
            viewHolder.noti_linear.setBackgroundColor(Color.LTGRAY);

            if (viewHolder.read.getText().equals("No")) {
                viewHolder.noti_linear.setBackgroundColor(Color.LTGRAY);
            } else {
                viewHolder.noti_linear.setBackgroundColor(Color.TRANSPARENT);
            }
//            for (int i = 0; i < savedList.size(); i++) {
//                Log.d("Test Custom", String.valueOf(savedList.get(i)));
//                if (position == savedList.get(i)) {
//                    viewHolder.noti_linear.setBackgroundColor(Color.TRANSPARENT);
//                } else {
//                    viewHolder.noti_linear.setBackgroundColor(Color.LTGRAY);
//                }
//            }

            return convertView;
        }
    }

    private class GetNotiListServer extends AsyncTask<Void, Void, Void> {

//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            dataList.clear();
//            // Showing progress dialog
//            pDialog = new ProgressDialog(MainActivity.this);
//            pDialog.setMessage("Please wait...");
//            pDialog.setCancelable(true);
//            pDialog.show();
//        }

        @Override
        protected Void doInBackground(Void... params) {

            String user_id = pref.GetPreferences("UserId");

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            String url = getString(R.string.url) + "EagleXpetizeService.svc/Notifications/" + user_id + "/1";
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            Log.d("Url", url);
            Log.d("Response: ", "> " + jsonStr);
            if (jsonStr != null) {

                try {

                    SQLite del = new SQLite(getApplicationContext());
                    del.open();
                    del.deleteNotificationRows();
                    del.close();

                    JSONArray tasks = new JSONArray(jsonStr);

                    for (int i = 0; i < tasks.length(); i++) {
                        JSONObject c = tasks.getJSONObject(i);

                        String id = c.getString("TaskId");
                        String taskName = c.getString("TaskName");
                        String username = c.getString("UserName");
                        String description = c.getString("Description");
                        String byId = c.getString("ById");
                        String toId = c.getString("ToId");
                        String isNew = c.getString("IsNew");

                        String read = "No";
                        String intentData = "Test";
                        SQLite entry = new SQLite(getApplicationContext());
                        entry.open();
                        entry.createEntryNotification(description, read, intentData);
                        String not_count = entry.getCountNotification();
                        Log.d("NotCountFcm :", not_count);
                        entry.close();
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
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

//            if (pDialog.isShowing())
//                pDialog.dismiss();

            new GetNotiListLocal().execute();

        }
    }

    private class GetNotiListLocal extends AsyncTask<Void, Void, Void> {

//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            // Showing progress dialog
//            pDialog = new ProgressDialog(MainActivity.this);
//            pDialog.setMessage(getString(R.string.pDialog_wait));
//            pDialog.setCancelable(true);
//            pDialog.show();
//        }

        @Override
        protected Void doInBackground(Void... params) {

            SQLite notC = new SQLite(DashboardActivity.this);
            notC.open();
            int countNot = Integer.parseInt(notC.getCountNotification());
            notC.close();
            Log.d("Service Count Not", String.valueOf(countNot));

            if (countNot != 0) {
                int i = 0;
                int counter;
                if (countNot < 5) {
                    counter = countNot;
                } else {
                    counter = 5;
                }
                while (i < counter) {

                    SQLite getNot = new SQLite(DashboardActivity.this);
                    getNot.open();
                    String notData[][] = getNot.getNotification();
                    db_rowId = notData[i][0];
                    db_desc = notData[i][1];
                    db_read = notData[i][2];
                    db_intent = notData[i][3];
                    getNot.close();
                    Log.d("Test DEsc", db_desc + db_intent + db_read);

                    HashMap<String, Object> taskMap = new HashMap<String, Object>();
                    taskMap.put("RowId", db_rowId);
                    taskMap.put("Description", db_desc);
                    taskMap.put("Read", db_read);
                    taskMap.put("Intent", db_intent);
                    dataList.add(taskMap);
                    i++;

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

//            if (pDialog.isShowing())
//                pDialog.dismiss();

            // initialize pop up window
            for (int i = 0; i < dataList.size(); i++) {
                if (dataList.get(i).get("Read").equals("No")) {
                    count++;
                }
            }
//            count = dataList.size();
            CustomAdapterNot notAdapter = new CustomAdapterNot(DashboardActivity.this, R.layout.notification_layout, dataList);
            hidden_not.setAdapter(notAdapter);
            menuItem.setIcon(buildCounterDrawable(count, R.drawable.blue_bell_small));

        }
    }

    private class AddToken extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(DashboardActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {


            String token = pref.GetPreferences("FCM TOKEN");
            String userid = pref.GetPreferences("UserId");

            HttpPost request = new HttpPost(getString(R.string.url) + "EagleXpetizeService.svc/AddTokenNew");
//            HttpPost request = new HttpPost(getString(R.string.url) + "EagleXpetizeService.svc/AddToken");

            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            // Build JSON string
            JSONStringer userJson = null;
            try {
                userJson = new JSONStringer()
                        .object()
                        .key("TkDtl")
                        .object()
                        .key("UserId").value(userid)
                        .key("Token").value(token)
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
        }
    }

    public void dialogBox() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Confirm");
        alertDialogBuilder.setMessage("Do You Want to Log Out ?");

        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        //Exit
                        System.exit(0);

                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private Drawable buildCounterDrawable(int count, int backgroundImageId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.counter_menuitem_layout, null);
        view.setBackgroundResource(backgroundImageId);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.rel_panel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setText("" + count);
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(getResources(), bitmap);
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        //inflate menu
        getMenuInflater().inflate(R.menu.menu_my, menu);

        menuItem = menu.findItem(R.id.testAction);
        menuItem.setIcon(buildCounterDrawable(count, R.drawable.blue_bell_small));
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (hidden_not.getVisibility() == View.VISIBLE) {
                    hidden_not.setVisibility(View.GONE);
                } else {
                    hidden_not.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });


//        // Get the notifications MenuItem and LayerDrawable (layer-list)
//        MenuItem item_noti = menu.findItem(R.id.action_noti);
//        MenuItem item_logOut = menu.findItem(R.id.action_logOut);
//
//        item_logOut.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//
//                dialogBox();
//                return false;
//            }
//        });
//
//        item_noti.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//
//                Intent i = new Intent(DashboardActivity.this, NotificationActivity.class);
//                startActivity(i);
//                return false;
//            }
//        });

        return true;
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

        Intent i = new Intent(DashboardActivity.this, DashboardActivity.class);
        startActivity(i);
    }

}
