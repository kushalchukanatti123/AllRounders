package com.albatross.allrounders.MAIN;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.albatross.allrounders.R;
import com.albatross.allrounders.Util.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.io.Console;

public class SplashScreenAct extends AppCompatActivity {

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                checknSend();
            }
        }, 100);

    }

    private void checknSend() {
        if (firebaseUser==null){
            startActivity(new Intent(SplashScreenAct.this, ZeroActivity.class));
        }else {
            FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(firebaseUser.getUid())){
                        if (dataSnapshot.child(firebaseUser.getUid()).hasChild("Name")){

                            String oneSignalNotifId = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();

                            OneSignal.startInit(SplashScreenAct.this).init();
                            OneSignal.setSubscription(true);
                            String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS).child(myUid).child(Constants.Notif_Key).setValue(oneSignalNotifId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(SplashScreenAct.this,"Updated notif key ",Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                            startActivity(new Intent(SplashScreenAct.this, MainActivity.class));
                        }else {
                            startActivity(new Intent(SplashScreenAct.this, GeneralSetupActivity.class));
                        }
                    }else {
                        startActivity(new Intent(SplashScreenAct.this, GeneralSetupActivity.class));

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(SplashScreenAct.this,"Error "+databaseError.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
