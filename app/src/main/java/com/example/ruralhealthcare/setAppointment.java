package com.example.ruralhealthcare;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class setAppointment extends AppCompatActivity {


    private FirebaseDatabase firebaseDatabase;

    private TextView DoctorName,Position ,ServiceDate,ServiceTime;

    private MaterialButton SetDate, SetTime,UploadReceipt,SetAppointment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_appointment);

        String PatientId = getIntent().getStringExtra("PatientId");
        String admin_id = getIntent().getStringExtra("Admin");

        firebaseDatabase = FirebaseDatabase.getInstance();

        //TextView of Service when you Tap the Services from the Previous Class
        DoctorName = findViewById(R.id.DoctorName);
        Position= findViewById(R.id.Position);


        //TextView of data and Set Date Time and Service Date
        //Buttons

        SetDate = findViewById(R.id.SetDateBtn);
        SetTime = findViewById(R.id.SetTimeBtn);

        //TextView
        ServiceDate = findViewById(R.id.ServiceDate);
        ServiceTime = findViewById(R.id.ServiceTime);

        SetAppointment = findViewById(R.id.setAppointmentBtn);

        SetAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SetAppointments();
            }
        });



        SetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setServiceDate();
            }
        });

        SetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime();
            }
        });

    }


    private void setServiceDate(){

        Calendar calendar = Calendar.getInstance();

        int years = calendar.get(Calendar.YEAR);
        int Months = calendar.get(Calendar.MONTH);
        int Day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(setAppointment.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if(year < years || month < Months || dayOfMonth < Day){
                    Toast.makeText(setAppointment.this, "Cannot Set Appointment in the Past Date", Toast.LENGTH_SHORT).show();
                }else if (year >= years || Months >= month || dayOfMonth >= Day){
                    ServiceDate.setText(month + 1 + "/" + dayOfMonth + "/" + year);
                }

            }
        },years,Months,Day);
        datePickerDialog.show();
    }

    private void setTime(){


        Calendar calendar = Calendar.getInstance();

        int hours = calendar.get(Calendar.HOUR);
        int mins = calendar.get(Calendar.MINUTE);


        TimePickerDialog timePickerDialog = new TimePickerDialog(setAppointment.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                view.setIs24HourView(false);

                String AM_PM;


                int hourDay =  hourOfDay % 12;
                if(hourDay == 0) {
                    hourOfDay = 12; // Handle midnight (12 AM)
                    AM_PM = "AM";

                }else{
                    AM_PM = "PM";
                }
                String setTime = String.format("%02d:%02d%s",hourOfDay,minute,AM_PM);
                ServiceTime.setText(setTime);



            }
        },hours,mins,false);
        timePickerDialog.show();
    }

    private void SetAppointments(){
        String id = String.valueOf(System.currentTimeMillis());
        String PatientId = getIntent().getStringExtra("PatientId");
        String admin_id = getIntent().getStringExtra("Admin");
        String Doctorname = getIntent().getStringExtra("name");

        ProgressDialog progressDialog = new ProgressDialog(setAppointment.this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();



        //TextView converting to String

        String Name = Doctorname;
        String position = Position.getText().toString();
        String DateService = ServiceDate.getText().toString();
        String TimeService = ServiceTime.getText().toString();

        Map<String, Object> SetServices = new HashMap<>();

        SetServices.put("name",Name);
        SetServices.put("Position",position);

        SetServices.put("Date",DateService);
        SetServices.put("Time", TimeService);
        SetServices.put("Status","Waiting");
        SetServices.put("PatientId",PatientId);
        SetServices.put("AdminUid", admin_id);





        firebaseDatabase.getReference("Employee").child(admin_id).child("Appointment").child(id).setValue(SetServices);


        firebaseDatabase.getReference("Patients").child(PatientId).child("Appointment").child(id).setValue(SetServices);









    }

}