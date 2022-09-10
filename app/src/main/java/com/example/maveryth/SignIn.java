package com.example.maveryth;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.maveryth.databinding.ActivitySignInBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class SignIn extends AppCompatActivity {

    ActivitySignInBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    FirebaseDatabase database;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(SignIn.this);
        progressDialog.setTitle("Logging In");
        progressDialog.setMessage("Login to your account");

        //configure google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        /*binding.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                auth.signInWithEmailAndPassword(binding.email.getText().toString(), binding.password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(SignIn.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(SignIn.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });
        binding.clickSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, LoginActivity.class);
                startActivity(intent);

            }
        });*/
        binding.googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // signIn();
                resultLauncher.launch(new Intent(mGoogleSignInClient.getSignInIntent()));
            }
        });

        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(SignIn.this, MainActivity.class);
            startActivity(intent);
        }
    }
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

        //for google sign in
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent intent = result.getData();
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("TAG", "Google sign in failed", e);
                }

            }
        }

    });

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            Users users = new Users();
                            users.setUserId(user.getUid());
                            users.setUserName(user.getDisplayName());
                            users.setProfilepic(user.getPhotoUrl().toString());
                            database.getReference().child("Users").child(user.getUid()).setValue(users);

                            Intent intent=new Intent(SignIn.this,MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(SignIn.this, "Sign in with Google", Toast.LENGTH_SHORT).show();
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignIn.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Snackbar.make(binding.getRoot(),"Authentication Failed",Snackbar.LENGTH_LONG);
                            // updateUI(null);
                        }
                    }
                });
    }

}