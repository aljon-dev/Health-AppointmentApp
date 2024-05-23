package com.example.ruralhealthcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    DrawerLayout drawer;

    NavigationView nav;

    ImageButton ShowNavBtn;

    FirebaseDatabase firebaseDatabase;

    TextView Username,UserAddress,UserNumber;

    ImageView UserProfile;


    RecyclerView DoctorList,AppointmentListView;

    EmployeeAdapter adapter;

    ArrayList<UserRole> DoctorListItem;
    ArrayList<AppointmentStatus> AppointmentStatusList;

    AppointmentAdapter AppointAdapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        String PatientId = getIntent().getStringExtra("PatientId");


        firebaseDatabase = FirebaseDatabase.getInstance();
        //Navigations
        drawer = findViewById(R.id.NavDrawer);
        nav = findViewById(R.id.navLayOut);

        //TextView and ImageView To Display Profile
        Username = findViewById(R.id.username);
        UserAddress = findViewById(R.id.userAddress);
        UserNumber = findViewById(R.id.userNumber);
        UserProfile = findViewById(R.id.userProfile);

        DoctorList = findViewById(R.id.DoctorList);
        AppointmentListView = findViewById(R.id.AppointmentListView);

        DoctorList.setLayoutManager(new LinearLayoutManager(Home.this,LinearLayoutManager.HORIZONTAL,false));
        DoctorListItem = new ArrayList<>();
        adapter = new EmployeeAdapter(Home.this,DoctorListItem);
        DoctorList.setAdapter(adapter);

        DisplayUserInfo(PatientId);

        AppointmentListView.setLayoutManager(new LinearLayoutManager(this));
        AppointmentStatusList = new ArrayList<>();
        AppointAdapter = new AppointmentAdapter(this,AppointmentStatusList);

        AppointmentListView.setAdapter(AppointAdapter);

        firebaseDatabase.getReference("Patients").child(PatientId).child("Appointment").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    AppointmentStatus appointmentStatus = ds.getValue(AppointmentStatus.class);
                    AppointmentStatusList.add(appointmentStatus);
                }
                AppointAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });









        adapter.SetOnItemClickListener(new EmployeeAdapter.onClickListener() {
            @Override
            public void onClick(UserRole userRole) {
                Intent intent = new Intent(Home.this,setAppointment.class);
                intent.putExtra("PatientId",PatientId);
                intent.putExtra("Admin",userRole.getUid());
                intent.putExtra("name",userRole.getUsername());
                intent.putExtra("PatientName",Username.getText().toString());
                startActivity(intent);


            }
        });

        firebaseDatabase.getReference("Employee").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    UserRole userRole = ds.getValue(UserRole.class);
                    DoctorListItem.add(userRole);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Home.this, "Loading Error", Toast.LENGTH_SHORT).show();

            }
        });


        // Show Navigations
        ShowNavBtn = findViewById(R.id.ShowNavigation);

        ShowNavBtn.setOnClickListener(v -> {
           drawer.open();
           });

        //Navigations Functions
        nav.setNavigationItemSelectedListener(menuItem -> {
            int item = menuItem.getItemId();

            if(item == R.id.NavHome){

            }else if (item == R.id.NavManage){
                Intent intent = new Intent(Home.this,AccountManage.class);
                intent.putExtra("PatientId",PatientId);
                startActivity(intent);
            }else if(item == R.id.NavSearch){

            }else if(item == R.id.NavAbout){

            }else if(item == R.id.NavSignOut){

            }



            return false;
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
                Toast.makeText(Home.this, "Failed to load", Toast.LENGTH_SHORT).show();
            }
        });

    }

}