package com.example.ruralhealthcare;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.FirebaseDatabase;

public class Home extends AppCompatActivity {

    DrawerLayout drawer;

    NavigationView nav;

    ImageButton ShowNavBtn;

    FirebaseDatabase firebaseDatabase;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        drawer = findViewById(R.id.NavDrawer);
        nav = findViewById(R.id.navLayOut);

        ShowNavBtn = findViewById(R.id.ShowNavigation);

        ShowNavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.open();
            }
        });






    }
}