package com.example.ruralhealthcare;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.splash_screen);


    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {

            Intent intent = new Intent(SplashScreen.this,Login.class);
            startActivity(intent);



        }
    },4000);

    }


}