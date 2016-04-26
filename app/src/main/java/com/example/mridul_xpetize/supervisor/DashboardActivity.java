package com.example.mridul_xpetize.supervisor;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

public class DashboardActivity extends AppCompatActivity {

    ImageButton view_inspectors, add_inspectors, notification, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Supervisor");

        //Initialise
        view_inspectors = (ImageButton) findViewById(R.id.imageButton_inspectors);
        add_inspectors = (ImageButton) findViewById(R.id.imageButton_addInsp);
        notification = (ImageButton) findViewById(R.id.imageButton_notification);
        logout = (ImageButton) findViewById(R.id.imageButton_logout);

        //onClick of view inspectors
        view_inspectors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(DashboardActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        //onClick of add inspectors
        add_inspectors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(DashboardActivity.this, TestActivity.class);
                startActivity(i);
            }
        });

        //onClick of notification
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(DashboardActivity.this, NotificationActivity.class);
                startActivity(i);
            }
        });

        //onClick of logout
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //trigger dialog box
                dialogBox();

            }
        });
    }

    //Alert Dialog
    public void dialogBox() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Confirm");
        alertDialogBuilder.setMessage("Do You Want to Log Out ?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        //Exit
                        System.exit(0);

                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
