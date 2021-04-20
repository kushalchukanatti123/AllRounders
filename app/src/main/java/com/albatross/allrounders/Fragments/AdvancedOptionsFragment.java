package com.albatross.allrounders.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.albatross.allrounders.MAIN.AdvAddDemoLinkActivity;
import com.albatross.allrounders.MAIN.TutorProfileActivity;
import com.albatross.allrounders.R;
import com.albatross.allrounders.Util.Constants;
import com.albatross.allrounders.Util.TutorUserUtilClass;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdvancedOptionsFragment extends Fragment {
    View v;
    FirebaseRecyclerAdapter<TutorUserUtilClass,AdvViewHolder> adapter;
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.adv_frag_lyt,container,false);
        recyclerView = v.findViewById(R.id.adv_opt_recy_vw_id);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        firebaseTutSearch("");
        return v;
    }
    private void firebaseTutSearch(final String text) {

        Query searchQuery = FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS).orderByChild(Constants.Is_Tutor).equalTo(Constants.TRUE);

        /*searchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(getContext(),adapter.getItemCount()+"",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        FirebaseRecyclerOptions<TutorUserUtilClass> options = new FirebaseRecyclerOptions.Builder<TutorUserUtilClass>()
                .setQuery(searchQuery, TutorUserUtilClass.class)
                .build();


        adapter = new FirebaseRecyclerAdapter<TutorUserUtilClass, AdvViewHolder>(options) {
            @NonNull
            @Override
            public AdvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.home_recy_lyt, parent, false);
                return new AdvViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull AdvViewHolder holder, int position, @NonNull final TutorUserUtilClass model) {
                final String uid = this.getSnapshots().getSnapshot(position).getKey();
                holder.nameTxt.setText(model.getName());
                holder.addTxt.setText(model.getAddress_L()+"\n"+model.getAddress_C()+"\n"+model.getAddress_P());
                Glide.with(getContext()).load(model.getProfile_Url()).into(holder.circleImageView);
                Map m = model.getSubjects();
                ArrayList<String> sList = new ArrayList<>();
                sList.addAll(m.keySet());
                holder.chipGroup.removeAllViews();
                for (String s:sList){
                    final Chip chip = new Chip(getContext());
                    chip.setText(s);
                    chip.setClickable(false);
                    chip.setCheckable(false);
                    holder.chipGroup.addView(chip);
                }
                //  currentLocation = HomeFragment.currLocation;
                boolean hasDemo = false;
                final ArrayList<String> demoList= new ArrayList<>();
                if (this.getSnapshots().getSnapshot(position).hasChild(Constants.Demo)){
                    hasDemo = true;
                    for (DataSnapshot d:this.getSnapshots().getSnapshot(position).child(Constants.Demo).getChildren()){
                        demoList.add(d.getKey());
                    }
                }

                final boolean finalHasDemo = hasDemo;

                final Bundle args = new Bundle();
                args.putSerializable("ARRAYLIST",demoList);
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getContext(), AdvAddDemoLinkActivity.class);
                        i.putExtra("DATA",model);
                        i.putExtra("UID",uid);
                        i.putExtra("HAS_DEMO", finalHasDemo);
                        i.putExtra("BUNDLE", args);
                        getContext().startActivity(i);
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

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    class AdvViewHolder extends RecyclerView.ViewHolder{

        CircleImageView circleImageView;
        TextView nameTxt,addTxt,distTxt;
        ChipGroup chipGroup;
        CardView cardView;
        public AdvViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.home_recy_img_id);
            nameTxt = itemView.findViewById(R.id.home_recy_text_id);
            distTxt = itemView.findViewById(R.id.home_recy_dist_id);
            addTxt = itemView.findViewById(R.id.home_recy_add_txt_id);
            chipGroup = itemView.findViewById(R.id.home_recy_chip_grp_id);
            cardView = itemView.findViewById(R.id.home_recy_card_vw_id);
        }
    }

}
