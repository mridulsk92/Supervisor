package com.example.mridul_xpetize.supervisor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class TestActivity extends AppCompatActivity {

    private static String cryptoPass = "sup3rS3yx";
    ProgressDialog pDialog;
    EditText name, loc, desig, username, pass, created;
    String name_st, loc_st, desig_st, username_st, pass_st, create_st;
    Button user_add;
    String encryptedUsername, encryptedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        user_add = (Button)findViewById(R.id.button_useradd);
        name = (EditText) findViewById(R.id.editText_name);
        loc = (EditText) findViewById(R.id.editText_loc);
        desig = (EditText) findViewById(R.id.editText_desig);
        username = (EditText) findViewById(R.id.editText_username);
        pass = (EditText) findViewById(R.id.editText_passs);
        created = (EditText) findViewById(R.id.editText_create);

        user_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name_st = name.getText().toString();
                loc_st = loc.getText().toString();
                desig_st = desig.getText().toString();
                username_st = username.getText().toString();
                pass_st = pass.getText().toString();
                create_st = created.getText().toString();

                encryptedUsername = encryptIt(username_st);
                encryptedPassword = encryptIt(pass_st);

                new CreateUser().execute();
            }
        });
    }

    private class CreateUser extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(TestActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

//            String url = "http://vikray.in/MyService.asmx/GetEmployessJSONNewN";
            String url = "http://vikray.in/MyService.asmx/InsertUserDetails?Name="+name_st+"&Designation="+desig_st+"&location="+loc_st+"&username="+username_st+"&password="+pass_st+"&createdby="+create_st;
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

            Intent i = new Intent(TestActivity.this, MainActivity.class);
            startActivity(i);
        }
    }

    public static String encryptIt(String value) {
        try {
            DESKeySpec keySpec = new DESKeySpec(cryptoPass.getBytes("UTF8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            byte[] clearText = value.getBytes("UTF8");
            // Cipher is not thread safe
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            String encrypedValue = Base64.encodeToString(cipher.doFinal(clearText), Base64.DEFAULT);
            Log.d("TAG", "Encrypted: " + value + " -> " + encrypedValue);
            return encrypedValue;

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return value;
    };

    @Override
    protected void onPause() {
        super.onPause();
        super.finish();
    }
}
