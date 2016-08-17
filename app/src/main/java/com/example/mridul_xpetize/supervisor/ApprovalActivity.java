package com.example.mridul_xpetize.supervisor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import android.widget.EditText;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ApprovalActivity extends AppCompatActivity {

    private Drawer result = null;
    PreferencesHelper pref;
    View empty;
    int count;
    MenuItem menuItem;
    ListView hidden_not;

    LayoutInflater inflater;
    CustomAdapter cardAdapter;

    ListView subTask_list;
    String user_id;
    ListView added_list;
    JSONArray tasks;

    List<String> popupList = new ArrayList<String>();
    ProgressDialog pDialog;
    ProgressDialog pDialogN;
    ArrayList<HashMap<String, Object>> dataList;
    ArrayList<HashMap<String, Object>> notiList;
    SharedPreferences prefNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Inspector");

        //Get Preference Values
        prefNew = getSharedPreferences("LangPref", Activity.MODE_PRIVATE);
        pref = new PreferencesHelper(ApprovalActivity.this);
        user_id = pref.GetPreferences("UserId");
        final String acc_name = pref.GetPreferences("UserName");

        //Initialise Views
        hidden_not = (ListView) findViewById(R.id.listView_hidden_notification);
        empty = findViewById(R.id.empty);
        subTask_list = (ListView) findViewById(R.id.listView_subtasks);
        dataList = new ArrayList<HashMap<String, Object>>();
        notiList = new ArrayList<HashMap<String, Object>>();

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
                                AlertDialog.Builder builder = new AlertDialog.Builder(ApprovalActivity.this);
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

        //onItemClick of ListView
        subTask_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String assignedTo_st = ((TextView) view.findViewById(R.id.assignedTo)).getText().toString();
                String id_st = ((TextView) view.findViewById(R.id.task_id)).getText().toString();
                String details_st = ((TextView) view.findViewById(R.id.detailsId)).getText().toString();
                String assignedBy_st = ((TextView) view.findViewById(R.id.assignedId)).getText().toString();
                String creaedBy_st = ((TextView) view.findViewById(R.id.createdBy)).getText().toString();
                String name_st = ((TextView) view.findViewById(R.id.subName)).getText().toString();
                String desc_st = ((TextView) view.findViewById(R.id.desc)).getText().toString();
                String comments_st = ((TextView) view.findViewById(R.id.comments)).getText().toString();
                String startDate_st = ((TextView) view.findViewById(R.id.start)).getText().toString();
                String endDate_st = ((TextView) view.findViewById(R.id.end)).getText().toString();
                String status_st = ((TextView) view.findViewById(R.id.status)).getText().toString();

                Intent i = new Intent(ApprovalActivity.this, ApprovalDetailsActivity.class);
                i.putExtra("Id", id_st);
                i.putExtra("AssignedTo", assignedTo_st);
                i.putExtra("DetailsId", details_st);
                i.putExtra("AssignedById", assignedBy_st);
                i.putExtra("CreatedById", creaedBy_st);
                i.putExtra("Name", name_st);
                i.putExtra("Desc", desc_st);
                i.putExtra("Comments", comments_st);
                i.putExtra("StartDate", startDate_st);
                i.putExtra("EndDate", endDate_st);
                i.putExtra("StatusId", status_st);
                startActivity(i);

            }
        });

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
                String desc = ((TextView) view.findViewById(R.id.textview_noti)).getText().toString();
                String byId = ((TextView) view.findViewById(R.id.noti_by)).getText().toString();
//                Intent i = new Intent(ApprovalActivity.this, NotificationActivity.class);
//                i.putExtra("Description", desc);
//                i.putExtra("ById", byId);
//                startActivity(i);
            }
        });

        new GetSubTaskList().execute();

        new GetNotification().execute();
    }

    private class GetNotification extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Showing progress dialog
            pDialogN = new ProgressDialog(ApprovalActivity.this);
            pDialogN.setMessage("Please wait...");
            pDialogN.setCancelable(false);
            pDialogN.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            String user_id = pref.GetPreferences("UserId");

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            String url = getString(R.string.url) + "EagleXpetizeService.svc/Notifications/" + user_id +"/1";
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
                        taskMap.put("UserName",username);
                        taskMap.put("TaskName",taskName);
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
            CustomAdapterNot notAdapter = new CustomAdapterNot(ApprovalActivity.this, R.layout.popup_layout, notiList);
            hidden_not.setAdapter(notAdapter);
        }
    }

    private class CustomAdapterNot extends ArrayAdapter<HashMap<String, Object>> {

        public CustomAdapterNot(Context context, int textViewResourceId, ArrayList<HashMap<String, Object>> Strings) {

            //let android do the initializing :)
            super(context, textViewResourceId, Strings);
        }

        //class for caching the views in a row
        private class ViewHolder {

            TextView not, isRead, byName, taskName;
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
                viewHolder.taskName = (TextView) convertView.findViewById(R.id.noti_task);
                viewHolder.byName = (TextView) convertView.findViewById(R.id.noti_by);
                viewHolder.noti_linear = (LinearLayout) convertView.findViewById(R.id.not_layout);
                viewHolder.not = (TextView) convertView.findViewById(R.id.textview_noti);
                viewHolder.isRead = (TextView) convertView.findViewById(R.id.textview_isRead);

                //link the cached views to the convertview
                convertView.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) convertView.getTag();

            //set the data to be displayed
            viewHolder.byName.setText(notiList.get(position).get("UserName").toString());
            viewHolder.taskName.setText(notiList.get(position).get("TaskName").toString());
            viewHolder.not.setText(notiList.get(position).get("Description").toString());
            viewHolder.noti_linear.setBackgroundColor(Color.LTGRAY);

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

    private class CustomAdapter extends ArrayAdapter<HashMap<String, Object>> {

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<HashMap<String, Object>> Strings) {

            //let android do the initializing :)
            super(context, textViewResourceId, Strings);
        }

        //class for caching the views in a row
        private class ViewHolder {

            TextView comments, desc, priority, startdate, enddate, jobOrder, statusId, id, subId, createdBy, subName, isSub, status, assignedBy, detailsId, assignedId, assignedTo;
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
                viewHolder.assignedTo = (TextView) convertView.findViewById(R.id.assignedTo);
                viewHolder.assignedId = (TextView) convertView.findViewById(R.id.assignedId);
                viewHolder.detailsId = (TextView) convertView.findViewById(R.id.detailsId);
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
            viewHolder.assignedTo.setText(dataList.get(position).get("AssignedToId").toString());
            viewHolder.assignedId.setText(dataList.get(position).get("AssignedById").toString());
            viewHolder.detailsId.setText(dataList.get(position).get("TaskDetailsId").toString());
            viewHolder.subName.setText(dataList.get(position).get("TaskName").toString());
            viewHolder.createdBy.setText(dataList.get(position).get("CreatedBy").toString());
            viewHolder.comments.setText(dataList.get(position).get("Comments").toString());
            viewHolder.desc.setText(dataList.get(position).get("TaskDescription").toString());
            viewHolder.status.setText(dataList.get(position).get("Status").toString());
            viewHolder.assignedBy.setText(dataList.get(position).get("AssignedByName").toString());
            viewHolder.assignedId.setText(dataList.get(position).get("AssignedById").toString());
            viewHolder.startdate.setText(dataList.get(position).get("StartDate").toString());
            viewHolder.enddate.setText(dataList.get(position).get("EndDate").toString());
            viewHolder.detailsId.setText(dataList.get(position).get("TaskDetailsId").toString());
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
            pDialog = new ProgressDialog(ApprovalActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {

//            String url;
//            String check = arg0[0];

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

                        if (tasks != null) {

                            // Looping through Array
                            for (int i = 0; i < tasks.length(); i++) {
                                JSONObject c = tasks.getJSONObject(i);


                                String id = c.getString("TaskId");
                                String detailsId = c.getString("TaskDetailsId");
                                String assignedId = c.getString("AssignedById");
                                String stDate = c.getString("StartDate");
                                String endDate = c.getString("EndDate");
                                String name = c.getString("TaskName");
                                String assignedTo = c.getString("AssignedToId");
                                String desc = c.getString("TaskDescription");
                                String comments = c.getString("Comments");
                                String isSub = c.getString("IsSubTask");
                                String status = c.getString("Status");
                                String createdBy = c.getString("CreatedBy");
                                int statusId = c.getInt("StatusId");
                                String assignedBy = c.getString("AssignedByName");

                                //adding each child node to HashMap key => value
                                HashMap<String, Object> taskMap = new HashMap<String, Object>();
                                taskMap.put("TaskDetailsId", detailsId);
                                taskMap.put("AssignedToId",assignedTo);
                                taskMap.put("AssignedById", assignedId);
                                taskMap.put("StartDate", stDate);
                                taskMap.put("EndDate", endDate);
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
                        } else {
                            Log.e("ServiceHandler", "Couldn't get any data from the url");
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

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            try {
                cardAdapter = new CustomAdapter(ApprovalActivity.this, R.layout.task_list, dataList);
                subTask_list.setAdapter(cardAdapter);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.empty);
        ListView list = (ListView) findViewById(R.id.listView_subtasks);
        list.setEmptyView(empty);
    }

    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
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

        Intent i = new Intent(ApprovalActivity.this, ApprovalActivity.class);
        startActivity(i);
    }
}