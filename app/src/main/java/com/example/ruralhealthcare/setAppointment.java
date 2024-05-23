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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private TextView DoctorName, Position, ServiceDate, ServiceTime;
    private MaterialButton SetDate, SetTime, SetAppointment;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_appointment);

        String PatientId = getIntent().getStringExtra("PatientId");
        String admin_id = getIntent().getStringExtra("Admin");

        firebaseDatabase = FirebaseDatabase.getInstance();

        // Initialize UI components
        DoctorName = findViewById(R.id.DoctorName);
        Position = findViewById(R.id.Position);
        SetDate = findViewById(R.id.SetDateBtn);
        SetTime = findViewById(R.id.SetTimeBtn);
        ServiceDate = findViewById(R.id.ServiceDate);
        ServiceTime = findViewById(R.id.ServiceTime);
        SetAppointment = findViewById(R.id.setAppointmentBtn);

        // Fetch and display doctor details
        firebaseDatabase.getReference("Employee").child(admin_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isDestroyed()) {
                    UserRole userRole = snapshot.getValue(UserRole.class);
                    if (userRole != null) {
                        DoctorName.setText(userRole.getUsername());
                        Position.setText(userRole.getRole());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });

        // Set appointment button listener
        SetAppointment.setOnClickListener(v -> SetAppointments());

        // Set date button listener
        SetDate.setOnClickListener(v -> setServiceDate());

        // Set time button listener
        SetTime.setOnClickListener(v -> setTime());
    }

    private void setServiceDate() {
        Calendar calendar = Calendar.getInstance();
        int years = calendar.get(Calendar.YEAR);
        int months = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(setAppointment.this, (view, year, month, dayOfMonth) -> {
            if (year < years || (year == years && month < months) || (year == years && month == months && dayOfMonth < day)) {
                Toast.makeText(setAppointment.this, "Cannot set appointment in the past date", Toast.LENGTH_SHORT).show();
            } else {
                ServiceDate.setText((month + 1) + "/" + dayOfMonth + "/" + year);
            }
        }, years, months, day);
        datePickerDialog.show();
    }

    private void setTime() {
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int mins = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(setAppointment.this, (view, hourOfDay, minute) -> {
            boolean isPM = hourOfDay >= 12;
            view.setIs24HourView(false);
            int hourIn12Format = hourOfDay % 12 == 0 ? 12 : hourOfDay % 12;
            String amPm = isPM ? "PM" : "AM";
            String setTime = String.format("%02d:%02d %s", hourIn12Format, minute, amPm);
            ServiceTime.setText(setTime);
        }, hours, mins, false);
        timePickerDialog.show();
    }

    private void SetAppointments() {
        String id = String.valueOf(System.currentTimeMillis());
        String PatientId = getIntent().getStringExtra("PatientId");
        String admin_id = getIntent().getStringExtra("Admin");
        String Patient_name = getIntent().getStringExtra("PatientName");

        progressDialog = new ProgressDialog(setAppointment.this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();

        // TextView converting to String
        String Name = DoctorName.getText().toString();
        String position = Position.getText().toString();
        String DateService = ServiceDate.getText().toString();
        String TimeService = ServiceTime.getText().toString();

        Map<String, Object> SetServices = new HashMap<>();
        SetServices.put("name", Name);
        SetServices.put("Position", position);
        SetServices.put("Date", DateService);
        SetServices.put("Time", TimeService);
        SetServices.put("Status", "Waiting");
        SetServices.put("PatientId", PatientId);
        SetServices.put("AdminUid", admin_id);
        SetServices.put("PatientName", Patient_name);
        SetServices.put("AppointId",id);

        DatabaseReference employeeRef = firebaseDatabase.getReference("Employee").child(admin_id).child("Appointment").child(id);
        DatabaseReference patientRef = firebaseDatabase.getReference("Patients").child(PatientId).child("Appointment").child(id);

        employeeRef.setValue(SetServices).addOnCompleteListener(task -> {
            if (isDestroyed()) return;
            if (task.isSuccessful()) {
                patientRef.setValue(SetServices).addOnCompleteListener(innerTask -> {
                    if (isDestroyed()) return;
                    progressDialog.dismiss();
                    if (innerTask.isSuccessful()) {
                        Toast.makeText(setAppointment.this, "Appointment set successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(setAppointment.this, "Failed to set appointment", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                progressDialog.dismiss();
                Toast.makeText(setAppointment.this, "Failed to set appointment", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            if (isDestroyed()) return;
            progressDialog.dismiss();
            Toast.makeText(setAppointment.this, "Failed to set appointment", Toast.LENGTH_SHORT).show();
        });
    }
}
