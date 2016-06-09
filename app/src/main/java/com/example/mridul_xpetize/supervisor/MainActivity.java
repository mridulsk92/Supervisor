package com.example.mridul_xpetize.supervisor;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialize.color.Material;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    private Drawer result = null;
    ProgressDialog pDialog;
    private static String TAG_INSPECTOR = "insp";
    ArrayList<HashMap<String, String>> dataList;
    ListView inspector_list;
    AutoCompleteTextView autoText;
    JSONArray tasks;
    private static String TAG_NAME = "Name";
    private static String TAG_ID = "Id";
    Button add;
    String del_id;
    PreferencesHelper pref;

    List<String> dbListName = new ArrayList<String>();
    List<String> dbListId = new ArrayList<String>();
    List<String> savedList = new ArrayList<String>();
    List<String> savedListId = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialise toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        pref = new PreferencesHelper(MainActivity.this);
        String name = pref.GetPreferences("Name");

        //Side Drawer
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName(name).withEmail(name+"@gmail.com").withIcon(getResources().getDrawable(R.drawable.profile))
                ).build();

        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .withSelectedItem(-1)
                .withDisplayBelowStatusBar(true)
                .addDrawerItems(
//                        new PrimaryDrawerItem().withName("Filter").withIcon(getResources().getDrawable(R.drawable.ic_filter)).withIdentifier(1).withSelectable(false),
                        new SecondaryDrawerItem().withName("About").withIcon(getResources().getDrawable(R.drawable.ic_about)).withSelectable(false),
                        new SecondaryDrawerItem().withName("Log Out").withIcon(getResources().getDrawable(R.drawable.ic_logout)).withSelectable(false)
                ).build();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        //Initialise Views
        add = (Button) findViewById(R.id.button_add);
        inspector_list = (ListView) findViewById(R.id.inspector_list);
        dataList = new ArrayList<HashMap<String, String>>();
        pref = new PreferencesHelper(MainActivity.this);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, TestActivity.class);
                startActivity(i);
            }
        });

//        if (isNetworkAvailable() && !savedList.isEmpty()) {
//            new GetInspectorList().execute();
//        } else {
//            GetSavedInspectorList();
//        }
        new GetInspectorList().execute();

        //long click on list item
        inspector_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                TextView ins_id = (TextView) view.findViewById(R.id.inspector_id);
                del_id = ins_id.getText().toString();
                Log.d("test",del_id);
                ShowDialog();

                return true;
            }
        });

        //onItem click listener for list items
        inspector_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String name = ((TextView) view.findViewById(R.id.inspector)).getText().toString();
                String id_insp = ((TextView)view.findViewById(R.id.inspector_id)).getText().toString();

                Intent i = new Intent(MainActivity.this, InspectorActivity.class);
                i.putExtra("name", name);
                i.putExtra("Id",id_insp);
                startActivity(i);
            }
        });
    }

    private void ShowDialog() {

        LayoutInflater factory = LayoutInflater.from(this);
        final View addView = factory.inflate(
                R.layout.layout_dialog, null);
        final AlertDialog addDialog = new AlertDialog.Builder(this).create();
        addDialog.setView(addView);

        //Initialise
        TextView delete = (TextView) addView.findViewById(R.id.textView_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DeleteUser().execute();
                addDialog.dismiss();
            }
        });
        addDialog.show();
    }

    private class DeleteUser extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

//            String url = "http://vikray.in/MyService.asmx/GetEmployessJSONNewN";
            String url = getString(R.string.url)+"MyService.asmx/DeleteUserDetails?id=" + del_id;
            // Making a request to url and getting response

            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            Log.d("Response: ", "> " + jsonStr);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            new GetInspectorList().execute();

        }
    }

    private void GetSavedInspectorList() {

        TinyDB tiny = new TinyDB(MainActivity.this);
        savedList = tiny.getListString("Names");

        for (int i = 0; i < savedList.size(); i++) {

            String name = savedList.get(i);
            String id = savedListId.get(i);
            HashMap<String, String> contact = new HashMap<String, String>();

            // adding each child node to HashMap key => value
            contact.put(TAG_INSPECTOR, name);
            contact.put(TAG_ID,id);
            dataList.add(contact);
        }

        ListAdapter adapter = new SimpleAdapter(
                MainActivity.this, dataList,
                R.layout.layout_inspector, new String[]{TAG_INSPECTOR,TAG_ID}, new int[]{R.id.inspector,R.id.inspector_id
        });

        inspector_list.setAdapter(adapter);

    }

    //AsyncTask to get rejected tasks(to be edited)
    private class GetInspectorList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            dataList.clear();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            String url = getString(R.string.url)+"MyService.asmx/ExcProcedure?Para=Proc_GetUserMst&Para=2";
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {

                try {

                    tasks = new JSONArray(jsonStr);
                    // looping through All Contacts
                    for (int i = 0; i < tasks.length(); i++) {
                        JSONObject c = tasks.getJSONObject(i);

                        String id = c.getString(TAG_ID);
                        String name = c.getString(TAG_NAME);

                        dbListName.add(name);
                        dbListId.add(id);

                        // tmp hashmap for single contact
                        HashMap<String, String> contact = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        contact.put(TAG_INSPECTOR, name);
                        contact.put(TAG_ID, id);
                        dataList.add(contact);

                    }

                    TinyDB tiny = new TinyDB(MainActivity.this);
                    tiny.putListString("Names", (ArrayList<String>) dbListName);

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

            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, dataList,
                    R.layout.layout_inspector, new String[]{TAG_INSPECTOR, TAG_ID}, new int[]{R.id.inspector, R.id.inspector_id
            });

            inspector_list.setAdapter(adapter);
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
//        MenuItem item = menu.findItem(R.id.action_notifications);
////        LayerDrawable icon = (LayerDrawable) item.getIcon();
//
//        // Update LayerDrawable's BadgeDrawable
////        Utils2.setBadgeCount(this, icon, 2);
//
//        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//
//                Intent i = new Intent(MainActivity.this, NotificationActivity.class);
//                startActivity(i);
//                return false;
//            }
//        });

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!result.isDrawerOpen()) {
                    result.openDrawer();
                } else {
                    result.closeDrawer();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
