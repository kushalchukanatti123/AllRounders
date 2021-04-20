package com.albatross.allrounders.MAIN;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.albatross.allrounders.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1000;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseAuth.AuthStateListener authStateListener;
    ValueEventListener loginListener;
    DatabaseReference userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user!=null){
                    if (user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp()) {
                        Toast.makeText(LoginActivity.this, "Fresh user.", Toast.LENGTH_SHORT).show();
                        // addUserToDatabase();
                    } else {
                        Toast.makeText(LoginActivity.this, "Glad your'e back again", Toast.LENGTH_SHORT).show();
                    }

                    checkUserAvail();
                    //TODO:CHECK USER AVAILIBILITY

                }else {
                    handleLoginRegister(); //RECENT CHANGED

                }

                // ...
            }
        };

        handleLoginRegister();
    }
    private void handleLoginRegister() {
        List<AuthUI.IdpConfig> provider = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(provider)
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp()) {
                    Toast.makeText(LoginActivity.this, "Fresh user.", Toast.LENGTH_SHORT).show();
                    // addUserToDatabase();
                } else {
                    Toast.makeText(LoginActivity.this, "Glad your'e back again", Toast.LENGTH_SHORT).show();
                }

                checkUserAvail();

                // ...
            } else {
                Toast.makeText(LoginActivity.this,"Login failed",Toast.LENGTH_LONG).show();
                finish();
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void checkUserAvail() {
        boolean avail = false;
        userRef = FirebaseDatabase.getInstance().getReference().child("ALL_USERS");
        loginListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        if (dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).hasChild("Name")){
                            //TODO:CHECK HERE AND SEND FROM WHERE HED CAME LIKE TO MY STUD OR MAIN ACT

                            if (getIntent().hasExtra("FROM")){
                                if (getIntent().getStringExtra("FROM").equals("MY_STUD")){
                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                    i.putExtra("FROM","MY_STUD");
                                    startActivity(i);
                                    finish();
                                    return;
                                }
                            }
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }else {
                            if (getIntent().hasExtra("FROM")){
                                if (getIntent().getStringExtra("FROM").equals("MY_STUD")){
                                    Intent i = new Intent(LoginActivity.this, TutorSetupActivity.class);
                                    i.putExtra("FROM","MY_STUD");
                                    startActivity(i);
                                    finish();
                                    return;
                                }
                            }
                            startActivity(new Intent(LoginActivity.this, GeneralSetupActivity.class));
                            finish();

                        }
                    }else {
                        if (getIntent().hasExtra("FROM")){
                            if (getIntent().getStringExtra("FROM").equals("MY_STUD")){
                                Intent i = new Intent(LoginActivity.this, TutorSetupActivity.class);
                                i.putExtra("FROM","MY_STUD");
                                startActivity(i);
                                finish();
                                return;
                            }
                        }
                        startActivity(new Intent(LoginActivity.this, GeneralSetupActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        userRef.addValueEventListener(loginListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (userRef!=null){
            if (loginListener!=null){
                userRef.removeEventListener(loginListener);
            }
        }
    }
}
