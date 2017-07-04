package com.zyyoona7.customviewsets.heart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.zyyoona7.customviewsets.R;

import java.util.Random;

public class HeartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);
        final HeartView heartView= (HeartView) findViewById(R.id.heart_view);
        heartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                heartView.addHeart(new Random().nextInt(6));
            }
        });
    }
}
