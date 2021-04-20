package com.albatross.allrounders.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.albatross.allrounders.MAIN.ClassStudentsActivity;
import com.albatross.allrounders.MAIN.CreateClassActivity;
import com.albatross.allrounders.R;
import com.albatross.allrounders.Util.Constants;
import com.albatross.allrounders.Util.GenUserUtilClass;
import com.albatross.allrounders.Util.StudentUtilClass;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyStudentsClassesFragment extends Fragment {
    View v;
    RecyclerView recyclerView;
    Button createClassBtn;
    FrameLayout loadingFramLyt;
    String myUid;
    ClassAdapter adapter;
    DatabaseReference allUserRef;
    ArrayList<String> mAllClassList = new ArrayList<>(),mAllMembList = new ArrayList<>(),mTimeList = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.my_stud_classes_frag_lyt,container,false);
        recyclerView = v.findViewById(R.id.my_stud_classes_frag_recy_vw_id);
        createClassBtn = v.findViewById(R.id.my_stud_classes_frag_add_Class_btn_id);
        loadingFramLyt = v.findViewById(R.id.my_Stud_class_frag_loading_lyt_id);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        allUserRef = FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS);
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getAllClassDetails();


        createClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), CreateClassActivity.class));
            }
        });


        return v;
    }

    private void getAllClassDetails() {
        loadingFramLyt.setVisibility(View.VISIBLE);
        allUserRef.child(myUid).child(Constants.Classes).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot d:dataSnapshot.getChildren()){
                        mAllClassList.add(d.getKey());
                        String count = d.child(Constants.Students).getChildrenCount()+"";
                        mAllMembList.add(count);
                        String times = d.child(Constants.Timestamp).getValue().toString();
                        mTimeList.add(times);
                    }
                    adapter = new ClassAdapter(getContext(),mAllClassList,mAllMembList,mTimeList);
                    recyclerView.setAdapter(adapter);
                    loadingFramLyt.setVisibility(View.GONE);
                }else {
                    Toast.makeText(getContext(),"No classes available",Toast.LENGTH_SHORT).show();
                    loadingFramLyt.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingFramLyt.setVisibility(View.GONE);
            }
        });
    }

    class  ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder>{
        Context context;
        ArrayList<String> classNameList,classMembList,timeList;

        public ClassAdapter(Context context, ArrayList<String> classNameList, ArrayList<String> classMembList, ArrayList<String> timeList) {
            this.context = context;
            this.classNameList = classNameList;
            this.classMembList = classMembList;
            this.timeList = timeList;
        }

        @NonNull
        @Override
        public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.my_stud_class_recy_item_lyt,parent,false);
            return new ClassViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ClassViewHolder holder, final int position) {

            holder.timeTxt.setText(Constants.getdatetimeFromTimestamp(timeList.get(position)));
            holder.nameTxt.setText(classNameList.get(position));
            holder.membTxt.setText(classMembList.get(position)+" Students");
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getContext(), ClassStudentsActivity.class);
                    i.putExtra("CLASS_NAME",classNameList.get(position));
                    startActivity(i);
                }
            });

            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //TODO: DISSOLVE CLASS HERE
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return classNameList.size();
        }

        class ClassViewHolder extends RecyclerView.ViewHolder{

            TextView nameTxt,membTxt,timeTxt;
            CardView cardView;
            public ClassViewHolder(@NonNull View itemView) {
                super(itemView);
                nameTxt = itemView.findViewById(R.id.my_stud_class_recy_item_name_txt);
                membTxt = itemView.findViewById(R.id.my_stud_class_recy_item_memb_txt);
                cardView = itemView.findViewById(R.id.my_stud_class_recy_item_card_vw_txt);
                timeTxt = itemView.findViewById(R.id.my_stud_class_recy_item_time_txt);
            }
        }
    }

}
