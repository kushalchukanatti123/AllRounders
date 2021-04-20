package com.albatross.allrounders.MAIN;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.albatross.allrounders.Fragments.MyStudentsStudentsFragment;
import com.albatross.allrounders.R;
import com.albatross.allrounders.Util.Constants;
import com.albatross.allrounders.Util.GenUserUtilClass;
import com.albatross.allrounders.Util.MyClassStudentsUtilClass;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClassStudentsActivity extends AppCompatActivity {

    
    String className;
    String myUid;
    FirebaseRecyclerAdapter<MyClassStudentsUtilClass,CStudViewHolder> adapter;
    TextView cnameTxt;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_students);
        cnameTxt = findViewById(R.id.class_stud_clas_name_txt_id);
        recyclerView = findViewById(R.id.class_stud_Recy_vw_id);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        className = getIntent().getStringExtra("CLASS_NAME");
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        cnameTxt.setText(className);
        firebaseSearch();

    }
    private void firebaseSearch() {

        Query searchQuery = FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS).child(myUid).child(Constants.Classes).child(className).child(Constants.Students);

        /*searchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(ClassStudentsActivity.this(),adapter.getItemCount()+"",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        FirebaseRecyclerOptions<MyClassStudentsUtilClass> options = new FirebaseRecyclerOptions.Builder<MyClassStudentsUtilClass>()
                .setQuery(searchQuery, MyClassStudentsUtilClass.class)
                .build();



        adapter = new FirebaseRecyclerAdapter<MyClassStudentsUtilClass, CStudViewHolder>(options) {
            @NonNull
            @Override
            public CStudViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(ClassStudentsActivity.this).inflate(R.layout.my_stud_stud_recy_item_lyt,parent,false);
                return new CStudViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull final CStudViewHolder holder, int position, @NonNull MyClassStudentsUtilClass model) {
                final String suid = this.getSnapshots().getSnapshot(position).getKey();
                final GenUserUtilClass[] genUserUtilClass = new GenUserUtilClass[1];

                holder.delBtn.setVisibility(View.GONE);

                FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS).child(suid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            genUserUtilClass[0] = dataSnapshot.getValue(GenUserUtilClass.class);
                            holder.nameTxt.setText(genUserUtilClass[0].getName());
                            Glide.with(ClassStudentsActivity.this).load(genUserUtilClass[0].getProfile_Url()).into(holder.imageView);
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
                        Intent i = new Intent(ClassStudentsActivity.this,StudentProfileActivity.class);
                        i.putExtra("DATA", genUserUtilClass[0]);
                        i.putExtra("UID",suid);
                        startActivity(i);
                    }
                });
                /*if (model.getClasses().size()<=1){
                    holder.classChipGrp.setVisibility(View.GONE);
                    holder.notAssTxt.setVisibility(View.VISIBLE);
                }else {*/

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

    public class CStudViewHolder extends RecyclerView.ViewHolder{


        TextView nameTxt,timeTxt;
        CircleImageView imageView;
        ImageButton delBtn;
        
        CardView cardView;


        public CStudViewHolder (@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.my_stud_stud_recy_item_recy_card_vw_id);
            imageView = itemView.findViewById(R.id.my_stud_stud_recy_item_img_vw_id);
            nameTxt = itemView.findViewById(R.id.my_stud_stud_recy_item_name_txt_id);
            timeTxt = itemView.findViewById(R.id.my_stud_stud_recy_item_time_txt_id);
            delBtn = itemView.findViewById(R.id.my_stud_stud_recy_item_del_btn_id);
        }

    }
}
