package com.example.mridul_xpetize.supervisor;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    String desc, stdate, enddate, worker_id, comments_st, order_st, name_st;
    int priority;

    Calendar myCalendarS, myCalendarE;
    EditText startDate, endDate;
    LayoutInflater inflater;
    CustomAdapter cardAdapter;
    EditText task_select;

    ListView added_list;
    JSONArray tasks;
    String selected_task, selected_task_id;

    List<String> popupList = new ArrayList<String>();
    List<String> popupListId = new ArrayList<String>();

    ProgressDialog pDialog;

    ArrayList<HashMap<String, Object>> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Inspector");

        pref = new PreferencesHelper(WorkerActivity.this);
        String uname = pref.GetPreferences("UserName");

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
                        new SecondaryDrawerItem().withName("About").withIcon(getResources().getDrawable(R.drawable.ic_about)).withSelectable(false),
                        new SecondaryDrawerItem().withName("Log Out").withIcon(getResources().getDrawable(R.drawable.ic_logout)).withSelectable(false)
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (drawerItem != null) {

                        }
                        return false;
                    }
                }).build();

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
        empty = findViewById(R.id.empty);
        workerName = (TextView) findViewById(R.id.textView_inspector);
        task_list = (ListView) findViewById(R.id.listView_task);
        task_list.setEmptyView(findViewById(android.R.id.empty));
        dataList = new ArrayList<>();
        empty = (TextView) findViewById(R.id.empty);
        added_list = (ListView) findViewById(R.id.listView_task);
        workerName.setText(name);

        //Show new Subtask List
        new GetSubTaskList().execute("User");

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
            String userId_st = passed.get(1);
            String createdBy_st = passed.get(2);
            String status_st = passed.get(3);
            String comments_st = passed.get(4);
            String insp_id = pref.GetPreferences("UserId");

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
                        .key("AssignedToId").value(userId_st)
                        .key("AssignedById").value(insp_id)
                        .key("StatusId").value(status_st)
                        .key("IsSubTask").value(1)
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

            new GetSubTaskList().execute("User");

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

            String insp_id = pref.GetPreferences("UserId");

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
            new GetSubTaskList().execute("User");

        }
    }

    private class CustomAdapter extends ArrayAdapter<HashMap<String, Object>> {

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<HashMap<String, Object>> Strings) {

            //let android do the initializing :)
            super(context, textViewResourceId, Strings);
        }

        //class for caching the views in a row
        private class ViewHolder {

            TextView comments, desc, priority, startdate, enddate, jobOrder, statusId, id, subId, createdBy, subName, isSub;
            CardView cv;
        }

        //Initialise
        ViewHolder viewHolder;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                //inflate the custom layout
                convertView = inflater.from(parent.getContext()).inflate(R.layout.task_list_new, parent, false);
                viewHolder = new ViewHolder();

                //cache the views
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
//            viewHolder.desc.setText(dataList.get(position).get("Description").toString());
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

                            // Looping through Array
                            for (int i = 0; i < tasks.length(); i++) {
                                JSONObject c = tasks.getJSONObject(i);

                                String id = c.getString("TaskId");
                                String name = c.getString("TaskName");
//                            String desc = c.getString("Description");
                                String comments = c.getString("Comments");
                                String isSub = c.getString("IsSubTask");
//                            String priority = c.getString("Priority");
                                String createdBy = c.getString("CreatedBy");
                                int statusId = c.getInt("StatusId");
//                            int subId = c.getInt("SubTaskId");

                                //adding each child node to HashMap key => value
                                HashMap<String, Object> taskMap = new HashMap<String, Object>();
//                            taskMap.put("Description", "Description : " + desc);
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
//                        String statusId = dataList.get(which).get("StatusId").toString();
                        String comments = dataList.get(which).get("Comments").toString();
                        String createdBy = dataList.get(which).get("CreatedBy").toString();
                        ArrayList<String> passing = new ArrayList<String>();
                        passing.add(id);
                        passing.add(worker_id);
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

                Intent i = new Intent(WorkerActivity.this, NotificationActivity.class);
                startActivity(i);
                return false;
            }
        });

        return true;
    }

}
