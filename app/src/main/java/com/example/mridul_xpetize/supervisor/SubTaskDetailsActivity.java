package com.example.mridul_xpetize.supervisor;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.Arrays;

public class SubTaskDetailsActivity extends AppCompatActivity {

    Button record, play, send, test;
    ProgressDialog pDialog;
    PreferencesHelper pref;
    String sub_id;
    int response_json;
    byte[] audioByte;
    String base64String;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_task_details);

        //Initialise
        pref = new PreferencesHelper(SubTaskDetailsActivity.this);
        record = (Button) findViewById(R.id.record_btn);
        test = (Button) findViewById(R.id.button_test);
        play = (Button) findViewById(R.id.play_btn);
        send = (Button) findViewById(R.id.send_btn);
        TextView subName = (TextView) findViewById(R.id.view_subName);
        TextView desc = (TextView) findViewById(R.id.view_description);
        TextView comments = (TextView) findViewById(R.id.view_comments);
        TextView status = (TextView) findViewById(R.id.view_status);
        TextView assigned = (TextView) findViewById(R.id.view_assigned);

        //GetIntent Values
        Intent i = getIntent();
        String name_st = i.getStringExtra("SubName");
        sub_id = i.getStringExtra("SubId");
        String desc_st = i.getStringExtra("Desc");
        String comments_st = i.getStringExtra("Comments");
        String status_st = i.getStringExtra("Status");
        final String assigned_st = i.getStringExtra("Assigned");
        Log.d("SubId", sub_id);

        //SetTextValues
        subName.setText(name_st);
        desc.setText("Description : " + desc_st);
        comments.setText("Comments : " + comments_st);
        status.setText("Status : " + status_st);
        assigned.setText("Assigned By : " + assigned_st);

        //onClick of record Button
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recordAudio("SubTask" + sub_id + ".mp3");
            }
        });

        play.setEnabled(false);
        send.setEnabled(true);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/InspectorAudio/SubTask" + sub_id + ".mp3");
                intent.setDataAndType(Uri.fromFile(file), "audio/*");
                startActivity(intent);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                base64String = getString(R.string.audioEncode);
//                new PostAttachment().execute();

//                String path = Environment.getExternalStorageDirectory().getPath() + "/InspectorAudio/SubTask" + sub_id + ".mp3";
                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/InspectorAudio/SubTask" + sub_id + ".mp3");
                try {
                    audioByte = FileUtils.readFileToByteArray(file);
                    base64String = Base64.encodeToString(audioByte, 0);
                    new PostAudio().execute();
//                    Log.d("Test", base64String);
//                    new PostAttachment().execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                try {
//                    audioByte = convert(path);
////                                    encodedImage = encodeToBase64(temp, Bitmap.CompressFormat.JPEG, 50);
//
//                    base64String = Base64.encodeToString(audioByte, 0);
//                    Log.d("test", base64String);
//                    new PostAttachment().execute();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        });

//        test.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                base64String = getString(R.string.audioEncode);
//                base64String = getString(R.string.audioEncode);
//                new PostAudio().execute();
//
////                byte[] decoded = Base64.decode(base64String, 0);
////                try
////                {
////                    File file2 = new File(Environment.getExternalStorageDirectory().getPath() + "/InspectorAudio/test.mp3");
////                    FileOutputStream os = new FileOutputStream(file2, true);
////                    os.write(decoded);
////                    os.close();
////                }
////                catch (Exception e)
////                {
////                    e.printStackTrace();
////                }
////
////                Intent intent = new Intent();
////                intent.setAction(android.content.Intent.ACTION_VIEW);
////                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/InspectorAudio/test.mp3");
////                intent.setDataAndType(Uri.fromFile(file), "audio/*");
////                startActivity(intent);
//            }
//        });
    }

//    public byte[] convert(String path) throws IOException {
//
//        FileInputStream fis = new FileInputStream(path);
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        byte[] b = new byte[8192];
//
//        for (int readNum; (readNum = fis.read(b)) != -1; ) {
//            bos.write(b, 0, readNum);
//        }
//
//        byte[] bytes = bos.toByteArray();
//        return bytes;
//    }

    private class PostAudio extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SubTaskDetailsActivity.this);
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
                Toast.makeText(SubTaskDetailsActivity.this, "Success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SubTaskDetailsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
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

        final ProgressDialog mProgressDialog = new ProgressDialog(SubTaskDetailsActivity.this);
        mProgressDialog.setTitle(R.string.lbl_recording);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setButton("Stop recording", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mProgressDialog.dismiss();
                recorder.stop();
                recorder.release();
                play.setEnabled(true);
                send.setEnabled(true);
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
}
