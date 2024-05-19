package com.example.ruralhealthcare;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Login extends AppCompatActivity {

    FirebaseAuth auth;

    FirebaseDatabase firebaseDatabase;



    TextView SignUp;

    EditText Email,Password;

    Button LoginBtn;

    ExecutorService executorService;

    Handler mainThreadHandler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();


        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.password);
        SignUp = findViewById(R.id.SignUp);
        LoginBtn = findViewById(R.id.loginBtn);


       executorService = Executors.newSingleThreadExecutor();
         mainThreadHandler = new Handler(Looper.getMainLooper());


        SignUp.setOnClickListener(v ->  {

                Intent intent = new Intent(Login.this,Register.class);
                startActivity(intent);

        });

        LoginBtn.setOnClickListener( v -> {
            String email = Email.getText().toString();
            String password = Password.getText().toString();

            SignIn(email,password);


        });


    }
private void SignIn (String email, String password ){

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        if(task.isSuccessful()){
                           mainThreadHandler.post(new Runnable() {
                               @Override
                               public void run() {

                                   FirebaseUser firebaseUser = auth.getCurrentUser();
                                   Intent intent = new Intent(Login.this,Home.class);
                                   intent.putExtra("PatiendId",firebaseUser.getUid());
                                   Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                   startActivity(intent);

                               }
                           });
                        }else{
                            Toast.makeText(Login.this, "Invalid Email & Password ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

        });



}

}