package com.example.ruralhealthcare;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.service.chooser.ChooserAction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.view.Change;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountManage extends AppCompatActivity {

    TextView Username,UserAddress,UserNumber;
    FirebaseDatabase firebaseDatabase;
    ImageView UserProfile;

    MaterialButton button;

    FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manage);

        String PatientId = getIntent().getStringExtra("PatientId");
        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Username = findViewById(R.id.username);
        UserAddress = findViewById(R.id.userAddress);
        UserNumber = findViewById(R.id.userNumber);
        UserProfile = findViewById(R.id.userProfile);
        button = findViewById(R.id.EditAccount);

        DisplayUserInfo(PatientId);


        button.setOnClickListener( v-> {

            ActionDialog();

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

                Glide.with(AccountManage.this).load(users.getProfile()).error(R.drawable.logo).into(UserProfile);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void ActionDialog(){
        AlertDialog.Builder chooseAction = new AlertDialog.Builder(AccountManage.this);
        chooseAction.setTitle("Choose Action To Edit Account");
        CharSequence ActionPick [] = {"Change Username","Change Password","Change Address","Change Contact"};


        chooseAction.setItems(ActionPick, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    changeUsername();
                }
                else if(which == 1){
                ChangePassword();

                }else if (which == 2){
                    ChangeAddress();
                }else if (which == 3){
                    ChangeContact();

                }
            }
        });
        chooseAction.show();

    }
    private void changeUsername() {
        String PatientId = getIntent().getStringExtra("PatientId");

        AlertDialog.Builder ChangeUserName = new AlertDialog.Builder(AccountManage.this);
        ChangeUserName.setTitle("Change Username");
        View view = LayoutInflater.from(AccountManage.this).inflate(R.layout.change_username,null,false);
        ChangeUserName.setView(view);

        TextInputEditText ChangeUsername;

        ChangeUsername = view.findViewById(R.id.ChangeUsername);

        firebaseDatabase.getReference("Patients").child(PatientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                ChangeUsername.setText(users.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ChangeUserName.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProgressDialog progressDialog = new ProgressDialog(AccountManage.this);

                progressDialog.setTitle("Editing Account");
                progressDialog.setMessage("Changing User");
                progressDialog.show();
                Map<String,Object> UpdateUserName = new HashMap<>();
                String Username = ChangeUsername.getText().toString();
                UpdateUserName.put("username",Username);

                firebaseDatabase.getReference("Patients").child(PatientId).updateChildren(UpdateUserName).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                             progressDialog.dismiss();
                        Toast.makeText(AccountManage.this, "Update Username Successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AccountManage.this, "Update Username Failed", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ChangeUserName.show();

    }


    private void ChangeAddress(){
        String PatientId = getIntent().getStringExtra("PatientId");

        AlertDialog.Builder ChangeAddress = new AlertDialog.Builder(AccountManage.this);
        ChangeAddress.setTitle("Change Address");
        View view = LayoutInflater.from(this).inflate(R.layout.change_address,null,false);
        ChangeAddress.setView(view);
        TextInputEditText ChangeAdd;

        ChangeAdd = view.findViewById(R.id.ChangeAddress);

        ChangeAddress.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String setAddress = ChangeAdd.getText().toString();
                Map<String,Object> UpdateAddress = new HashMap<>();
                UpdateAddress.put("address",setAddress);
                firebaseDatabase.getReference("Patients").child(PatientId).updateChildren(UpdateAddress).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AccountManage.this, "Update Address Successfull", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        ChangeAddress.show();



    }
    private void ChangeContact(){

        String PatientId = getIntent().getStringExtra("PatientId");

        AlertDialog.Builder  ChangeContact = new AlertDialog.Builder(AccountManage.this);
        ChangeContact.setTitle("Change Contact");
        View view = LayoutInflater.from(this).inflate(R.layout.change_contact,null,false);
        TextInputEditText   ChangeNumber;

        ChangeNumber = view.findViewById(R.id.ChangeContact);

        ChangeContact.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String setNumber =   ChangeNumber.getText().toString();
                Map<String,Object> UpdateContact = new HashMap<>();
                UpdateContact.put("",setNumber);
                firebaseDatabase.getReference("Patients").child(PatientId).updateChildren(UpdateContact).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AccountManage.this, "Update Address Successfull", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        ChangeContact.show();


    }

    private void ChangePassword(){
        AlertDialog.Builder ChangePass = new AlertDialog.Builder(AccountManage.this);
        ChangePass.setTitle("Change Password");
        View view = LayoutInflater.from(this).inflate(R.layout.change_password,null,false);
        ChangePass.setView(view);

        TextInputEditText ChangePassword;
        ChangePassword = view.findViewById(R.id.ChangePassword);


        ChangePass.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String setPass = ChangePassword.getText().toString();
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                firebaseUser.updatePassword(setPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(AccountManage.this, "Password Updated", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(AccountManage.this, "Failed Updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ChangePass.show();


    }

}