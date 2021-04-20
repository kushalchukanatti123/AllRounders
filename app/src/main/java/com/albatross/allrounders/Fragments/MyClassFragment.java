package com.albatross.allrounders.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.albatross.allrounders.MAIN.MainActivity;
import com.albatross.allrounders.MAIN.StudentProfileActivity;
import com.albatross.allrounders.MAIN.TutorProfileActivity;
import com.albatross.allrounders.MAIN.TutorSetupActivity;
import com.albatross.allrounders.MAIN.ZeroActivity;
import com.albatross.allrounders.R;
import com.albatross.allrounders.Util.Constants;
import com.albatross.allrounders.Util.GenUserUtilClass;
import com.albatross.allrounders.Util.GeneralNotifUtilClass;
import com.albatross.allrounders.Util.MyClassStudentsUtilClass;
import com.albatross.allrounders.Util.StudentUtilClass;
import com.albatross.allrounders.Util.TutorUserUtilClass;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
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
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyClassFragment extends Fragment {

    View v;
    String myUid;
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<MyClassStudentsUtilClass,MyClassVH> adapter;
    DatabaseReference allUserRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.my_class_frag_lyt,container,false);
        recyclerView = v.findViewById(R.id.my_Class_frag_recy_vw_id);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        allUserRef = FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS);
        firebaseSearch();

        return v;
    }

    private void firebaseSearch() {

        Query searchQuery = FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS).child(myUid).child(Constants.My_Tutors).orderByChild(Constants.Timestamp);
        

        /*searchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(getContext(),adapter.getItemCount()+"",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        FirebaseRecyclerOptions<MyClassStudentsUtilClass> options = new FirebaseRecyclerOptions.Builder<MyClassStudentsUtilClass>()
                .setQuery(searchQuery, MyClassStudentsUtilClass.class)
                .build();



        adapter = new FirebaseRecyclerAdapter<MyClassStudentsUtilClass, MyClassVH>(options) {
            @NonNull
            @Override
            public MyClassVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.my_class_frag_recy_lyt,parent,false);
                return new MyClassVH(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull final MyClassVH holder, int position, @NonNull final MyClassStudentsUtilClass model) {
                final String suid = this.getSnapshots().getSnapshot(position).getKey();

                final TutorUserUtilClass[] tutorUserUtilClasses = new TutorUserUtilClass[1];

                FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS).child(suid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            DataSnapshot cSnap = dataSnapshot.child(Constants.My_Students).child(myUid);
                            if (cSnap.hasChild(Constants.Classes)){
                                for (DataSnapshot d:cSnap.child(Constants.Classes).getChildren()){
                                    String cls = d.getKey();
                                    if (!cls.equals(Constants.NULL)){
                                        final Chip chip = new Chip(getContext());
                                        //  chip.setText(model.getName());
                                        chip.setCloseIconVisible(false);
                                        chip.setClickable(false);
                                        chip.setCheckable(false);

                                        chip.setText(cls);
                                        holder.chipGroup.addView(chip);
                                    }
                                }
                            }
                            tutorUserUtilClasses[0] = dataSnapshot.getValue(TutorUserUtilClass.class);
                            holder.nameTxt.setText(tutorUserUtilClasses[0].getName());
                            Glide.with(getContext()).load(tutorUserUtilClasses[0].getProfile_Url()).into(holder.imageView);
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
                        Intent intent = new Intent(getContext(), TutorProfileActivity.class);
                        intent.putExtra("DATA",tutorUserUtilClasses[0]);
                        intent.putExtra("UID",suid);
                        startActivity(intent);
                    }
                });

                holder.delBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                        dialog.setMessage("You will lose contact with the teacher, are you sure to proceed with this operation?");
                        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                                                        //ADDING TO MY STUDENTS SECTION
                                        allUserRef.child(myUid).child(Constants.My_Tutors).child(suid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    allUserRef.child(myUid).child(Constants.Notifications).child(Constants.Requests).child(suid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                removeFromTutorSide(myUid,suid);
                                                            }
                                                        }
                                                    }) ;
                                                }
                                            }
                                        });


                            }
                        });
                        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        dialog.show();
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
    private void removeFromTutorSide(final String myUid, final String tuid) {
        allUserRef.child(tuid).child(Constants.My_Students).child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    StudentUtilClass studentUtilClass = dataSnapshot.getValue(StudentUtilClass.class);
                    Map<String,Object> classMap = studentUtilClass.getClasses();
                    final ArrayList<String> classList = new ArrayList<>(classMap.keySet());
                    final int[] count = {0};
                    for (String s:classList){
                        DatabaseReference classRef = allUserRef.child(tuid).child(Constants.Classes).child(s).child(Constants.Students).child(myUid);
                        classRef.removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            count[0]++;
                                            if (count[0] ==classList.size()){
                                                Toast.makeText(getContext(),"Removed from all classes",Toast.LENGTH_SHORT).show();
                                                allUserRef.child(tuid).child(Constants.My_Students).child(myUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            Toast.makeText(getContext(),"Successfully disconnected",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public class MyClassVH extends RecyclerView.ViewHolder{

        TextView nameTxt,timeTxt;
        CircleImageView imageView;
        CardView cardView;
        ChipGroup chipGroup;
        ImageButton delBtn;


        public MyClassVH(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.my_cls_recy_name_id);
            timeTxt = itemView.findViewById(R.id.my_cls_recy_time_id);
            delBtn = itemView.findViewById(R.id.my_cls_recy_del_img_btn_id);
            chipGroup = itemView.findViewById(R.id.my_cls_recy_chip_grp_id);
            imageView = itemView.findViewById(R.id.my_cls_recy_img_id);
            cardView = itemView.findViewById(R.id.my_cls_recy_card_vw_id);
        }

    }

}
