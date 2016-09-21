package com.example.mridul_xpetize.supervisor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class InspectorActivity extends AppCompatActivity {

    TextView inspector;
    ListView added_list;
    private Drawer result = null;
    ImageButton startCal, endCal;
    ArrayList<String> spinnerData = new ArrayList<String>();
    Calendar myCalendarS, myCalendarE;
    EditText startDate, endDate;
    ProgressDialog pDialog;
    int priority;
    String insp_id, isSubTask = "No";
    JSONArray tasks;
    PreferencesHelper pref;
    LayoutInflater inflater;
    CustomAdapter cardAdapter;
    int response_json;
    String byId, taskId, nameTask;

    List<String> popupList = new ArrayList<String>();
    List<String> popupListId = new ArrayList<String>();

    String user_id;
    ArrayList<HashMap<String, Object>> dataList;
    SharedPreferences prefNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspector);

        //Initialise and add Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Supervisor");

        prefNew = getSharedPreferences("LangPref", Activity.MODE_PRIVATE);
        pref = new PreferencesHelper(InspectorActivity.this);
        String acc_name = pref.GetPreferences("UserName");
        user_id = pref.GetPreferences("UserId");

        //Add header to navigation drawer
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(InspectorActivity.this);
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
        added_list = (ListView) findViewById(R.id.listView_task);
        dataList = new ArrayList<>();
        inspector = (TextView) findViewById(R.id.textView_inspector);

        //onItemClick Listener of listview
        added_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (isSubTask.equals("No")) {
                    //Get TextView values and assign to String
                    String idTask = ((TextView) view.findViewById(R.id.task_id)).getText().toString();
                    String name = ((TextView) view.findViewById(R.id.taskname)).getText().toString();
                    String comments = ((TextView) view.findViewById(R.id.comments)).getText().toString();
                    String desc = ((TextView) view.findViewById(R.id.desc)).getText().toString();
                    String assignedBy = ((TextView) view.findViewById(R.id.assigned)).getText().toString();
                    String status = ((TextView) view.findViewById(R.id.status)).getText().toString();

                    Intent i = new Intent(InspectorActivity.this, TaskDetailsActivity.class);
                    i.putExtra("Id", idTask);
                    i.putExtra("Name", name);
                    i.putExtra("Status", status);
                    i.putExtra("Comments", comments);
                    i.putExtra("Description", desc);
                    i.putExtra("AssignedBy", assignedBy);
                    startActivity(i);
                } else {

                    //Show SubTask details in dialog box
                    String name_st = ((TextView) view.findViewById(R.id.subName)).getText().toString();
                    String comments_st = ((TextView) view.findViewById(R.id.comments)).getText().toString();
                    String desc_st = ((TextView) view.findViewById(R.id.desc)).getText().toString();
                    String status_st = ((TextView) view.findViewById(R.id.statusId)).getText().toString();
                    String assigned_st = ((TextView) view.findViewById(R.id.assignedBy)).getText().toString();

                    LayoutInflater factory = LayoutInflater.from(InspectorActivity.this);
                    final View addView = factory.inflate(
                            R.layout.dialog_taskdetails, null);
                    final AlertDialog detailDialog = new AlertDialog.Builder(InspectorActivity.this).create();
                    detailDialog.setView(addView);

                    //Initialise
                    TextView subName = (TextView) addView.findViewById(R.id.view_subName);
                    TextView desc = (TextView) addView.findViewById(R.id.view_description);
                    TextView comments = (TextView) addView.findViewById(R.id.view_comments);
                    TextView status = (TextView) addView.findViewById(R.id.view_status);
                    TextView assigned = (TextView) addView.findViewById(R.id.view_assigned);
                    ImageButton close = (ImageButton) addView.findViewById(R.id.imageButton_close);

                    //SetTextValues
                    subName.setText(name_st);
                    desc.setText("Description : " + desc_st);
                    comments.setText("Comments : " + comments_st);
                    status.setText("Status : " + status_st);
                    assigned.setText("Assigned By : " + assigned_st);

                    //ok button onClick
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            detailDialog.dismiss();
                        }
                    });

                    detailDialog.show();
                }
            }
        });

        //Get Inspector name and display it
        Intent i = getIntent();
        insp_id = i.getStringExtra("Id");
        String name = i.getStringExtra("name");
        inspector.setText(name);

        new GetTaskList().execute("User");

        //onClick of Floating Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Show DialogBox
                final android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(InspectorActivity.this);
                final CharSequence items[] = {"Select Pre Defined tasks", "Create Tasks"};
                alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                            //Select Pre defined tasks
                            new GetTaskList().execute("All");
                        } else {
                            //Create tasks
                            AddTask();
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
        });
    }

    private void AddTask() {

        LayoutInflater factory = LayoutInflater.from(this);
        final View addView = factory.inflate(
                R.layout.addtask_dialog, null);
        final AlertDialog addDialog = new AlertDialog.Builder(this).create();
        addDialog.setView(addView);

        //Initialise
        startCal = (ImageButton) addView.findViewById(R.id.imageButton_startDate);
        endCal = (ImageButton) addView.findViewById(R.id.imageButton_endDate);
        myCalendarS = Calendar.getInstance();
        myCalendarE = Calendar.getInstance();
        final EditText task_name = (EditText) addView.findViewById(R.id.editText_name);
        final EditText editDescription = (EditText) addView.findViewById(R.id.editText_desc);
        final Spinner typeSpinner = (Spinner) addView.findViewById(R.id.spinner);
        startDate = (EditText) addView.findViewById(R.id.editText_start);
        endDate = (EditText) addView.findViewById(R.id.editText_end);
        final EditText loc = (EditText) addView.findViewById(R.id.editText_location);
        final EditText comments = (EditText) addView.findViewById(R.id.editText_comments);

        //Add button onClick
        Button addTask = (Button) addView.findViewById(R.id.button_add);
        addTask.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String desc = editDescription.getText().toString();
                String name_st = task_name.getText().toString();
                String stdate = startDate.getText().toString();
                String enddate = endDate.getText().toString();
                String comments_st = comments.getText().toString();
                String loc_st = loc.getText().toString();
                if (typeSpinner.getSelectedItem().equals("High")) {
                    priority = 1;
                } else if (typeSpinner.getSelectedItem().equals("Medium")) {
                    priority = 2;
                } else if (typeSpinner.getSelectedItem().equals("Low")) {
                    priority = 3;
                } else {
                    priority = 1;
                }

                addDialog.dismiss();
                String createdBy = user_id;
                ArrayList<String> passing = new ArrayList<String>();
                passing.add(desc);
                passing.add(name_st);
                passing.add(loc_st);
                passing.add(comments_st);
                passing.add(createdBy);
                new PostTasks().execute(passing);
            }
        });

        //Date Picker
        final DatePickerDialog.OnDateSetListener dateStart = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendarS.set(Calendar.YEAR, year);
                myCalendarS.set(Calendar.MONTH, monthOfYear);
                myCalendarS.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelStart();
            }
        };

        final DatePickerDialog.OnDateSetListener dateEnd = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendarE.set(Calendar.YEAR, year);
                myCalendarE.set(Calendar.MONTH, monthOfYear);
                myCalendarE.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelEnd();
            }
        };

        startCal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(InspectorActivity.this, dateStart, myCalendarS
                        .get(Calendar.YEAR), myCalendarS.get(Calendar.MONTH),
                        myCalendarS.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        endCal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(InspectorActivity.this, dateEnd, myCalendarE
                        .get(Calendar.YEAR), myCalendarE.get(Calendar.MONTH),
                        myCalendarE.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //Spinner
        ArrayList<String> spinnerData = new ArrayList<String>();
        spinnerData.add("Select Priority");
        spinnerData.add("High");
        spinnerData.add("Medium");
        spinnerData.add("Low");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerData);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        typeSpinner.setAdapter(dataAdapter);

        addDialog.show();
    }

    private class AssignTask extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(InspectorActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(ArrayList<String>... params) {

            ArrayList<String> passed = params[0]; //get passed arraylist
            String taskid_st = passed.get(0);
            taskId = taskid_st;
            String userId_st = passed.get(1);
            String createdBy_st = passed.get(2);
            byId = createdBy_st;
            String status_st = passed.get(3);
            String comments_st = passed.get(4);
            String current_time = getCurrentTimeStamp();

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
                        .key("TaskId").value(taskid_st)
                        .key("AssignedToId").value(insp_id)
                        .key("AssignedById").value(createdBy_st)
                        .key("AssignedDateStr").value(current_time)
                        .key("StatusId").value(status_st)
                        .key("IsSubTask").value(0)
                        .key("Comments").value(comments_st)
                        .key("CreatedBy").value(createdBy_st)
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
        protected void onPostExecute(ArrayList<String> result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            new PostNotification().execute("Assigned");

        }
    }

    private class PostNotification extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Showing progress dialog
            pDialog = new ProgressDialog(InspectorActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            String username = pref.GetPreferences("UserName");
            String status = params[0];
            String noti_message = username + " has " + status + " the Task : " + nameTask;

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
                        .key("TaskId").value(taskId)
                        .key("ById").value(byId)
                        .key("ToId").value(insp_id)
                        .key("CreatedBy").value(byId)
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
                new GetTaskList().execute("User");
            } else {
                Toast.makeText(InspectorActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                new GetTaskList().execute("User");
            }
        }
    }

    private class PostTasks extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(InspectorActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(ArrayList<String>... passing) {


            ArrayList<String> passed = passing[0]; //get passed arraylist
            String desc = passed.get(0);
            String name = passed.get(1);
            nameTask = name;
            String loc = passed.get(2);
            String comments = passed.get(3);
            String createdBy = passed.get(4);
            String current_time = getCurrentTimeStamp();

            HttpPost request = new HttpPost(getString(R.string.url) + "EagleXpetizeService.svc/NewTask");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            // Build JSON string
            JSONStringer userJson = null;
            try {
                userJson = new JSONStringer()
                        .object()
                        .key("task")
                        .object()
                        .key("TaskName").value(name)
                        .key("Description").value(desc)
                        .key("CreatedDateStr").value(current_time)
                        .key("ModifiedDateStr").value(current_time)
                        .key("Location").value(loc)
                        .key("StatusId").value("1")
                        .key("Comments").value(comments)
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
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            new GetTaskList().execute("User");
        }
    }

    private class GetTaskList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dataList.clear();
            popupList.clear();
            // Showing progress dialog
            pDialog = new ProgressDialog(InspectorActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {

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
                            .key("AssignedToId").value(insp_id)
                            .key("AssignedById").value(0)
                            .key("IsSubTask").value(0)
                            .key("StatusId").value(0)
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
                            tasks = json1.getJSONArray("TaskAssignedResult");

                            for (int i = 0; i < tasks.length(); i++) {
                                JSONObject c = tasks.getJSONObject(i);

                                String id = c.getString("TaskId");
                                String name = c.getString("TaskName");
                                String comments = c.getString("Comments");
                                String assignedBy = c.getString("AssignedByName");
                                String desc = c.getString("TaskDescription");
//                            String loc = c.getString("Location");

                                // adding each child node to HashMap key => value
                                HashMap<String, Object> taskMap = new HashMap<String, Object>();

                                taskMap.put("TaskDescription", desc);
                                taskMap.put("TaskName", name);
                                taskMap.put("TaskId", id);
                                taskMap.put("AssignedByName", "Assigned By : " + assignedBy);
                                taskMap.put("Comments", comments);
//                            taskMap.put("Location", loc);
                                dataList.add(taskMap);

//                                JSONArray subTasks = c.getJSONArray("SubTasks");
//                                //Loop through SubTasks
//                                for (int j = 0; j < subTasks.length(); j++) {
//
//                                    JSONObject a = subTasks.getJSONObject(j);
//                                    String sub_id = a.getString("SubTaskId");
//                                    String sub_desc = a.getString("Description");
//
//                                    //Load Description and Id's in List
//                                    popupList.add(sub_desc);
//                                    popupListId.add(sub_id);
//                                }
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

                ServiceHandler sh = new ServiceHandler();
                String url = getString(R.string.url) + "EagleXpetizeService.svc/Tasks/0/0/1";
                Log.d("URL", url);

                // Making a request to url and getting response
                String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

                Log.d("Response: ", "> " + jsonStr);

                if (jsonStr != null) {

                    try {

                        tasks = new JSONArray(jsonStr);
                        // looping through Array
                        for (int i = 0; i < tasks.length(); i++) {
                            JSONObject c = tasks.getJSONObject(i);

                            String id = c.getString("TaskId");
                            String name = c.getString("TaskName");
                            String comments = c.getString("Comments");
                            String desc = c.getString("Description");
                            String loc = c.getString("Location");
                            String assigned = c.getString("CreatedBy");

                            // adding each child node to HashMap key => value
                            HashMap<String, Object> taskMap = new HashMap<String, Object>();

                            taskMap.put("Description", desc);
                            taskMap.put("TaskName", name);
                            taskMap.put("TaskId", id);
                            taskMap.put("Comments", comments);
                            taskMap.put("Location", loc);
                            taskMap.put("CreatedBy", assigned);
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
                cardAdapter = new CustomAdapter(InspectorActivity.this, R.layout.task_list, dataList);
                added_list.setAdapter(cardAdapter);
            } else {

                CharSequence[] items = popupList.toArray(new CharSequence[popupList.size()]);
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(InspectorActivity.this);
                builderSingle.setTitle("Select A Task");

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
                        String id = dataList.get(which).get("TaskId").toString();
                        nameTask = dataList.get(which).get("TaskName").toString();
                        String comments = dataList.get(which).get("Comments").toString();
                        String createdBy = dataList.get(which).get("CreatedBy").toString();
                        ArrayList<String> passing = new ArrayList<String>();
                        passing.add(id);
                        passing.add(user_id);
                        passing.add(createdBy);
                        passing.add("1");
                        passing.add(comments);
                        passing.add(createdBy);
                        new AssignTask().execute(passing);
                    }
                });
                builderSingle.show();
            }
        }
    }

    private class CustomAdapter extends ArrayAdapter<HashMap<String, Object>> {

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<HashMap<String, Object>> Strings) {

            //let android do the initializing :)
            super(context, textViewResourceId, Strings);
        }

        //class for caching the views in a row
        private class ViewHolder {

            TextView status, desc, comments, startdate, enddate, loc, id, taskName, assignedBy;
        }

        //Initialise
        ViewHolder viewHolder;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                //inflate the custom layout
                convertView = inflater.from(parent.getContext()).inflate(R.layout.task_list_main, parent, false);
                viewHolder = new ViewHolder();

                //cache the views
                viewHolder.assignedBy = (TextView) convertView.findViewById(R.id.assigned);
                viewHolder.taskName = (TextView) convertView.findViewById(R.id.taskname);
                viewHolder.status = (TextView) convertView.findViewById(R.id.status);
                viewHolder.desc = (TextView) convertView.findViewById(R.id.desc);
                viewHolder.comments = (TextView) convertView.findViewById(R.id.comments);
                viewHolder.startdate = (TextView) convertView.findViewById(R.id.start);
                viewHolder.enddate = (TextView) convertView.findViewById(R.id.end);
                viewHolder.loc = (TextView) convertView.findViewById(R.id.location);
                viewHolder.id = (TextView) convertView.findViewById(R.id.task_id);

                //link the cached views to the convertview
                convertView.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) convertView.getTag();

            //set the data to be displayed
            viewHolder.taskName.setText(dataList.get(position).get("TaskName").toString());
            viewHolder.id.setText(dataList.get(position).get("TaskId").toString());
            viewHolder.assignedBy.setText(dataList.get(position).get("AssignedByName").toString());
            viewHolder.desc.setText(dataList.get(position).get("TaskDescription").toString());
            viewHolder.comments.setText(dataList.get(position).get("Comments").toString());
//            viewHolder.loc.setText(dataList.get(position).get("Location").toString());
            return convertView;
        }
    }

    private void updateLabelStart() {

        String myFormat = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        startDate.setText(sdf.format(myCalendarS.getTime()));
    }

    private void updateLabelEnd() {

        String myFormat = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        endDate.setText(sdf.format(myCalendarE.getTime()));
    }

    public static String getCurrentTimeStamp() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        String strDate = sdf.format(now);
        return strDate;
    }

    private class CustomAdapterSub extends ArrayAdapter<HashMap<String, Object>> {

        public CustomAdapterSub(Context context, int textViewResourceId, ArrayList<HashMap<String, Object>> Strings) {

            //let android do the initializing :)
            super(context, textViewResourceId, Strings);
        }

        //class for caching the views in a row
        private class ViewHolder {

            TextView comments, desc, priority, startdate, enddate, jobOrder, statusId, id, subId, createdBy, subName, isSub, detailsId, assignedBy;
            CardView cv;
        }

        //Initialise
        ViewHolder viewHolder;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                //inflate the custom layout
                convertView = inflater.from(parent.getContext()).inflate(R.layout.sublist, parent, false);
                viewHolder = new ViewHolder();

                //cache the views
                viewHolder.assignedBy = (TextView) convertView.findViewById(R.id.assignedBy);
                viewHolder.detailsId = (TextView) convertView.findViewById(R.id.details_id);
                viewHolder.subName = (TextView) convertView.findViewById(R.id.subName);
                viewHolder.createdBy = (TextView) convertView.findViewById(R.id.createdBy);
                viewHolder.subId = (TextView) convertView.findViewById(R.id.subtask_id);
                viewHolder.comments = (TextView) convertView.findViewById(R.id.comments);
                viewHolder.desc = (TextView) convertView.findViewById(R.id.desc);
                viewHolder.isSub = (TextView) convertView.findViewById(R.id.isSub);
                viewHolder.priority = (TextView) convertView.findViewById(R.id.priority);
                viewHolder.startdate = (TextView) convertView.findViewById(R.id.start);
                viewHolder.enddate = (TextView) convertView.findViewById(R.id.end);
                viewHolder.jobOrder = (TextView) convertView.findViewById(R.id.jobOrder);
                viewHolder.statusId = (TextView) convertView.findViewById(R.id.statusId);
                viewHolder.id = (TextView) convertView.findViewById(R.id.task_id);

                //link the cached views to the convertview
                convertView.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) convertView.getTag();

            //set the data to be displayed
            viewHolder.assignedBy.setText(dataList.get(position).get("AssignedById").toString());
            viewHolder.detailsId.setText(dataList.get(position).get("TaskDetailsId").toString());
            viewHolder.subName.setText(dataList.get(position).get("TaskName").toString());
            viewHolder.createdBy.setText(dataList.get(position).get("CreatedBy").toString());
            viewHolder.comments.setText(dataList.get(position).get("Comments").toString());
            viewHolder.desc.setText(dataList.get(position).get("TaskDescription").toString());
//            viewHolder.priority.setText(dataList.get(position).get("Priority").toString());
//            viewHolder.startdate.setText(dataList.get(position).get("TaskStartDate").toString());
//            viewHolder.enddate.setText(dataList.get(position).get("TaskEndDate").toString());
//            viewHolder.jobOrder.setText(dataList.get(position).get("JobOrder").toString());
            viewHolder.isSub.setText(dataList.get(position).get("IsSub").toString());
            viewHolder.statusId.setText(dataList.get(position).get("StatusId").toString());
            viewHolder.id.setText(dataList.get(position).get("TaskId").toString());
//            viewHolder.subId.setText(dataList.get(position).get("SubTaskId").toString());
            return convertView;
        }
    }

    private class GetTaskListSub extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dataList.clear();
            // Showing progress dialog
            pDialog = new ProgressDialog(InspectorActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {


            String selection = arg0[0];

            HttpPost request = new HttpPost(getString(R.string.url) + "EagleXpetizeService.svc/TaskAssigned");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            if (selection.equals("Approve")) {

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
                            .key("AssignedById").value(insp_id)
                            .key("IsSubTask").value(1)
                            .key("StatusId").value(7)
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
            } else {

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
                            .key("AssignedById").value(insp_id)
                            .key("IsSubTask").value(1)
                            .key("StatusId").value(6)
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
            }

            // Send request to WCF service
            DefaultHttpClient httpClient = new DefaultHttpClient();
            try {
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String response = httpClient.execute(request, responseHandler);
                Log.d("res", response);

                if (response != null) {

                    try {

                        JSONObject json1 = new JSONObject(response);
                        tasks = json1.getJSONArray("TaskAssignedResult");

                        // Looping through Array
                        for (int i = 0; i < tasks.length(); i++) {
                            JSONObject c = tasks.getJSONObject(i);

                            String id = c.getString("TaskId");
                            String detailsId = c.getString("TaskDetailsId");
                            String name = c.getString("TaskName");
                            String desc = c.getString("TaskDescription");
                            String comments = c.getString("Comments");
                            String isSub = c.getString("IsSubTask");
//                            String priority = c.getString("Priority");
                            String createdBy = c.getString("CreatedBy");
                            String assignedBy = c.getString("AssignedById");
                            int statusId = c.getInt("StatusId");
//                            int subId = c.getInt("SubTaskId");

                            //adding each child node to HashMap key => value
                            HashMap<String, Object> taskMap = new HashMap<String, Object>();
                            taskMap.put("TaskDescription", desc);
                            taskMap.put("TaskDetailsId", detailsId);
                            taskMap.put("AssignedById", assignedBy);
                            taskMap.put("CreatedBy", createdBy);
                            taskMap.put("TaskId", id);
                            taskMap.put("TaskName", name);
                            taskMap.put("IsSub", isSub);
//                            taskMap.put("SubTaskId", subId);
                            taskMap.put("StatusId", statusId);
                            taskMap.put("Comments", comments);
//                            contact.put("Priority", "Priority : " + priority);

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

            return selection;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            CustomAdapterSub cardAdapter = new CustomAdapterSub(InspectorActivity.this, R.layout.sublist, dataList);
            added_list.setAdapter(cardAdapter);

        }
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.empty);
        ListView list = (ListView) findViewById(R.id.listView_task);
        list.setEmptyView(empty);
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

                Intent i = new Intent(InspectorActivity.this, NotificationActivity.class);
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

        Intent i = new Intent(InspectorActivity.this, InspectorActivity.class);
        startActivity(i);
    }
}
