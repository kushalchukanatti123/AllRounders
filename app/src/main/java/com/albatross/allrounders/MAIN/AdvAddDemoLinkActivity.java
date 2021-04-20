package com.albatross.allrounders.MAIN;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.albatross.allrounders.R;
import com.albatross.allrounders.Util.Constants;
import com.albatross.allrounders.Util.GenUserUtilClass;
import com.albatross.allrounders.Util.TutorUserUtilClass;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdvAddDemoLinkActivity extends AppCompatActivity {

    TutorUserUtilClass tutorInfoModel;
    String tutorUID;
    TextView nameTxtVw,descTxtVw,addressTxtVw;
    ChipGroup subjectsChipGrp;
    ImageButton backImgBtn;
    ImageView imageView;

    Dialog dialog;
    EditText dialogEdtxt;
    Button dlgCancelBtn,dlgOkBtn;
    ArrayList<String> prevDemoList;
    RecyclerView demoRecyVw;
    ArrayList<String> allImgIdsList = new ArrayList<>();
    DemoAdapter adapter ;
    ImageButton addLinkBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adv_add_demo_link);

        nameTxtVw = findViewById(R.id.adv_prof_act_name_id);
        descTxtVw = findViewById(R.id.adv_prof_act_desc_id);
        addressTxtVw = findViewById(R.id.adv_prof_act_address_id);
        subjectsChipGrp = findViewById(R.id.adv_prof_act_chip_grp_id);
        backImgBtn = findViewById(R.id.adv_prof_act_back_img_btn_id);
        imageView = findViewById(R.id.adv_prof_act_img_vw_id_id);
        demoRecyVw = findViewById(R.id.adv_Act_demo_recy_vw_id);
        addLinkBtn = findViewById(R.id.adv_prof_act_add_id);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        demoRecyVw.setLayoutManager(linearLayoutManager);

        tutorInfoModel = (TutorUserUtilClass) getIntent().getSerializableExtra("DATA");
        tutorUID = getIntent().getStringExtra("UID");
        if (getIntent().getBooleanExtra("HAS_DEMO",false)){
            Bundle args = getIntent().getBundleExtra("BUNDLE");
            if (args!=null){
                prevDemoList = (ArrayList<String>) args.getSerializable( "ARRAYLIST");

                allImgIdsList.addAll(prevDemoList);
                showDemoVids(allImgIdsList);
            }

        }


        setProfileDetails();
        adapter = new DemoAdapter(allImgIdsList,AdvAddDemoLinkActivity.this);
        demoRecyVw.setAdapter(adapter);
        setDialog();
        addLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });



    }

    private void showDemoVids(ArrayList<String> prevDemoList) {
        DemoAdapter demoAdapter = new DemoAdapter(prevDemoList,AdvAddDemoLinkActivity.this);
        demoRecyVw.setAdapter(demoAdapter);
    }


    private void setDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.adv_add_demo_link_lyt);
        dialogEdtxt = dialog.findViewById(R.id.adv_Ad_dlg_edtxt_id);
        dlgCancelBtn = dialog.findViewById(R.id.adv_Ad_dlg_cancel_id);
        dlgOkBtn = dialog.findViewById(R.id.adv_Ad_dlg_ok_id);

        dlgCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dlgOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String id = dialogEdtxt.getText().toString().trim();
                if (!TextUtils.isEmpty(id)){
                    Map<String,Object> map = new HashMap<>();
                    map.put(id,Constants.TRUE);
                    FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS).child(tutorUID).child(Constants.Demo)
                            .updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                allImgIdsList.add(id);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(AdvAddDemoLinkActivity.this,"Added",Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                            }else {
                                Toast.makeText(AdvAddDemoLinkActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    });
                }
            }
        });

    }

    private void setProfileDetails() {
        Glide.with(AdvAddDemoLinkActivity.this).load(tutorInfoModel.getProfile_Url()).into(imageView);
        nameTxtVw.setText(tutorInfoModel.getName());
        descTxtVw.setText(tutorInfoModel.getT_Desc());
        String add = tutorInfoModel.getAddress_L()+"\n"+tutorInfoModel.getAddress_C()+"\n"+tutorInfoModel.getAddress_P();
        addressTxtVw.setText(add);
        Map m = tutorInfoModel.getSubjects();
        ArrayList<String> sList = new ArrayList<>();
        sList.addAll(m.keySet());
        for (String s : sList) {
            final Chip chip = new Chip(AdvAddDemoLinkActivity.this);
            chip.setText(s);
            chip.setClickable(false);
            chip.setCheckable(false);
            subjectsChipGrp.addView(chip);
        }

        //TODO: CHECK IF ALREADY IS TEACHING AND FOUND IN TUTOR SECTION AND UPDATE THE CONNECT BUTTON //DONE
    }
    public  class DemoAdapter extends RecyclerView.Adapter<DemoAdapter.DemoHolder>{
        ArrayList<String> idList;
        Context context;

        public DemoAdapter(ArrayList<String> idList,Context context) {
            this.idList = idList;
            this.context = context;
        }

        @NonNull
        @Override
        public DemoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.demo_vid_recy_lyt_item,parent,false);
            return new DemoHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final DemoHolder holder, final int position) {

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    Glide.with(context).load("https://img.youtube.com/vi/"+idList.get(position)+"/mqdefault.jpg").into(holder.imageView);

                }
            });
            holder.delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS).child(tutorUID).child(Constants.Demo).child(allImgIdsList.get(position)).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        allImgIdsList.remove(position);
                                        notifyDataSetChanged();
                                        Toast.makeText(AdvAddDemoLinkActivity.this,"Deleted",Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(AdvAddDemoLinkActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            });
        }

        @Override
        public int getItemCount() {
            return idList.size();
        }

        class DemoHolder extends RecyclerView.ViewHolder{

            ImageView imageView;
            ImageButton delBtn;
            public DemoHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.adv_add_demo_recy_item_img_id);
                delBtn = itemView.findViewById(R.id.adv_add_demo_recy_item_del_id);
            }
        }
    }

}
