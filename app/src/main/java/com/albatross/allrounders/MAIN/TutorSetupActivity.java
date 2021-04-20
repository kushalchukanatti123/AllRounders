package com.albatross.allrounders.MAIN;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.albatross.allrounders.R;
import com.albatross.allrounders.Util.Constants;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class TutorSetupActivity extends AppCompatActivity {

    ChipGroup selectedSubChipGrp;
    ImageButton addSubBtn;
    Button saveButton;
    FirebaseRecyclerAdapter<String,SubViewHolder> adapter;
    DatabaseReference allUserRef;
    String myUid;

    Dialog subDialog,upSubDialog;
    EditText upSubDlgEdtxt;
    Button upSubCancBtn,upSubUpdateBtn;
    ChipGroup dlgSubChipGrp;
    EditText dlgSubEdtxt;
    Button dlgSubCancelBtn, dlgSubApplyBtn;
    RecyclerView dlgSubRecyVw;
    ArrayList<String> mselectedSubList = new ArrayList<>();
    ArrayList<String> selectedSubList = new ArrayList<>();
    Spinner teachModeSpinner;
    ArrayAdapter<String> teachModeSpinnerAdapter;
    String teachMode = Constants.TEACHING_MODE_ARR[0];
    String amount,TDesc;
    FrameLayout progLyt;
    TextInputEditText chargeEdtxt,TDescEdtxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_setup);

        selectedSubChipGrp = findViewById(R.id.tut_Setup_csub_hip_grp_id);
        addSubBtn = findViewById(R.id.tut_Setup_Add_sub_imgbtn_id);
        saveButton = findViewById(R.id.tutor_setup_save_btn_id);
        chargeEdtxt = findViewById(R.id.tutor_setup_charge_edtxt_id);
        teachModeSpinner = findViewById(R.id.tut_Setup_mode_spinner_id);
        TDescEdtxt = findViewById(R.id.tutor_setup_desc_edtxt_id);
        progLyt = findViewById(R.id.tut_set_prog_lyt);
        createUpSubDialog();
        setSubDialog();
        setSpinners();

        allUserRef = FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS);
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        addSubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: SHOW A DIALOG HERE TO SELECT SUBJECTS
                dlgSubChipGrp.removeAllViews();
                selectedSubList.clear();
                dlgSubEdtxt.setText("");
                subDialog.show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTutor();
            }
        });
    }

    private void setSpinners() {
        teachModeSpinnerAdapter = new ArrayAdapter<String>(TutorSetupActivity.this,android.R.layout.simple_spinner_item,Constants.TEACHING_MODE_ARR);
        teachModeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teachModeSpinner.setAdapter(teachModeSpinnerAdapter);

        teachModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i){
                    case 0:
                        teachMode = Constants.TEACHING_MODE_ARR[0];
                        break;
                    case 1:
                        teachMode = Constants.TEACHING_MODE_ARR[1];

                        break;
                    case 2:
                        teachMode = Constants.TEACHING_MODE_ARR[2];

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void createUpSubDialog() {
        upSubDialog = new Dialog(this);
        upSubDialog.setContentView(R.layout.update_subject_name_dlg_lyt);
        upSubCancBtn = upSubDialog.findViewById(R.id.up_sub_name_cancel_btn_id);
        upSubUpdateBtn = upSubDialog.findViewById(R.id.up_sub_name_ok_btn_id);
        upSubDlgEdtxt = upSubDialog.findViewById(R.id.up_sub_name_edtxt_id);

        upSubCancBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upSubDialog.dismiss();
            }
        });
        upSubUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:UPDATE SUBJECT HERE
                String sub = upSubDlgEdtxt.getText().toString();
                if (TextUtils.isEmpty(sub)){
                    return;
                }
            }
        });
    }

    private void saveTutor() {
        //TODO: UPDATE ISTUTOR AND ADD SUBJECTS NODE
        Map<String,Object> updateMap = new HashMap();
        Map<String,Object> subMAp= new HashMap();

        amount = chargeEdtxt.getText().toString().trim();
        TDesc = TDescEdtxt.getText().toString().trim();
        if (TextUtils.isEmpty(amount)){
            Toast.makeText(TutorSetupActivity.this,"Amount is empty",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(TDesc)){
            Toast.makeText(TutorSetupActivity.this,"Description is empty",Toast.LENGTH_SHORT).show();
            return;
        }
        if (mselectedSubList.size()<1){
            Toast.makeText(TutorSetupActivity.this,"Please add a subject to proceed",Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i=0;i<mselectedSubList.size();i++){
            subMAp.put(mselectedSubList.get(i),Constants.TRUE);
        }
        updateMap.put("Subjects",subMAp);
        updateMap.put(Constants.Is_Tutor,Constants.TRUE);
        updateMap.put(Constants.Amount,amount);
        updateMap.put(Constants.Mode,teachMode);
        updateMap.put(Constants.T_Desc,TDesc);

        progLyt.setVisibility(View.VISIBLE);
        allUserRef.child(myUid).updateChildren(updateMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    Map<String,Object> tutorMap = new HashMap();
                    Map<String,Object> tMap = new HashMap<>();
                    tMap.put("Timestamp",Constants.getCurrentTimestamp());
                    tutorMap.put(myUid,tMap);

                    FirebaseDatabase.getInstance().getReference().child(Constants.ALL_TUTORS).updateChildren(tutorMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                progLyt.setVisibility(View.GONE);
                                Toast.makeText(TutorSetupActivity.this,"Successfully added as teacher",Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(TutorSetupActivity.this,MainActivity.class);
                                i.putExtra("TARGET","MY_STUDENTS");
                                startActivity(i);
                            }else {
                                progLyt.setVisibility(View.GONE);
                                Toast.makeText(TutorSetupActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else {
                    progLyt.setVisibility(View.GONE);
                    Toast.makeText(TutorSetupActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setSubDialog() {
        subDialog = new Dialog(TutorSetupActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        subDialog.setContentView(R.layout.tutor_setup_sub_dlg_lyt);
        dlgSubApplyBtn = subDialog.findViewById(R.id.tut_setup_sub_dlg_apply_btn_id);
        dlgSubCancelBtn = subDialog.findViewById(R.id.tut_setup_sub_dlg_dismiss_btn_id);
        dlgSubEdtxt = subDialog.findViewById(R.id.tut_setup_sub_dlg_edtxt_id);
        dlgSubChipGrp = subDialog.findViewById(R.id.tut_setup_sub_dlg_chpgrp_id);
        dlgSubRecyVw = subDialog.findViewById(R.id.tut_setup_sub_dlg_recy_vw_id);

        dlgSubRecyVw.setLayoutManager(new LinearLayoutManager(this));
        firebaseSubSearch("");
        dlgSubEdtxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                firebaseSubSearch(charSequence.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dlgSubApplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subDialog.dismiss();


                dlgSubChipGrp.clearCheck();
                for (int i=0;i<selectedSubList.size();i++){
                    final Chip chip = new Chip(TutorSetupActivity.this);
                    //  chip.setText(model.getName());
                    chip.setCloseIconVisible(true);
                    chip.setClickable(false);
                    chip.setCheckable(false);
                    final int finalI = i;
                    chip.setText(selectedSubList.get(finalI));
                    chip.setOnCloseIconClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            selectedSubChipGrp.removeView(view);
                            mselectedSubList.remove(chip.getText());
                            if (mselectedSubList.size()==0){
                                selectedSubChipGrp.setVisibility(View.GONE);
                            }
                        }
                    });
                    if (!mselectedSubList.contains(selectedSubList.get(i))){
                        mselectedSubList.add(selectedSubList.get(i));
                        selectedSubChipGrp.setVisibility(View.VISIBLE);
                        selectedSubChipGrp.addView(chip);
                    }
                }
                mselectedSubList.addAll(selectedSubList);
                selectedSubList.clear();

            }
        });
        dlgSubCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subDialog.dismiss();
                selectedSubList.clear();
                dlgSubChipGrp.clearCheck();
            }
        });
    }

    private void firebaseSubSearch(String text) {

        Query searchQuery = FirebaseDatabase.getInstance().getReference().child("SUBJECTS").orderByKey().limitToFirst(100).startAt(text).endAt(text+"\uf8ff");

        /*searchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(getContext(),adapter.getItemCount()+"",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        FirebaseRecyclerOptions<String> options = new FirebaseRecyclerOptions.Builder<String>()
                .setQuery(searchQuery, String.class)
                .build();



        adapter = new FirebaseRecyclerAdapter<String, SubViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SubViewHolder holder, int position, @NonNull final String model) {
                final String subject = this.getSnapshots().getSnapshot(position).getKey();
                /*if (uid.equals(myUid)||model.getRank().equals(Constants.RANK_CORE)||model.getRank().equals(Constants.RANK_LEADER)){
                    holder.cardView.setVisibility(View.GONE);
                }else {
                    holder.cardView.setVisibility(View.VISIBLE);
                }*/
                holder.textView.setText(subject);

                holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (subject.equals(Constants.Others)){
                            //TODO:CREATE A DIALOG HERE TO UPDATE SUBJECT
                            return;
                        }
                        final Chip chip = new Chip(TutorSetupActivity.this);
                        //  chip.setText(model.getName());
                        chip.setCloseIconVisible(true);
                        chip.setText(subject);
                        chip.setClickable(false);
                        chip.setCheckable(false);
                        chip.setOnCloseIconClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dlgSubChipGrp.removeView(view);
                                selectedSubList.remove(subject);
                                if (selectedSubList.size()==0){
                                    dlgSubChipGrp.setVisibility(View.GONE);
                                }
                            }
                        });
                        if (!selectedSubList.contains(subject)){
                            selectedSubList.add(subject);
                            dlgSubChipGrp.setVisibility(View.VISIBLE);
                            dlgSubChipGrp.addView(chip);
                        }
                        //TODO: ADD CHIP HERE
                    }
                });
               /*  if (adapter.getItemCount()<=0){
                    noUserFoundFramLyt.setVisibility(View.VISIBLE);
                }else {
                    noUserFoundFramLyt.setVisibility(View.GONE);
                }*/
                //TODO:CHECK IF THIS WORKS WHEN THE USER BASE IS LARGE
            }

            @NonNull
            @Override
            public SubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(TutorSetupActivity.this).inflate(R.layout.tutor_setup_add_sub_dlg_recy_item_lyt,parent,false);
                /*if (adapter.getItemCount()<=1){
                    noUserFoundFramLyt.setVisibility(View.VISIBLE);
                }else {
                    noUserFoundFramLyt.setVisibility(View.GONE);
                }*/
                return new SubViewHolder(v);
            }
        };
        adapter.startListening();
        /*if (adapter.getItemCount()<=0){
            noUserFoundFramLyt.setVisibility(View.VISIBLE);
        }else {
            noUserFoundFramLyt.setVisibility(View.GONE);
        }*/
        /*LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(CoreAddMemberActivity.this, R.anim.layout_animation_fall_down);
        dlgSubRecyVw.setLayoutAnimation(animation);*/
        dlgSubRecyVw.setAdapter(adapter);


    }


    public class SubViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView textView;
        LinearLayout linearLayout;
        
        public SubViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tutor_setup_add_sub_dlg_recy_item_text_id);
            linearLayout = itemView.findViewById(R.id.tutor_setup_add_sub_dlg_recy_item_lin_lyt_id);            
        }
        
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter!=null){
            adapter.startListening();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (adapter!=null){
            adapter.stopListening();
        }
    }

}
