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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationActivity extends AppCompatActivity {

    ProgressDialog pDialog;
    private Drawer result = null;

    private static String TAG_TASKID = "TaskId";
    private static String TAG_DESCRIPTION = "Description";
    private static String TAG_STARTDATE = "TaskStartDate";
    private static String TAG_ENDDATE = "TaskEndDate";
    private static String TAG_STATUS = "Status";

    private static String TAG_USERNAME = "Username";
    private static String TAG_ID = "Id";
    JSONArray tasks;

    private static String TAG_INSPECTOR = "insp";
    ArrayList<HashMap<String, String>> dataList;
    ListView inspector_list;
    PreferencesHelper pref;
    String statusString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        //Initialise toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Supervisor");

        pref = new PreferencesHelper(NotificationActivity.this);
        String acc_name = pref.GetPreferences("Name");

        //Side Drawer
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName(acc_name).withEmail(acc_name+"@gmail.com").withIcon(getResources().getDrawable(R.drawable.profile))
                ).build();

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withTranslucentStatusBar(false)
                .withDisplayBelowStatusBar(true)
                .addDrawerItems(
                        new SecondaryDrawerItem().withName("About").withIcon(getResources().getDrawable(R.drawable.ic_about)).withSelectable(false),
                        new SecondaryDrawerItem().withName("Log Out").withIcon(getResources().getDrawable(R.drawable.ic_logout)).withSelectable(false)
                ).build();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        //Initialise Views
        inspector_list = (ListView) findViewById(R.id.inspector_list);
        dataList = new ArrayList<HashMap<String, String>>();


    }

    @Override
    protected void onPause() {
        super.onPause();
        super.finish();
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

                return false;
            }
        });

        return true;
    }
}
