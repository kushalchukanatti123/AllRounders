package com.albatross.allrounders.MAIN;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.albatross.allrounders.R;
import com.albatross.allrounders.Util.Constants;
import com.albatross.allrounders.Util.GenUserUtilClass;
import com.albatross.allrounders.Util.SendNotificationOnesignal;
import com.albatross.allrounders.Util.TutorUserUtilClass;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TutorProfileActivity extends AppCompatActivity {

    TutorUserUtilClass tutorInfoModel;
    String tutorUID,myUid;
    ImageButton sendQueryBtn;
    EditText queryEdtxt;
    ImageView imageView;
    ChipGroup subjectsChipGrp;
    Button connectBtn;
    TextView nameTxtVw, addressTxtVw,descTxtVw;
    DatabaseReference allUserRef;
    GenUserUtilClass userData;
    String myName;
    ImageButton backBtn;
    FrameLayout progLyt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_profile);

        connectBtn = findViewById(R.id.tutor_prof_act_conncect_btn_id);
        imageView = findViewById(R.id.tutor_prof_act_img_vw_id_id);
        nameTxtVw = findViewById(R.id.tutor_prof_act_name_id);
        descTxtVw = findViewById(R.id.tutor_prof_act_desc_id);
        addressTxtVw = findViewById(R.id.tutor_prof_act_address_id);
        subjectsChipGrp = findViewById(R.id.tutor_prof_act_chip_grp_id);
        queryEdtxt = findViewById(R.id.tutor_prof_act_query_edtxt_id);
        sendQueryBtn = findViewById(R.id.tutor_prof_act_send_query_img_btn_id);
        backBtn = findViewById(R.id.tutor_prof_act_back_img_btn_id);
        progLyt = findViewById(R.id.tutor_prof_fram_lyt_id);

        tutorInfoModel = (TutorUserUtilClass) getIntent().getSerializableExtra("DATA");
        tutorUID = getIntent().getStringExtra("UID");
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        allUserRef = FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        allUserRef.child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    myName = dataSnapshot.child("Name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        setProfileDetails();

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Map<String,Object> reqMap = new HashMap<>();
                Map<String,Object> map = new HashMap<>();
                map.put("Timestamp",Constants.getCurrentTimestamp());
                map.put(Constants.Type,Constants.Connection);
                reqMap.put(myUid,map);

                final Map<String,Object> actionsMap = new HashMap<>();
                Map<String,Object> map2 = new HashMap<>();
                map2.put("Timestamp",Constants.getCurrentTimestamp());
                map2.put("Status",Constants.PENDING);
                map2.put(Constants.Type,Constants.Connection);
                actionsMap.put(tutorUID,map2);

                progLyt.setVisibility(View.VISIBLE);
                allUserRef.child(tutorUID).child(Constants.Notifications).child(Constants.Requests)
                        .updateChildren(reqMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS).child(myUid).child(Constants.ACTIONS).child(Constants.Requests).updateChildren(actionsMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                connectBtn.setEnabled(false);
                                                connectBtn.setText("Pending");
                                                final String msg = myName+" has sent you a connection request";
                                                if (myName!=null){
                                                    progLyt.setVisibility(View.GONE);
                                                    new SendNotificationOnesignal(Constants.Connection_Request,msg,userData.getNotif_Key(),Constants.N_PROFILE_ACT);
                                                }else {
                                                    allUserRef.child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.exists()){
                                                                myName = dataSnapshot.child("Name").getValue().toString();
                                                                progLyt.setVisibility(View.GONE);
                                                                new SendNotificationOnesignal(Constants.Connection_Request,msg,userData.getNotif_Key(),Constants.N_PROFILE_ACT);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                            progLyt.setVisibility(View.GONE);

                                                            Toast.makeText(TutorProfileActivity.this,"Failed sending a notification",Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                                Toast.makeText(TutorProfileActivity.this,"A request has been sent to connect",Toast.LENGTH_SHORT).show();
                                            }else {
                                                progLyt.setVisibility(View.GONE);

                                                Toast.makeText(TutorProfileActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }else {
                            progLyt.setVisibility(View.GONE);
                            Toast.makeText(TutorProfileActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

        sendQueryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        Intent intent = new Intent(TutorProfileActivity.this,ChatActivity.class);
                        intent.putExtra("UID",tutorUID);
                        String message = queryEdtxt.getText().toString();
                        if (!TextUtils.isEmpty(message)){
                            intent.putExtra("MESSAGE",message);
                        }
                        //TODO:SEND TUTOR MODEL HERE
                        if (userData!=null){
                            intent.putExtra("DATA",userData);
                            startActivity(intent);
                        }



            }
        });


    }

    private void setProfileDetails() {
        userData = new GenUserUtilClass(tutorInfoModel.getName(),tutorInfoModel.getProfile_Url(),tutorInfoModel.getIs_Tutor(),tutorInfoModel.getDOB(),tutorInfoModel.getS_Desc(),tutorInfoModel.getGender(),tutorInfoModel.getMobile(),tutorInfoModel.getLat(),tutorInfoModel.getLng(),tutorInfoModel.getAddress_L(),tutorInfoModel.getAddress_C(),tutorInfoModel.getAddress_P(),tutorInfoModel.getNotif_Key());
        Glide.with(TutorProfileActivity.this).load(tutorInfoModel.getProfile_Url()).into(imageView);
        nameTxtVw.setText(tutorInfoModel.getName());
        descTxtVw.setText(tutorInfoModel.getT_Desc());
        String add = userData.getAddress_L()+"\n"+userData.getAddress_C()+"\n"+userData.getAddress_P();
        addressTxtVw.setText(add);
        Map m = tutorInfoModel.getSubjects();
        ArrayList<String> sList = new ArrayList<>();
        sList.addAll(m.keySet());
        for (String s : sList) {
            final Chip chip = new Chip(TutorProfileActivity.this);
            chip.setText(s);
            chip.setClickable(false);
            chip.setCheckable(false);
            subjectsChipGrp.addView(chip);
        }

        //Updating conncect button
        allUserRef.child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.child(Constants.ACTIONS).child(Constants.Requests).hasChild(tutorUID)){
                       String status = dataSnapshot.child(tutorUID).child("Status").getValue().toString();
                       if (status.equals(Constants.PENDING)){
                           connectBtn.setEnabled(false);
                           connectBtn.setText("Pending");
                       }else {
                           connectBtn.setEnabled(true);
                           connectBtn.setText("Connect");
                       }
                    }
                    if (dataSnapshot.child(Constants.My_Tutors).hasChild(tutorUID)){
                        connectBtn.setEnabled(false);
                        connectBtn.setText("Connected");
                    }


                    if (dataSnapshot.hasChild(Constants.Demo)){
                        //TODO: DEMO VIDEOS ARE AVAILABLE SHOW THEM
                        Iterable<DataSnapshot> demoSnap = dataSnapshot.child(Constants.Demo).getChildren();
                        for (DataSnapshot d:demoSnap){
                            //SHOW THUMBNAILS

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //TODO: CHECK IF ALREADY IS TEACHING AND FOUND IN TUTOR SECTION AND UPDATE THE CONNECT BUTTON //DONE
    }
    //GEOCODING

    //GEOCODING ENDS
}
