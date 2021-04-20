package com.albatross.allrounders.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.albatross.allrounders.MAIN.ClassStudentsActivity;
import com.albatross.allrounders.MAIN.MainActivity;
import com.albatross.allrounders.MAIN.StudentProfileActivity;
import com.albatross.allrounders.MAIN.TutorProfileActivity;
import com.albatross.allrounders.MAIN.ZeroActivity;
import com.albatross.allrounders.R;
import com.albatross.allrounders.Util.Constants;
import com.albatross.allrounders.Util.GenUserUtilClass;
import com.albatross.allrounders.Util.GeneralNotifUtilClass;
import com.albatross.allrounders.Util.StudentUtilClass;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyStudentsStudentsFragment extends Fragment {
    View v;
    RecyclerView recyclerView;
    String myUid;
    FirebaseRecyclerAdapter<StudentUtilClass,StudViewHolder> adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.my_stud_stud_frag_lyt,container,false);
        recyclerView = v.findViewById(R.id.my_stud_Stud_frag_recy_vw_id);

        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        firebaseSearch();

        return v;
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



        adapter = new FirebaseRecyclerAdapter<StudentUtilClass, StudViewHolder>(options) {
            @NonNull
            @Override
            public StudViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.my_stud_stud_recy_item_lyt,parent,false);
                return new StudViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull final StudViewHolder holder, int position, @NonNull StudentUtilClass model) {
                final String suid = this.getSnapshots().getSnapshot(position).getKey();
                final GenUserUtilClass[] genUserUtilClass = new GenUserUtilClass[1];
                FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS).child(suid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            genUserUtilClass[0] = dataSnapshot.getValue(GenUserUtilClass.class);
                            holder.nameTxt.setText(genUserUtilClass[0].getName());
                            try {
                                Glide.with(getContext()).load(genUserUtilClass[0].getProfile_Url()).into(holder.imageView);
                            }catch (Exception e){

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getContext(), StudentProfileActivity.class);
                        i.putExtra("DATA", genUserUtilClass[0]);
                        i.putExtra("UID",suid);
                        startActivity(i);
                    }
                });

                holder.delBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO: SEND A DELETION REQUEST AND ADD TO MY ACTIONS TOO
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                        dialog.setMessage("Sure to send a disconnection request?");
                        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                              //DELETE HERE
                                final Map<String,Object> reqMap = new HashMap<>();
                                Map<String,Object> map = new HashMap<>();
                                map.put("Timestamp",Constants.getCurrentTimestamp());
                                map.put(Constants.Type,Constants.DisConnection);
                                reqMap.put(myUid,map);

                                final Map<String,Object> actionsMap = new HashMap<>();
                                Map<String,Object> map2 = new HashMap<>();
                                map2.put("Timestamp",Constants.getCurrentTimestamp());
                                map2.put("Status",Constants.PENDING);
                                map2.put(Constants.Type,Constants.DisConnection);
                                actionsMap.put(suid,map2);

                                FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS).child(suid).child(Constants.Notifications).child(Constants.Requests)
                                        .updateChildren(reqMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS).child(myUid).child(Constants.ACTIONS).child(Constants.Requests).updateChildren(actionsMap)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){

                                                                Toast.makeText(getContext(),"A request has been sent asking  to disconnect",Toast.LENGTH_SHORT).show();
                                                            }else {
                                                                Toast.makeText(getContext(),task.getException().toString(),Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }else {
                                            Toast.makeText(getContext(),task.getException().toString(),Toast.LENGTH_SHORT).show();
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

                try {
                    holder.timeTxt.setText(Constants.getdatetimeFromTimestamp(model.getTimestamp()));
                }catch (Exception e){

                }
                /*if (model.getClasses().size()<=1){
                    holder.classChipGrp.setVisibility(View.GONE);
                    holder.notAssTxt.setVisibility(View.VISIBLE);
                }else {*/
                    holder.classChipGrp.setVisibility(View.VISIBLE);
                    holder.notAssTxt.setVisibility(View.GONE);
                    Map m = model.getClasses();
                    ArrayList<String> sList = new ArrayList<>();
                    sList.addAll(m.keySet());
                    for (String s : sList) {
                        final Chip chip = new Chip(getContext());
                        chip.setText(s);
                        chip.setClickable(false);
                        chip.setCheckable(false);
                        if (!s.equals(Constants.NULL)){
                            holder.classChipGrp.addView(chip);
                        }
                    }

               // }
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

    public class StudViewHolder extends RecyclerView.ViewHolder{


        TextView nameTxt,timeTxt,notAssTxt;
        CircleImageView imageView;
        ChipGroup classChipGrp;
        CardView cardView;
        ImageButton delBtn;


        public StudViewHolder (@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.my_stud_stud_recy_item_recy_card_vw_id);
            classChipGrp = itemView.findViewById(R.id.my_stud_stud_recy_item_subs_chip_grp_id);
            imageView = itemView.findViewById(R.id.my_stud_stud_recy_item_img_vw_id);
            nameTxt = itemView.findViewById(R.id.my_stud_stud_recy_item_name_txt_id);
            timeTxt = itemView.findViewById(R.id.my_stud_stud_recy_item_time_txt_id);
            notAssTxt = itemView.findViewById(R.id.my_stud_stud_recy_item_not_Assigned_txt_id);
            delBtn = itemView.findViewById(R.id.my_stud_stud_recy_item_del_btn_id);
        }

    }
}
