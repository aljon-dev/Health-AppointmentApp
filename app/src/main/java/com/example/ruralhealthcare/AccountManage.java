package com.example.ruralhealthcare;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountManage extends AppCompatActivity {

    TextView Username,UserAddress,UserNumber;
    FirebaseDatabase firebaseDatabase;
    ImageView UserProfile;

    MaterialButton button;



    private final Object lock = new Object();
    private ExecutorService executorService;
    private Handler mainThreadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manage);

        String PatientId = getIntent().getStringExtra("PatiendId");
        firebaseDatabase = FirebaseDatabase.getInstance();

        Username = findViewById(R.id.username);
        UserAddress = findViewById(R.id.userAddress);
        UserNumber = findViewById(R.id.userNumber);
        UserProfile = findViewById(R.id.userProfile);
        button = findViewById(R.id.EditAccount);

        DisplayUserInfo(PatientId);


        executorService = Executors.newFixedThreadPool(2);
        mainThreadHandler = new Handler(Looper.getMainLooper());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            changeUsername();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            changeUsernameConfirm();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

            }
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
        CharSequence ActionPick [] = {"A"};


    }
    private void changeUsername() throws InterruptedException {
      synchronized (lock){
          lock.wait();
          mainThreadHandler.post(new Runnable() {
              @Override
              public void run() {
                  Toast.makeText(AccountManage.this, "Aica Be My Wife", Toast.LENGTH_SHORT).show();
              }
          });
      }
    }
    private void changeUsernameConfirm() throws InterruptedException {
      mainThreadHandler.post(new Runnable() {
          @Override
          public void run() {
              AlertDialog.Builder YouSure = new AlertDialog.Builder(AccountManage.this);
              YouSure.setTitle("Are You Sure About this");
              YouSure.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                      synchronized (lock){
                          lock.notify();
                      }
                  }
              });
              YouSure.show();
          }

      });
      Thread.sleep(2000);

    }

    private void ChangerAddress(){

    }
    private void ChangeContact(){

    }

    private void ChangePassword(){

    }

}