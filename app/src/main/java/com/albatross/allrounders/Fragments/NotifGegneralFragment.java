package com.albatross.allrounders.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.albatross.allrounders.R;
import com.albatross.allrounders.Util.Constants;
import com.albatross.allrounders.Util.GeneralNotifUtilClass;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class NotifGegneralFragment extends Fragment {
    View v;
    String myUid;
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<GeneralNotifUtilClass,NotifGenViewHolder> adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.notif_general_frag_lyt,container,false);
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        recyclerView = v.findViewById(R.id.notif_gen_frag_recy_vw_id);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);

        firebaseSearch();
        return v;
    }
    private void firebaseSearch() {

        Query searchQuery = FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS).child(myUid).child(Constants.Notifications).child(Constants.General);

        /*searchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(getContext(),adapter.getItemCount()+"",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        FirebaseRecyclerOptions<GeneralNotifUtilClass> options = new FirebaseRecyclerOptions.Builder<GeneralNotifUtilClass>()
                .setQuery(searchQuery, GeneralNotifUtilClass.class)
                .build();



        adapter = new FirebaseRecyclerAdapter<GeneralNotifUtilClass, NotifGenViewHolder>(options) {
            @NonNull
            @Override
            public NotifGenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.gen_notif_recy_item_lyt,parent,false);
                return new NotifGenViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull NotifGenViewHolder holder, int position, @NonNull GeneralNotifUtilClass model) {
               // final String subject = this.getSnapshots().getSnapshot(position).getKey();
                holder.timeTxt.setText(Constants.getdatetimeFromTimestamp(model.getTimestamp()));
                holder.msgtxt.setText(model.getMsg());
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

    public class NotifGenViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView msgtxt,timeTxt;


        public NotifGenViewHolder(@NonNull View itemView) {
            super(itemView);
            msgtxt = itemView.findViewById(R.id.gen_notif_item_lyt_msg_txt_id);
            timeTxt = itemView.findViewById(R.id.gen_notif_item_lyt_time_txt_id);
        }

    }

}
