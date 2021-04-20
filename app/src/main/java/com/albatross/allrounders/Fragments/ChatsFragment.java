package com.albatross.allrounders.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.albatross.allrounders.MAIN.ChatActivity;
import com.albatross.allrounders.R;
import com.albatross.allrounders.Util.AllChatsUtilClass;
import com.albatross.allrounders.Util.Constants;
import com.albatross.allrounders.Util.GenUserUtilClass;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsFragment extends Fragment {
    View v;
    FirebaseRecyclerAdapter<AllChatsUtilClass,ChatFragViewHolder> adapter;
    String myUid;
    RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.chat_frag_lyt,container,false);
        recyclerView = v.findViewById(R.id.chat_frag_recy_vw_id);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firebaseSearch();
        return v;
    }
    private void firebaseSearch() {

        Query searchQuery = FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS).child(myUid).child(Constants.ALL_CHATS);


        /*searchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(getContext(),adapter.getItemCount()+"",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        FirebaseRecyclerOptions<AllChatsUtilClass> options = new FirebaseRecyclerOptions.Builder<AllChatsUtilClass>()
                .setQuery(searchQuery, AllChatsUtilClass.class)
                .build();



        adapter = new FirebaseRecyclerAdapter<AllChatsUtilClass, ChatFragViewHolder>(options) {
            @NonNull
            @Override
            public ChatFragViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.all_chats_recy_item_lyt,parent,false);
                return new ChatFragViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ChatFragViewHolder holder, int position, @NonNull AllChatsUtilClass model) {

                final GenUserUtilClass[] genUserUtilClass = new GenUserUtilClass[1];
                final String uid = this.getSnapshots().getSnapshot(position).getKey();
                String ls = model.getLS();
                holder.timeTxt.setText(Constants.getdatetimeFromTimestamp(ls));
                FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            genUserUtilClass[0] = dataSnapshot.getValue(GenUserUtilClass.class);
                            holder.nameTxt.setText(genUserUtilClass[0].getName());
                            Glide.with(getContext()).load(genUserUtilClass[0].getProfile_Url()).into(holder.imageView);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getContext(),ChatActivity.class);
                        i.putExtra("UID",uid);
                        i.putExtra("DATA", genUserUtilClass[0]);
                        startActivity(i);
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

    public class ChatFragViewHolder extends RecyclerView.ViewHolder{


        TextView nameTxt,timeTxt;
        CircleImageView imageView;
        LinearLayout linearLayout;


        public ChatFragViewHolder (@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.all_chat_recy_item_img_id);
            timeTxt = itemView.findViewById(R.id.all_chat_recy_item_last_seen_id);
            nameTxt = itemView.findViewById(R.id.all_chat_recy_item_name_id);
            linearLayout = itemView.findViewById(R.id.all_chat_recy_item_lin_lyt_id);

        }

    }
}
