package com.example.maveryth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.example.maveryth.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    private FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        getSupportActionBar().hide();
        auth=FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        progressDialog=new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Creating new account");
        progressDialog.setMessage("Your account is being created");

        binding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();;
                auth.createUserWithEmailAndPassword(binding.email.getText().toString(),binding.password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Users user=new Users(binding.yourName.getText().toString(),binding.email.getText().toString(),binding.password.getText().toString());
                            String id=task.getResult().getUser().getUid();
                            database.getReference().child("Users").child(id).setValue(user);
                            Toast.makeText(LoginActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });
        binding.already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,SignIn.class);
                startActivity(intent);

            }
        });
    }
}