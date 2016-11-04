package com.example.mridul_xpetize.supervisor;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

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

public class CreateSubTaskActivity extends AppCompatActivity {

    Button addAttachmentDetails;
    ImageButton startCal, endCal;
    EditText startDate, endDate, task_select;
    Calendar myCalendarS, myCalendarE;
    List<String> popupList = new ArrayList<String>();
    List<String> popupListId = new ArrayList<String>();
    String desc, stdate, enddate, worker_id, comments_st, order_st, name_st, new_subTaskId;
    int priority;
    String selected_task, selected_task_id, taskId;
    PreferencesHelper pref;


    ProgressDialog pDialog;
    ArrayList<HashMap<String, Object>> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sub_task);

        //Initialise
        dataList = new ArrayList<>();
        pref = new PreferencesHelper(CreateSubTaskActivity.this);
        startCal = (ImageButton) findViewById(R.id.imageButton_startDate);
        endCal = (ImageButton) findViewById(R.id.imageButton_endDate);
        myCalendarS = Calendar.getInstance();
        myCalendarE = Calendar.getInstance();
        final EditText subName = (EditText) findViewById(R.id.editText_subName);
        final EditText editDescription = (EditText) findViewById(R.id.editText_desc);
        final Spinner typeSpinner = (Spinner) findViewById(R.id.spinner_type);
        startDate = (EditText) findViewById(R.id.editText_start);
        endDate = (EditText) findViewById(R.id.editText_end);
        final EditText comments = (EditText) findViewById(R.id.editText_comments);
        final EditText order = (EditText) findViewById(R.id.editText_order);
        task_select = (EditText) findViewById(R.id.editText_Tasks);

        task_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new LoadTask().execute();
            }
        });

        //Add button onClick
        Button addTask = (Button) findViewById(R.id.button_add);
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
                new DatePickerDialog(CreateSubTaskActivity.this, dateStart, myCalendarS
                        .get(Calendar.YEAR), myCalendarS.get(Calendar.MONTH),
                        myCalendarS.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        endCal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(CreateSubTaskActivity.this, dateEnd, myCalendarE
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
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(CreateSubTaskActivity.this, android.R.layout.simple_spinner_item, spinnerData);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        typeSpinner.setAdapter(dataAdapter);
    }

    private class LoadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            dataList.clear();
            popupList.clear();
            popupListId.clear();

            pDialog = new ProgressDialog(CreateSubTaskActivity.this);
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

                    JSONArray tasks = new JSONArray(jsonStr);
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
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(CreateSubTaskActivity.this);
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

    private class PostTasks extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(CreateSubTaskActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            String current_time = getCurrentTimeStamp();
            String insp_id = pref.GetPreferences("UserId");

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

            if(!new_subTaskId.isEmpty()){

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(CreateSubTaskActivity.this);
                alertDialog.setMessage("Do you want to add attachment now?");

                // Setting Positive "Yes" Btn
                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                Intent i = new Intent(CreateSubTaskActivity.this, AttachmentDetailsActivity.class);
                                i.putExtra("SubId", new_subTaskId);
                                startActivity(i);
                            }
                        });

                // Setting Negative "NO" Btn
                alertDialog.setNegativeButton("LATER",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                Intent i = new Intent(CreateSubTaskActivity.this, WorkerActivity.class);
                                startActivity(i);
                            }
                        });

                alertDialog.show();
            }else{
                Toast.makeText(CreateSubTaskActivity.this, "Template creation failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        String strDate = sdf.format(now);
        return strDate;
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
}
