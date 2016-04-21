package com.example.mridul_xpetize.supervisor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class FullImageActivity extends AppCompatActivity {

    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        img = (ImageView)findViewById(R.id.imageView2);
        Intent i = getIntent();
        String url = i.getStringExtra("Image");
        Picasso.with(FullImageActivity.this)
                .load(url)
//                .placeholder(R.drawable.no_image)
                .into(img);
    }
}
