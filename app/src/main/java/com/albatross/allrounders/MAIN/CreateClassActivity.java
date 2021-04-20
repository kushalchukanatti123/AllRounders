package com.albatross.allrounders.MAIN;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.albatross.allrounders.Fragments.MyStudentsStudentsFragment;
import com.albatross.allrounders.R;
import com.albatross.allrounders.Util.Constants;
import com.albatross.allrounders.Util.GenUserUtilClass;
import com.albatross.allrounders.Util.SendNotificationOnesignal;
import com.albatross.allrounders.Util.StudentUtilClass;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateClassActivity extends AppCompatActivity {

    Dialog classNameDlg;
    EditText clsDlgEdtxt;
    Button clsDlgCancelBtn,clsDlgokBtn;
    FirebaseRecyclerAdapter<StudentUtilClass,CreateClassViewHolder> adapter;
    ChipGroup selectedChipGrp;
    RecyclerView recyclerView;
    String myUid;
    Map<String,String> selectedUidList = new HashMap<>();
    Button addBtn;
    DatabaseReference allUserRef;
    FrameLayout loadingFramLyt;
    String myName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);
        recyclerView = findViewById(R.id.create_class_act_recy_vw_id);
        selectedChipGrp = findViewById(R.id.create_class_act_selected_chipgrp_id);
        addBtn = findViewById(R.id.create_class_act_submit_id);
        loadingFramLyt = findViewById(R.id.create_class_Act_loading_fram_id);

        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        allUserRef = FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        firebaseSearch();
        setClassDialog();
        allUserRef.child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    myName = dataSnapshot.child("Name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedUidList.size()==0){
                    Toast.makeText(CreateClassActivity.this,"No student selected",Toast.LENGTH_SHORT).show();
                    return;
                }
                classNameDlg.show();
            }
        });
    }
    private void firebaseSearch() {

        Query searchQuery = FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS).child(myUid).child(Constants.My_Students);

        /*searchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(getContext(),adapter.getItemCount()+"",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        FirebaseRecyclerOptions<StudentUtilClass> options = new FirebaseRecyclerOptions.Builder<StudentUtilClass>()
                .setQuery(searchQuery, StudentUtilClass.class)
                .build();



        adapter = new FirebaseRecyclerAdapter<StudentUtilClass, CreateClassViewHolder >(options) {
            @NonNull
            @Override
            public CreateClassViewHolder  onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(CreateClassActivity.this).inflate(R.layout.my_stud_stud_recy_item_lyt,parent,false);
                return new CreateClassViewHolder (v);
            }

            @Override
            protected void onBindViewHolder(@NonNull final CreateClassViewHolder  holder, int position, @NonNull StudentUtilClass model) {
                final String suid = this.getSnapshots().getSnapshot(position).getKey();
                final String[] name = {null};

                holder.delbtn.setVisibility(View.GONE);
                final GenUserUtilClass[] genUserUtilClass = new GenUserUtilClass[1];
                FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS).child(suid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            genUserUtilClass[0] = dataSnapshot.getValue(GenUserUtilClass.class);
                            holder.nameTxt.setText(genUserUtilClass[0].getName());
                           name[0] = genUserUtilClass[0].getName();
                            Glide.with(CreateClassActivity.this).load(genUserUtilClass[0].getProfile_Url()).into(holder.imageView);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                holder.timeTxt.setText(Constants.getdatetimeFromTimestamp(model.getTimestamp()));
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       if (name[0] !=null){
                           final Chip chip = new Chip(CreateClassActivity.this);
                           //  chip.setText(model.getName());
                           chip.setCloseIconVisible(true);
                           chip.setText(name[0]);
                           chip.setClickable(false);
                           chip.setCheckable(false);
                           chip.setOnCloseIconClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View view) {
                                   selectedChipGrp.removeView(view);
                                   selectedUidList.remove(suid);

                                   if (selectedUidList.size()==0){
                                       selectedChipGrp.setVisibility(View.GONE);
                                   }
                               }
                           });
                           if (!selectedUidList.containsKey(suid)){
                               selectedUidList.put(suid, genUserUtilClass[0].getNotif_Key());
                               selectedChipGrp.setVisibility(View.VISIBLE);
                               selectedChipGrp.addView(chip);
                           }
                       }
                    }
                });
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
        recyclerView.setAdapter(adapter);
    }

    public class CreateClassViewHolder extends RecyclerView.ViewHolder{


        TextView nameTxt,timeTxt,notAssTxt;
        CircleImageView imageView;
        CardView cardView;
        ImageButton delbtn;


        public CreateClassViewHolder (@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.my_stud_stud_recy_item_recy_card_vw_id);
            imageView = itemView.findViewById(R.id.my_stud_stud_recy_item_img_vw_id);
            nameTxt = itemView.findViewById(R.id.my_stud_stud_recy_item_name_txt_id);
            timeTxt = itemView.findViewById(R.id.my_stud_stud_recy_item_time_txt_id);
            delbtn = itemView.findViewById(R.id.my_stud_stud_recy_item_del_btn_id);
            notAssTxt = itemView.findViewById(R.id.my_stud_stud_recy_item_not_Assigned_txt_id);
        }

    }
    private void setClassDialog() {
        classNameDlg = new Dialog(CreateClassActivity.this);
        classNameDlg.setContentView(R.layout.tutor_class_name_dlg_lyt);
        clsDlgEdtxt = classNameDlg.findViewById(R.id.tutor_cls_name_edtxt_id);
        clsDlgCancelBtn = classNameDlg.findViewById(R.id.tutor_cls_name_cancel_btn_id);
        clsDlgokBtn = classNameDlg.findViewById(R.id.tutor_cls_name_ok_btn_id);

        clsDlgokBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: CREATE CLASS
                String className = clsDlgEdtxt.getText().toString();
                createClass(className);
                classNameDlg.dismiss();
            }
        });
    }

    private void createClass(final String className) {
        Map<String,Object> cMap = new HashMap<>();
        Map<String,Object> tMap = new HashMap<>();
        tMap.put("Timestamp",Constants.getCurrentTimestamp());
        cMap.put("Timestamp",Constants.getCurrentTimestamp());
        Map<String,Object> smap = new HashMap<>();
        Map<String,Object> umap = new HashMap<>();
        for (String s:selectedUidList.keySet()){
            umap.put(s,tMap);
        }
        cMap.put(Constants.Students,umap);
        //TODO: CHECK FOR DUPLICATE CLASS NAMES
        loadingFramLyt.setVisibility(View.VISIBLE);
        allUserRef.child(myUid).child(Constants.Classes).child(className).updateChildren(cMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful()){
                           Map<String,Object> cNameMap = new HashMap<>();
                           cNameMap.put(className,Constants.TRUE);
                           final int[] count = {0};
                           ArrayList<String> tempUidSet = new ArrayList<>();
                           tempUidSet.addAll(selectedUidList.keySet());
                           for (int i=0;i<selectedUidList.size();i++){
                               allUserRef.child(myUid).child(Constants.My_Students).child(tempUidSet.get(i)).child(Constants.Classes)
                                       .updateChildren(cNameMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if (task.isSuccessful()){
                                           count[0]++;
                                           if (count[0] ==selectedUidList.size()){
                                               Toast.makeText(CreateClassActivity.this,"Classs created successfully",Toast.LENGTH_SHORT).show();
                                               //Collect all notifIds and send notification here
                                               final ArrayList<String> keyList = new ArrayList<>(selectedUidList.values());
                                               if (myName!=null){
                                                   String msg = myName+" added you to class "+className;
                                                   new SendNotificationOnesignal(className,msg,keyList,Constants.N_CLASS_ACT);
                                               }else {
                                                   allUserRef.child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                       @Override
                                                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                           if (dataSnapshot.exists()) {
                                                               myName = dataSnapshot.child("Name").getValue().toString();
                                                               String msg = myName+" added you to class "+className;
                                                               new SendNotificationOnesignal(className,msg,keyList,Constants.N_CLASS_ACT);
                                                           }
                                                       }

                                                       @Override
                                                       public void onCancelled(@NonNull DatabaseError databaseError) {
                                                           Toast.makeText(CreateClassActivity.this,"Couldn't send notification",Toast.LENGTH_SHORT).show();

                                                       }
                                                   });
                                               }

                                               selectedUidList.clear();
                                               selectedChipGrp.removeAllViews();
                                               loadingFramLyt.setVisibility(View.GONE);
                                           }
                                       }else {
                                           loadingFramLyt.setVisibility(View.GONE);

                                       }
                                   }
                               });
                           }
                       }else {
                           loadingFramLyt.setVisibility(View.GONE);

                       }
                    }
                });
    }

}
