package com.example.mridul_xpetize.supervisor;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ManageTemplateActivity extends AppCompatActivity {

    TextView nameView, commentView, descView;
    ProgressDialog pDialog;
    LayoutInflater inflater;
    ArrayList<HashMap<String, Object>> dataList;
    CustomAdapter checkAdapter;
    List<String> itemListSend = new ArrayList<String>();
    ListView itemList;
    View emptyDialog;
    String sub_id;
    List<String> itemArrayList = new ArrayList<String>();
    private long myDownloadReference;
    LinearLayout recordLayout;
    TextView message_view;
    private DownloadManager dm;
    Button record, play, add;
    byte[] audioByte;
    String base64String;
    PreferencesHelper pref;
    int response_json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_template);

        //Get Intent
        Intent i = getIntent();
        sub_id = i.getStringExtra("Id");
        String name = i.getStringExtra("Name");
        String desc = i.getStringExtra("Description");
        String comments = i.getStringExtra("Comments");

        //Initialise
        dataList = new ArrayList<>();
        pref = new PreferencesHelper(ManageTemplateActivity.this);
        record = (Button) findViewById(R.id.record_btn);
        play = (Button) findViewById(R.id.play_btn);
        add = (Button) findViewById(R.id.send_btn);
        message_view = (TextView) findViewById(R.id.textView_message);
        recordLayout = (LinearLayout) findViewById(R.id.LinearLayout02);
        nameView = (TextView) findViewById(R.id.SubTaskName);
        descView = (TextView) findViewById(R.id.SubDesc);
        commentView = (TextView) findViewById(R.id.comments);

        //Set TextView values
        nameView.setText(name);
        descView.setText(desc);
        commentView.setText(comments);

        addCheckListItems();

        File f = new File(Environment.getExternalStorageDirectory().getPath() + "/WorkerAudio/SubTask" + sub_id + ".mp3");
        if (!f.exists()) {
            downloadAudio();
        }

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (myDownloadReference == reference) {
                    // Do something with downloaded file.
                    Toast.makeText(ManageTemplateActivity.this, "Download Complete", Toast.LENGTH_SHORT).show();
                    message_view.setVisibility(View.GONE);
                    ManageAudio();
                }
            }
        };
        registerReceiver(receiver, filter);

        itemList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final TextView currentItemText = (TextView) view.findViewById(R.id.textView_listItem);
                final android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(ManageTemplateActivity.this);
                final CharSequence items[] = {"Edit", "Delete"};
                alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {

                            //display edit popup
                            final Dialog editDialog = new Dialog(ManageTemplateActivity.this);
                            editDialog.setContentView(R.layout.edit_popup);
                            editDialog.setTitle("Edit CheckList Item");
                            final EditText editText_item = (EditText) editDialog.findViewById(R.id.edit_item);
                            editText_item.setText(currentItemText.getText());
                            Button btnOk = (Button) editDialog.findViewById(R.id.button_edit_ok);

                            btnOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    itemArrayList.set(position, editText_item.getText().toString());
                                    dataList.get(position).put("ItemListString", editText_item.getText().toString());
                                    checkAdapter = new CustomAdapter(ManageTemplateActivity.this, R.layout.checklist, dataList);
                                    itemList.setAdapter(checkAdapter);
                                    checkAdapter.notifyDataSetChanged();
                                    editDialog.cancel();

                                }
                            });

                            editDialog.show();
                        } else {

                            itemArrayList.remove(position);
                            dataList.remove(position);
                            checkAdapter = new CustomAdapter(ManageTemplateActivity.this, R.layout.checklist, dataList);
                            itemList.setAdapter(checkAdapter);
                            checkAdapter.notifyDataSetChanged();

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

                return false;
            }
        });
    }

    private void ManageAudio() {
        recordLayout.setVisibility(View.VISIBLE);
        record.setEnabled(true);
        play.setEnabled(true);
        add.setEnabled(false);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recordAudio("SubTask" + sub_id + ".mp3");
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/InspectorAudio/SubTask" + sub_id + ".mp3");
                intent.setDataAndType(Uri.fromFile(file), "audio/*");
                startActivity(intent);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/InspectorAudio/SubTask" + sub_id + ".mp3");
                try {
                    audioByte = FileUtils.readFileToByteArray(file);
                    base64String = Base64.encodeToString(audioByte, 0);
                    new PostAudio().execute();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void recordAudio(String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory().getPath() + "/InspectorAudio");
        if (!direct.exists()) {
            direct.mkdirs();
        }

        final MediaRecorder recorder = new MediaRecorder();
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.MediaColumns.TITLE, fileName);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(Environment.getExternalStorageDirectory().getPath() + "/InspectorAudio/" + fileName);
        try {
            recorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(ManageTemplateActivity.this);
        mProgressDialog.setTitle(R.string.lbl_recording);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setButton("Stop recording", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mProgressDialog.dismiss();
                recorder.stop();
                recorder.release();
                play.setEnabled(true);
                add.setEnabled(true);
            }
        });

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface p1) {
                recorder.stop();
                recorder.release();
            }
        });
        recorder.start();
        mProgressDialog.show();
    }

    private void downloadAudio() {

        String path;
        message_view.setVisibility(View.VISIBLE);
//        recordLayout.setVisibility(View.GONE);

        File f = new File(Environment.getExternalStorageDirectory().getPath() + "/WorkerAudio");
        if (!f.exists()) {
            f.mkdir();
        }
        path = f.getPath();

        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request req = new DownloadManager.Request(Uri.parse("http://vikray.in/NImage/SubTask" + sub_id + ".mp3"));

        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle("Worker")
                .setDescription("Downloading Task Audio")
                .setDestinationInExternalPublicDir("" + "/WorkerAudio/", "SubTask" + sub_id + ".mp3");
        myDownloadReference = dm.enqueue(req);
    }

    private void addCheckListItems() {

        //Initialise
        itemArrayList = new ArrayList<String>();
        ImageButton addItem = (ImageButton) findViewById(R.id.imageButton_itemAdd);
        emptyDialog = (View) findViewById(R.id.empty);
        final EditText itemAddBox = (EditText) findViewById(R.id.editText_itemText);
        itemList = (ListView) findViewById(R.id.listView_items);
        Button submitItems = (Button) findViewById(R.id.button_submitList);

        new GetCheckList().execute();

        if (itemArrayList.isEmpty()) {
            emptyDialog.setVisibility(View.VISIBLE);
            itemList.setVisibility(View.GONE);
        } else {
            emptyDialog.setVisibility(View.GONE);
            itemList.setVisibility(View.VISIBLE);
        }

        //Add items to list from EditText
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemList.setVisibility(View.VISIBLE);
                emptyDialog.setVisibility(View.GONE);

                String s = itemAddBox.getText().toString();

                if (itemArrayList.contains(itemAddBox.getText().toString())) {
                    Toast.makeText(ManageTemplateActivity.this, "Item already exist", Toast.LENGTH_SHORT).show();
                } else if (s.matches("")) {
                    Toast.makeText(ManageTemplateActivity.this, "Please Enter an Item", Toast.LENGTH_SHORT).show();
                } else {

                    HashMap<String, Object> taskMap = new HashMap<String, Object>();
                    taskMap.put("ItemListString", itemAddBox.getText().toString());
                    dataList.add(taskMap);

                    itemArrayList.add(new String(itemAddBox.getText().toString()));
                    itemListSend.add(new String(itemAddBox.getText().toString()));
//                    itemList.setAdapter(new ArrayAdapter<String>(ManageTemplateActivity.this, android.R.layout.simple_list_item_1, itemArrayList));
                    checkAdapter = new CustomAdapter(ManageTemplateActivity.this, R.layout.checklist, dataList);
                    itemList.setAdapter(checkAdapter);
                    checkAdapter.notifyDataSetChanged();
                    itemAddBox.setText("");

                }
            }
        });

        //Submit List to server
        submitItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                 itemArrayList.clear();
                //Post List to server
                new PostList().execute();
            }
        });
    }

    private class PostList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ManageTemplateActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            HttpPost request = new HttpPost(getString(R.string.url) + "EagleXpetizeService.svc/NewCheckList");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONObject listJson = new JSONObject();
            JSONObject finalJson = new JSONObject();

            View v;
            TextView tv;
            for (int i = 0; i < itemList.getCount(); i++) {
                v = itemList.getChildAt(i);
                tv = (TextView) v.findViewById(R.id.textView_listItem);
                itemListSend.add(tv.getText().toString());
            }

            // Build JSON string
            try {

                JSONArray listArr = new JSONArray();
                for (int i = 0; i < itemListSend.size(); i++) {
                    listArr.put(itemListSend.get(i));
                }

                listJson.put("TaskId", sub_id);
                listJson.put("IsSubTask", 1);
                listJson.put("ItemList", listArr);
                listJson.put("Checked", 0);
                listJson.put("ModifiedBy", 1);
                listJson.put("CreatedBy", 1);
                finalJson.put("checklist", listJson);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("Json", String.valueOf(finalJson));

            StringEntity entity = null;
            try {
                entity = new StringEntity(finalJson.toString(), "UTF-8");
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

                        String message = jsonObject.getString("NewCheckListResult");

                        //Save UserId and UserName if success
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

            //Dismiss Dialog if showing
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (response_json == 200) {
                Toast.makeText(ManageTemplateActivity.this, "Success", Toast.LENGTH_SHORT).show();
                itemArrayList.clear();
            } else {
                Toast.makeText(ManageTemplateActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private class PostAudio extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ManageTemplateActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            String createdbyId = pref.GetPreferences("UserId");

            HttpPost request = new HttpPost(getString(R.string.url) + "EagleXpetizeService.svc/NewAttachment");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            JSONStringer userJson = null;

            // Build JSON string
            try {
                userJson = new JSONStringer()
                        .object()
                        .key("attachment")
                        .object()
                        .key("TaskId").value(sub_id)
                        .key("IsSubTask").value(true)
                        .key("File").value(base64String)
                        .key("FileType").value("mp3")
                        .key("ModifiedBy").value(1)
                        .key("CreatedBy").value(1)
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

                        String message = jsonObject.getString("NewAttachmentResult");

                        //Save UserId and UserName if success
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
                Toast.makeText(ManageTemplateActivity.this, "Success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ManageTemplateActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetCheckList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            itemArrayList.clear();
            pDialog = new ProgressDialog(ManageTemplateActivity.this);
            pDialog.setMessage(getString(R.string.pDialog_wait));
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            //Url with parameters
            String url = getString(R.string.url) + "EagleXpetizeService.svc/CheckLists/" + sub_id;

            // Making a request to url and get response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            Log.d("url", url);
            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {

                try {

                    JSONArray tasks = new JSONArray(jsonStr);

                    for (int i = 0; i < tasks.length(); i++) {
                        JSONObject c = tasks.getJSONObject(i);

                        String id = c.getString("TaskId");
                        String createdBy = c.getString("CreatedBy");
                        String modifiedBy = c.getString("ModifiedBy");
                        String item = c.getString("ItemListString");
                        String isSubTask = c.getString("IsSubTask");
                        String checked = c.getString("Checked");

                        HashMap<String, Object> taskMap = new HashMap<String, Object>();
                        taskMap.put("ItemListString", item);
                        dataList.add(taskMap);
                        itemArrayList.add(item);
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
            if (pDialog.isShowing())
                pDialog.dismiss();

//            itemList.setAdapter(new ArrayAdapter<String>(ManageTemplateActivity.this, android.R.layout.simple_list_item_1, itemArrayList));

            checkAdapter = new CustomAdapter(ManageTemplateActivity.this, R.layout.checklist, dataList);
            itemList.setAdapter(checkAdapter);

            if (itemArrayList.isEmpty()) {
                emptyDialog.setVisibility(View.VISIBLE);
                itemList.setVisibility(View.GONE);
            } else {
                emptyDialog.setVisibility(View.GONE);
                itemList.setVisibility(View.VISIBLE);
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

            TextView itemName;
        }

        //Initialise
        ViewHolder viewHolder;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                //inflate the custom layout
                convertView = inflater.from(parent.getContext()).inflate(R.layout.checklist, parent, false);
                viewHolder = new ViewHolder();

                //cache the views
                viewHolder.itemName = (TextView) convertView.findViewById(R.id.textView_listItem);

                //link the cached views to the convertview
                convertView.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) convertView.getTag();

            //set the data to be displayed
            viewHolder.itemName.setText(dataList.get(position).get("ItemListString").toString());

            return convertView;
        }
    }

}
