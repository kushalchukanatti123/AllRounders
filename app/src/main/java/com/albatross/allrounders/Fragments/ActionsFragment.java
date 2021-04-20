package com.albatross.allrounders.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.albatross.allrounders.R;
import com.albatross.allrounders.Util.ActionsRequestsUtilCLass;
import com.albatross.allrounders.Util.Constants;
import com.albatross.allrounders.Util.GenUserUtilClass;
import com.albatross.allrounders.Util.GeneralNotifUtilClass;
import com.albatross.allrounders.Util.RequestNotifUtilClass;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActionsFragment extends Fragment {
    View v;
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<ActionsRequestsUtilCLass,ActionsViewHolder> adapter;
    String myUid ;
    DatabaseReference allUserRef;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.actions_frag_lyt,container,false);
        recyclerView = v.findViewById(R.id.actions_frag_Recy_vw);

        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        allUserRef = FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        firebaseSearch();

        return v;
    }
    private void firebaseSearch() {

        Query searchQuery = FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS).child(myUid).child(Constants.ACTIONS).child(Constants.Requests);

        /*searchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(getContext(),adapter.getItemCount()+"",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        FirebaseRecyclerOptions<ActionsRequestsUtilCLass> options = new FirebaseRecyclerOptions.Builder<ActionsRequestsUtilCLass>()
                .setQuery(searchQuery, ActionsRequestsUtilCLass.class)
                .build();



        adapter = new FirebaseRecyclerAdapter<ActionsRequestsUtilCLass, ActionsViewHolder>(options) {
            @NonNull
            @Override
            public ActionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.actions_recy_item_lyt,parent,false);
                return new ActionsViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ActionsViewHolder holder, int position, @NonNull final ActionsRequestsUtilCLass model) {
                final String tuid = this.getSnapshots().getSnapshot(position).getKey();

                holder.timeTxt.setText(Constants.getdatetimeFromTimestamp(model.getTimestamp()));

                FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS).child(tuid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            GenUserUtilClass genUserUtilClass = dataSnapshot.getValue(GenUserUtilClass.class);
                            holder.nameTxt.setText(genUserUtilClass.getName());
                            Glide.with(getContext()).load(genUserUtilClass.getProfile_Url()).into(holder.imageView);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                if (model.getType().equals(Constants.Connection)){
                    holder.statusTxt.setText(model.getStatus());
                    if (model.getStatus().equals(Constants.DECLINED)){
                        holder.cancelBtn.setVisibility(View.GONE);
                    }else {
                        holder.cancelBtn.setVisibility(View.VISIBLE);
                    }
                    holder.infoTxt.setText("Requested for Connection");
                    //TODO:DELETING AN ACTION
                    holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            if (model.getStatus().equals(Constants.DECLINED)){
                                allUserRef.child(myUid).child(Constants.ACTIONS).child(Constants.Requests).child(tuid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(getContext(),"cancelled request",Toast.LENGTH_SHORT).show();
                                        }   else {

                                        }
                                    }
                                });
                            }else {
                                Toast.makeText(getContext(),"Please decline a request before deleting",Toast.LENGTH_SHORT).show();

                            }
                            return true;
                        }
                    });
                    holder.timeTxt.setText(Constants.getdatetimeFromTimestamp(model.getTimestamp()));
                    holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            allUserRef.child(tuid).child(Constants.Notifications).child(Constants.Requests).child(myUid).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            allUserRef.child(myUid).child(Constants.ACTIONS).child(Constants.Requests).child(tuid).removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(getContext(),"cancelled request",Toast.LENGTH_SHORT).show();
                                                        }
                                                    })      ;
                                        }
                                    });
                        }
                    });
                }else {
                    //TODO: HE IS ASKING FOR A DISCONNECTION
                    holder.statusTxt.setText(model.getStatus());
                    holder.infoTxt.setText("Requested for Disonnection");

                    holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            allUserRef.child(tuid).child(Constants.Notifications).child(Constants.Requests).child(myUid).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            allUserRef.child(myUid).child(Constants.ACTIONS).child(Constants.Requests).child(tuid).removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(getContext(),"cancelled disconnection request",Toast.LENGTH_SHORT).show();
                                                        }
                                                    })      ;
                                        }
                                    });
                        }
                    });


                }


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

    public class ActionsViewHolder extends RecyclerView.ViewHolder{

        TextView nameTxt,timeTxt,statusTxt,infoTxt;
        Button cancelBtn;
        CircleImageView imageView;
        CardView cardView;


        public ActionsViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.act_recy_item_lyt_name_txt_id);
            timeTxt = itemView.findViewById(R.id.act_recy_item_lyt_time_id);
            cancelBtn = itemView.findViewById(R.id.act_recy_item_lyt_cancel_btn_id);
            imageView = itemView.findViewById(R.id.act_recy_item_lyt_image_id);
            cardView = itemView.findViewById(R.id.act_recy_item_lyt_card_vw_id);
            statusTxt = itemView.findViewById(R.id.act_recy_item_lyt_status_id);
            infoTxt = itemView.findViewById(R.id.act_recy_item_lyt_info_id);
        }

    }
}
