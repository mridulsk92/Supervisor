package com.example.mridul_xpetize.supervisor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.LinearLayout;
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

public class WorkerActivity extends AppCompatActivity {

    private Drawer result = null;
    ListView task_list;
    TextView workerName;
    ImageButton startCal, endCal;
    PreferencesHelper pref;
    View empty;
    MenuItem menuItem;
    int count, response_json;
    ListView hidden_not;
    List<Integer> posList = new ArrayList<Integer>();
    ArrayList<Integer> savedList = new ArrayList<Integer>();

    String desc, stdate, enddate, worker_id, comments_st, order_st, name_st, taskId, byId;
    String taskId_history, createdBy_history;
    String insp_id;
    int priority;

    Calendar myCalendarS, myCalendarE;
    EditText startDate, endDate;
    LayoutInflater inflater;
    CustomAdapter cardAdapter;
    EditText task_select;
    String current_time, new_subTaskId;

    ListView added_list;
    JSONArray tasks;
    String selected_task, selected_task_id;

    List<String> popupList = new ArrayList<String>();
    List<String> popupListId = new ArrayList<String>();

    ProgressDialog pDialog;
    ProgressDialog pDialogN;

    ArrayList<HashMap<String, Object>> dataList;
    ArrayList<HashMap<String, Object>> notiList;
    SharedPreferences prefNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Inspector");

        prefNew = getSharedPreferences("LangPref", Activity.MODE_PRIVATE);
        pref = new PreferencesHelper(WorkerActivity.this);
        String uname = pref.GetPreferences("UserName");
        insp_id = pref.GetPreferences("UserId");


        //Side Drawer Header
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName(uname).withEmail(uname + "@gmail.com").withIcon(getResources().getDrawable(R.drawable.profile))
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(WorkerActivity.this);
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

        //ToggleButton on Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        //onClick of Floating Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Show DialogBox
                final android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(WorkerActivity.this);
                alertDialogBuilder.setTitle("Select");
                final CharSequence items[] = {"Select Pre Defined SubTasks", "Create SubTask"};
                alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                            //Select Pre defined tasks
                            new GetSubTaskList().execute("All");
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

        Intent i = getIntent();
        String name = i.getStringExtra("name");
        worker_id = i.getStringExtra("id");

        //Initialize
        current_time = getCurrentTimeStamp();
        hidden_not = (ListView) findViewById(R.id.listView_hidden_notification);
        empty = findViewById(R.id.empty);
        workerName = (TextView) findViewById(R.id.textView_inspector);
        task_list = (ListView) findViewById(R.id.listView_task);
        task_list.setEmptyView(findViewById(android.R.id.empty));
        dataList = new ArrayList<>();
        notiList = new ArrayList<>();
        empty = (TextView) findViewById(R.id.empty);
        added_list = (ListView) findViewById(R.id.listView_task);
        workerName.setText(name);

        //Get Saved List
//        TinyDB db = new TinyDB(WorkerActivity.this);
//        savedList = db.getListInt("Positions");

        //onItemClick of ListView item
        hidden_not.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                count--;
                if (count <= 0) {
                    count = 0;
                }

                menuItem.setIcon(buildCounterDrawable(count, R.drawable.blue_bell_small));

                parent.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
//                String desc = ((TextView) view.findViewById(R.id.textview_noti)).getText().toString();
//                String byId = ((TextView) view.findViewById(R.id.noti_by)).getText().toString();
                Intent i = new Intent(WorkerActivity.this, NotificationActivity.class);
                i.putExtra("Description", desc);
                i.putExtra("ById", byId);
                startActivity(i);
            }
        });

        //onItemClick of ListView item
        task_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String sub_id = ((TextView) view.findViewById(R.id.task_id)).getText().toString();
                String name_st = ((TextView) view.findViewById(R.id.subName)).getText().toString();
                String comments_st = ((TextView) view.findViewById(R.id.comments)).getText().toString();
                String desc_st = ((TextView) view.findViewById(R.id.desc)).getText().toString();
                String status_st = ((TextView) view.findViewById(R.id.status)).getText().toString();
                String assigned_st = ((TextView) view.findViewById(R.id.assigned)).getText().toString();


                Intent i = new Intent(WorkerActivity.this, SubTaskDetailsActivity.class);
                i.putExtra("SubName", name_st);
                i.putExtra("SubId", sub_id);
                Log.d("SubId", sub_id);
                i.putExtra("Comments", comments_st);
                i.putExtra("Desc", desc_st);
                i.putExtra("Status", status_st);
                i.putExtra("Assigned", assigned_st);
                startActivity(i);


//                LayoutInflater factory = LayoutInflater.from(WorkerActivity.this);
//                final View addView = factory.inflate(
//                        R.layout.dialog_taskdetails, null);
//                final AlertDialog detailDialog = new AlertDialog.Builder(WorkerActivity.this).create();
//                detailDialog.setView(addView);
//
//                //Initialise
//                TextView subName = (TextView) addView.findViewById(R.id.view_subName);
//                TextView desc = (TextView) addView.findViewById(R.id.view_description);
//                TextView comments = (TextView) addView.findViewById(R.id.view_comments);
//                TextView status = (TextView) addView.findViewById(R.id.view_status);
//                TextView assigned = (TextView) addView.findViewById(R.id.view_assigned);
//                ImageButton close = (ImageButton) addView.findViewById(R.id.imageButton_close);
//
//                //SetTextValues
//                subName.setText(name_st);
//                desc.setText("Description : " + desc_st);
//                comments.setText("Comments : " + comments_st);
//                status.setText("Status : " + status_st);
//                assigned.setText("Assigned By : " + assigned_st);
//
//                //ok button onClick
//                close.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        detailDialog.dismiss();
//                    }
//                });
//
//                detailDialog.show();
            }
        });

        //Show new Subtask List
        new GetSubTaskList().execute("User");

//        new GetNotiList().execute();

    }

    private void AddTask() {

        LayoutInflater factory = LayoutInflater.from(WorkerActivity.this);
        final View addView = factory.inflate(
                R.layout.addtask_dialog_new, null);
        final AlertDialog addDialog = new AlertDialog.Builder(WorkerActivity.this).create();
        addDialog.setView(addView);

        //Initialise
        startCal = (ImageButton) addView.findViewById(R.id.imageButton_startDate);
        endCal = (ImageButton) addView.findViewById(R.id.imageButton_endDate);
        myCalendarS = Calendar.getInstance();
        myCalendarE = Calendar.getInstance();
        final EditText subName = (EditText) addView.findViewById(R.id.editText_subName);
        final EditText editDescription = (EditText) addView.findViewById(R.id.editText_desc);
        final Spinner typeSpinner = (Spinner) addView.findViewById(R.id.spinner_type);
        startDate = (EditText) addView.findViewById(R.id.editText_start);
        endDate = (EditText) addView.findViewById(R.id.editText_end);
        final EditText comments = (EditText) addView.findViewById(R.id.editText_comments);
        final EditText order = (EditText) addView.findViewById(R.id.editText_order);
        task_select = (EditText) addView.findViewById(R.id.editText_Tasks);

        task_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new LoadTask().execute();
            }
        });

        //Add button onClick
        Button addTask = (Button) addView.findViewById(R.id.button_add);
        addTask.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                name_st = subName.getText().toString();
                order_st = order.getText().toString();
                desc = editDescription.getText().toString();
                stdate = startDate.getText().toString();
                enddate = endDate.getText().toString();
                comments_st = comments.getText().toString();

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
                new PostTasks().execute();

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
                new DatePickerDialog(WorkerActivity.this, dateStart, myCalendarS
                        .get(Calendar.YEAR), myCalendarS.get(Calendar.MONTH),
                        myCalendarS.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        endCal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(WorkerActivity.this, dateEnd, myCalendarE
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
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(WorkerActivity.this, android.R.layout.simple_spinner_item, spinnerData);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        typeSpinner.setAdapter(dataAdapter);

        addDialog.show();

    }

    private class LoadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            dataList.clear();
            popupList.clear();
            popupListId.clear();

            pDialog = new ProgressDialog(WorkerActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            String url = getString(R.string.url) + "EagleXpetizeService.svc/Tasks/0/0/1";
            Log.d("url", url);

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {

                try {

                    tasks = new JSONArray(jsonStr);
                    // looping through All Tasks
                    for (int i = 0; i < tasks.length(); i++) {
                        JSONObject c = tasks.getJSONObject(i);

                        String id = c.getString("TaskId");
                        String name = c.getString("TaskName");

                        //Load Names and Ids in List
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

            CharSequence[] items = popupList.toArray(new CharSequence[popupList.size()]);
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(WorkerActivity.this);
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
                    selected_task = popupList.get(which);
                    selected_task_id = popupListId.get(which);
                    task_select.setText(selected_task);
                }
            });
            builderSingle.show();
        }
    }

//    private class PostHistory extends AsyncTask<String, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            // Showing progress dialog
//            pDialog = new ProgressDialog(WorkerActivity.this);
//            pDialog.setMessage("Please wait...");
//            pDialog.setCancelable(false);
//            pDialog.show();
//        }
//
//        @Override
//        protected Void doInBackground(String... params) {
//
//            String historyDate = getCurrentTimeStamp();
//            String status = params[0];
//            String user_name = pref.GetPreferences("UserName");
//            String createdbyId = pref.GetPreferences("UserId");
//
//            HttpPost request = new HttpPost(getString(R.string.url) + "EagleXpetizeService.svc/NewHistory");
//            request.setHeader("Accept", "application/json");
//            request.setHeader("Content-type", "application/json");
//
//            JSONStringer userJson = null;
//
//            if (status.equals("Assigned")) {
//                // Build JSON string
//                try {
//                    userJson = new JSONStringer()
//                            .object()
//                            .key("history")
//                            .object()
//                            .key("TaskId").value(taskId_history)
//                            .key("IsSubTask").value(1)
//                            .key("Notes").value("Assigned By : " + user_name)
//                            .key("Comments").value(status)
////                        .key("HistoryDate").value(historyDate)
////                        .key("CreatedDate").value(createdDate)
//                            .key("CreatedBy").value(insp_id)
//                            .endObject()
//                            .endObject();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                // Build JSON string
//                try {
//                    userJson = new JSONStringer()
//                            .object()
//                            .key("history")
//                            .object()
//                            .key("TaskId").value(new_subTaskId)
//                            .key("IsSubTask").value(1)
//                            .key("Notes").value("Created By : " + user_name)
//                            .key("Comments").value(status)
////                        .key("HistoryDate").value(historyDate)
////                        .key("CreatedDate").value(createdDate)
//                            .key("CreatedBy").value(insp_id)
//                            .endObject()
//                            .endObject();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            Log.d("Json", String.valueOf(userJson));
//
//            StringEntity entity = null;
//            try {
//                entity = new StringEntity(userJson.toString(), "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//
//            entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//            entity.setContentType("application/json");
//
//            request.setEntity(entity);
//
//            // Send request to WCF service
//            DefaultHttpClient httpClient = new DefaultHttpClient();
//            try {
//                ResponseHandler<String> responseHandler = new BasicResponseHandler();
//                String response = httpClient.execute(request, responseHandler);
//
//                Log.d("res", response);
//
//                if (response != null) {
//
//                    try {
//
//                        //Get Data from Json
//                        JSONObject jsonObject = new JSONObject(response);
//
//                        String message = jsonObject.getString("NewHistoryResult");
//
//                        //Save userid and username if success
//                        if (message.equals("success")) {
//                            response_json = 200;
//                        } else {
//                            response_json = 201;
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//            // Dismiss the progress dialog
//            if (pDialog.isShowing())
//                pDialog.dismiss();
//
//            if (response_json == 200) {
//                Toast.makeText(WorkerActivity.this, "Success", Toast.LENGTH_SHORT).show();
//                new GetSubTaskList().execute("User");
//            } else {
//                Toast.makeText(WorkerActivity.this, "Failed", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    private class AssignTask extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(WorkerActivity.this);
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
            String insp_id = pref.GetPreferences("UserId");
            taskId_history = taskid_st;
            createdBy_history = createdBy_st;

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
                        .key("taskDetails")
                        .object()
                        .key("TaskId").value(5555)
                        .key("AssignedToId").value(userId_st)
                        .key("AssignedById").value(insp_id)
                        .key("AssignedDateStr").value(current_time)
                        .key("StatusId").value(status_st)
                        .key("IsSubTask").value(1)
                        .key("Comments").value(getString(R.string.url))
                        .key("CreatedBy").value(createdBy_st)
                        .endObject()
                        .endObject();
            } catch (JSONException e) {
                e.printStackTrace();
            }

//            try {
//                userJson = new JSONStringer()
//                        .object()
//                        .key("user")
//                        .object()
//                        .key("UserId").value(userid)
//                        .key("token").value(token)
//                        .endObject()
//                        .endObject();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

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

//            new PostHistory().execute("Assigned");

            new PostNotification().execute("Assigned");

        }
    }

    private class PostNotification extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Showing progress dialog
            pDialog = new ProgressDialog(WorkerActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            String username = pref.GetPreferences("UserName");
            String status = params[0];
            String noti_message = username + " has " + status + " the Task : " + name_st;

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
                Toast.makeText(WorkerActivity.this, "Success", Toast.LENGTH_SHORT).show();
                new GetSubTaskList().execute("User");
            } else {
                Toast.makeText(WorkerActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                new GetSubTaskList().execute("User");
            }
        }
    }

    private class PostTasks extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(WorkerActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpPost request = new HttpPost(getString(R.string.url) + "EagleXpetizeService.svc/NewSubTask");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            // Build JSON string
            JSONStringer userJson = null;
            try {
                userJson = new JSONStringer()
                        .object()
                        .key("subTask")
                        .object()
                        .key("TaskId").value(selected_task_id)
                        .key("SubTaskName").value(name_st)
                        .key("Description").value(desc)
                        .key("CreatedDateStr").value(current_time)
                        .key("ModifiedDateStr").value(current_time)
                        .key("TaskOrder").value(order_st)
                        .key("StatusId").value("1")
                        .key("PriorityId").value(priority)
                        .key("Comments").value(comments_st)
                        .key("CreatedBy").value(insp_id)
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
                        new_subTaskId = jsonObject.getString("NewSubTaskResult");

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

            //Show new Subtask List
//            new PostHistory().execute("Created");
        }
    }

    private class CustomAdapter extends ArrayAdapter<HashMap<String, Object>> {

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<HashMap<String, Object>> Strings) {

            //let android do the initializing :)
            super(context, textViewResourceId, Strings);
        }

        //class for caching the views in a row
        private class ViewHolder {

            TextView comments, desc, priority, startdate, enddate, jobOrder, statusId, id, subId, createdBy, subName, isSub, status, assignedBy;
            CardView cv;
        }

        //Initialise
        ViewHolder viewHolder;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                //inflate the custom layout
                convertView = inflater.from(parent.getContext()).inflate(R.layout.task_list, parent, false);
                viewHolder = new ViewHolder();

                //cache the views
                viewHolder.status = (TextView) convertView.findViewById(R.id.status);
                viewHolder.assignedBy = (TextView) convertView.findViewById(R.id.assigned);
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
            viewHolder.subName.setText(dataList.get(position).get("TaskName").toString());
            viewHolder.createdBy.setText(dataList.get(position).get("CreatedBy").toString());
            viewHolder.comments.setText(dataList.get(position).get("Comments").toString());
            viewHolder.desc.setText(dataList.get(position).get("TaskDescription").toString());
            viewHolder.status.setText(dataList.get(position).get("Status").toString());
            viewHolder.assignedBy.setText(dataList.get(position).get("AssignedByName").toString());
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

    private class GetSubTaskList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dataList.clear();
            popupList.clear();
            empty.setVisibility(View.GONE);
            // Showing progress dialog
            pDialog = new ProgressDialog(WorkerActivity.this);
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
                            .key("AssignedToId").value(worker_id)
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
                            tasks = json1.getJSONArray("TaskAssignedResult");

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

                        tasks = new JSONArray(jsonStr);

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
                cardAdapter = new CustomAdapter(WorkerActivity.this, R.layout.task_list, dataList);
                added_list.setAdapter(cardAdapter);
            } else {
                CharSequence[] items = popupList.toArray(new CharSequence[popupList.size()]);
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(WorkerActivity.this);
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
                        String id = dataList.get(which).get("SubTaskId").toString();
                        name_st = dataList.get(which).get("SubTaskName").toString();
                        String comments = dataList.get(which).get("Comments").toString();
                        String createdBy = dataList.get(which).get("CreatedBy").toString();
                        ArrayList<String> passing = new ArrayList<String>();
                        passing.add(id);
                        passing.add(worker_id);
                        passing.add(createdBy);
                        passing.add("1");
                        passing.add(comments);
                        passing.add(createdBy);
                        if (isNetworkAvailable()) {
                            new AssignTask().execute(passing);
                        } else {
                            Toast.makeText(WorkerActivity.this, "No Internet Connection. Data stored locally", Toast.LENGTH_SHORT).show();
//                            SQLite entry = new SQLite(WorkerActivity.this);
//                            entry.open();
//                            entry.createEntryAssigned(id, worker_id, createdBy, "1", "1", comments, createdBy);
//                            String a = entry.getCountAssigned();
//                            entry.close();
//                            Log.d("Assigned Count :", a);
                        }
                    }
                });
                builderSingle.show();
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        empty = findViewById(R.id.empty);
        ListView list = (ListView) findViewById(R.id.listView_task);
        list.setEmptyView(empty);
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
            viewHolder.rowId.setText(notiList.get(position).get("RowId").toString());
            viewHolder.read.setText(notiList.get(position).get("Read").toString());
            viewHolder.intent.setText(notiList.get(position).get("Intent").toString());
            viewHolder.desc.setText(notiList.get(position).get("Description").toString());
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

    private class GetNotiList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dataList.clear();
            // Showing progress dialog
            pDialogN = new ProgressDialog(WorkerActivity.this);
            pDialogN.setMessage("Please wait...");
            pDialogN.setCancelable(false);
            pDialogN.show();
        }

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

                    JSONArray tasks = new JSONArray(jsonStr);

                    for (int i = 0; i < tasks.length(); i++) {
                        JSONObject c = tasks.getJSONObject(i);

                        String id = c.getString("TaskId");
                        String username = c.getString("UserName");
                        String taskName = c.getString("TaskName");
                        String description = c.getString("Description");
                        String byId = c.getString("ById");
                        String toId = c.getString("ToId");
                        String isNew = c.getString("IsNew");

                        // adding each child node to HashMap key => value
                        HashMap<String, Object> taskMap = new HashMap<String, Object>();
                        taskMap.put("TaskId", id);
                        taskMap.put("UserName", username);
                        taskMap.put("TaskName", taskName);
                        taskMap.put("Description", description);
                        taskMap.put("ById", byId);
                        taskMap.put("ToId", toId);
                        taskMap.put("IsNew", isNew);
                        notiList.add(taskMap);
                        popupList.add(description);

                    }
                    count = notiList.size();

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

            if (pDialogN.isShowing())
                pDialogN.dismiss();

            // initialize pop up window
            menuItem.setIcon(buildCounterDrawable(count, R.drawable.blue_bell_small));
            CustomAdapterNot notAdapter = new CustomAdapterNot(WorkerActivity.this, R.layout.popup_layout, notiList);
            hidden_not.setAdapter(notAdapter);
        }
    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        String strDate = sdf.format(now);
        return strDate;
    }

    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        //inflate menu
        getMenuInflater().inflate(R.menu.menu_my, menu);

        menuItem = menu.findItem(R.id.testAction);
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

//        MenuItem item = menu.findItem(R.id.badge);
//        MenuItemCompat.setActionView(item, R.layout.feed_update_count);
//        View view = MenuItemCompat.getActionView(item);
//        notifCount = (Button)view.findViewById(R.id.notif_count);
//        notifCount.setText(String.valueOf(mNotifCount));
//
//        // Get the notifications MenuItem and LayerDrawable (layer-list)
////        MenuItem item_noti = menu.findItem(R.id.action_noti);
//        MenuItem item_logOut = menu.findItem(R.id.action_logOut);
//
//        item_logOut.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//
//
//                return false;
//            }
//        });
//
////        item_noti.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
////            @Override
////            public boolean onMenuItemClick(MenuItem item) {
////
////                Intent i = new Intent(DashboardActivity.this, NotificationActivity.class);
////                startActivity(i);
////                return false;
////            }
////        });

        return true;
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

        Intent i = new Intent(WorkerActivity.this, WorkerActivity.class);
        startActivity(i);
    }

}
