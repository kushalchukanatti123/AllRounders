package com.albatross.allrounders.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.albatross.allrounders.MAIN.LoginActivity;
import com.albatross.allrounders.MAIN.TutorSetupActivity;
import com.albatross.allrounders.R;
import com.albatross.allrounders.Util.Constants;
import com.albatross.allrounders.Util.GenUserUtilClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyStudentsFragment extends Fragment {
    private View v;
    FrameLayout registerLyt;
    FirebaseUser firebaseUser;
    Button registerBtn;
    DatabaseReference allUserRef;
    String myUid;
    boolean isTutor = false;
    FrameLayout progLyt;
    TextView classTxt,studentsTxt;
    FrameLayout containrerFramLyt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.my_stud_frag_lyt,container,false);
        registerLyt = v.findViewById(R.id.my_stud_login_fram_lyt_id);
        progLyt = v.findViewById(R.id.my_stud_prog_lyt);
        containrerFramLyt = v.findViewById(R.id.my_stud_fram_container_lyt_id);
        studentsTxt = v.findViewById(R.id.my_stud_stud_txt_id);
        classTxt = v.findViewById(R.id.my_stud_class_txt_id);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myUid = firebaseUser.getUid();
        allUserRef = FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS).child(myUid);
        registerBtn = v.findViewById(R.id.my_stud_register_btn_id);



        checkIsTutor();
        setFragments();
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), TutorSetupActivity.class);
                startActivity(i);
            }
        });

        return v;
    }

    private void setFragments() {

        classTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                classTxt.setBackground(getResources().getDrawable(R.drawable.color_acc_rect_bg));
                studentsTxt.setBackground(getResources().getDrawable(R.drawable.white_rect_bg));
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.my_stud_fram_container_lyt_id, new MyStudentsClassesFragment())
                        .commit();
            }
        });
        studentsTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                studentsTxt.setBackground(getResources().getDrawable(R.drawable.color_acc_rect_bg));
                classTxt.setBackground(getResources().getDrawable(R.drawable.white_rect_bg));
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.my_stud_fram_container_lyt_id, new MyStudentsStudentsFragment())
                        .commit();
            }
        });
    }

    private void checkIsTutor() {
        allUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    GenUserUtilClass userUtilClass = dataSnapshot.getValue(GenUserUtilClass.class);
                    String isTutorStr = userUtilClass.getIs_Tutor();
                    if (isTutorStr.equals(Constants.TRUE)){
                        registerLyt.setVisibility(View.GONE);
                        isTutor = true;
                        progLyt.setVisibility(View.GONE);
                        classTxt.setBackground(getResources().getDrawable(R.drawable.color_acc_rect_bg));
                        studentsTxt.setBackground(getResources().getDrawable(R.drawable.white_rect_bg));
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.my_stud_fram_container_lyt_id, new MyStudentsClassesFragment())
                                .commit();

                    }else {
                        isTutor = false;
                        registerLyt.setVisibility(View.VISIBLE);
                        progLyt.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                isTutor = false;
                Toast.makeText(getContext(), databaseError.toString(),Toast.LENGTH_SHORT).show();
                progLyt.setVisibility(View.VISIBLE);
            }
        });
    }
}
