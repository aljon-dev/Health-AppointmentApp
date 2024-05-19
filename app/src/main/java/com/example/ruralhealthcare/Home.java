package com.example.ruralhealthcare;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home extends AppCompatActivity {

    DrawerLayout drawer;

    NavigationView nav;

    ImageButton ShowNavBtn;

    FirebaseDatabase firebaseDatabase;

    TextView Username,UserAddress,UserNumber;

    ImageView UserProfile;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        String PatientId = getIntent().getStringExtra("PatiendId");


        firebaseDatabase = FirebaseDatabase.getInstance();
        //Navigations
        drawer = findViewById(R.id.NavDrawer);
        nav = findViewById(R.id.navLayOut);

        //TextView and ImageView To Display Profile
        Username = findViewById(R.id.username);
        UserAddress = findViewById(R.id.userAddress);
        UserNumber = findViewById(R.id.userNumber);
        UserProfile = findViewById(R.id.userProfile);


        DisplayUserInfo(PatientId);





        // Show Navigations
        ShowNavBtn = findViewById(R.id.ShowNavigation);

        ShowNavBtn.setOnClickListener(v -> {
           drawer.open();
           });



    }

    private void DisplayUserInfo(String PatientId){

        firebaseDatabase.getReference("Patients").child(PatientId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);

                Username.setText(users.getUsername());
                UserAddress.setText(users.getAddress());
                UserNumber.setText(users.getContact());

                Glide.with(Home.this).load(users.getProfile()).error(R.drawable.logo).into(UserProfile);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}