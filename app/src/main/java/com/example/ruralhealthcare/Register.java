package com.example.ruralhealthcare;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {

    FirebaseAuth auth;

    FirebaseDatabase firebaseDatabase;

    EditText email,password,confirmpassword,contact,address;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.EmailEt);
        password = findViewById(R.id.passwordEt);
        confirmpassword = findViewById(R.id.ConfirmPassword);
        contact = findViewById(R.id.Contact);
        address = findViewById(R.id.Address);

        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();
        String confirmPassword = password.getText().toString();
        String Contacts = contact.getText().toString();
        String Address  = address.getText().toString();

        if(userEmail.isEmpty() || userPassword.isEmpty() || confirmPassword.isEmpty() || Contacts.isEmpty() || Address.isEmpty()){

            Toast.makeText(this, "Fill the fields", Toast.LENGTH_SHORT).show();

            if(!userPassword.equals(confirmPassword)){
                Toast.makeText(this, "Password not match", Toast.LENGTH_SHORT).show();
            }else{
                Register(userEmail,userPassword,Contacts,Address);
            }


        }


    }
    private void Register(String email,String password,String contact,String address){

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser users = auth.getCurrentUser();
                    Users user = new Users();

                    user.setUid(users.getUid());
                    user.setEmail(email);
                    user.setContact(contact);
                    user.setAddress(address);


                    firebaseDatabase.getReference().child("Patients").child(users.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Toast.makeText(Register.this, "This Account is Register  ", Toast.LENGTH_SHORT).show();
                            } else {
                                firebaseDatabase.getReference().child("Patients").child(users.getUid()).setValue(user);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                    else if(task.getException() instanceof FirebaseAuthUserCollisionException ) {
                    Toast.makeText(Register.this, "This Account is Already Exist ", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


}