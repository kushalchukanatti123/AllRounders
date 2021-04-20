package com.albatross.allrounders.MAIN;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.albatross.allrounders.R;
import com.albatross.allrounders.Util.GenUserUtilClass;
import com.bumptech.glide.Glide;

public class StudentProfileActivity extends AppCompatActivity {

    ImageView imageView;
    TextView nameTxt,addTxt;
    EditText editText;
    ImageButton sendBtn;
    GenUserUtilClass userDataClass;
    String toUid;
    ImageButton backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        imageView = findViewById(R.id.student_prof_act_img_vw_id_id);
        nameTxt = findViewById(R.id.student_prof_act_name_id);
        addTxt = findViewById(R.id.student_prof_act_address_id);
        editText = findViewById(R.id.student_prof_act_query_edtxt_id);
        sendBtn = findViewById(R.id.student_prof_act_send_query_img_btn_id);
        backBtn = findViewById(R.id.student_prof_act_back_img_btn_id);

        userDataClass = (GenUserUtilClass) getIntent().getSerializableExtra("DATA");
        toUid = getIntent().getStringExtra("UID");

        Glide.with(StudentProfileActivity.this).load(userDataClass.getProfile_Url()).into(imageView);
        nameTxt.setText(userDataClass.getName());
        addTxt.setText(userDataClass.getAddress_L()+userDataClass.getAddress_C()+userDataClass.getAddress_P());

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(StudentProfileActivity.this,ChatActivity.class);
                intent.putExtra("UID",toUid);
                String message = editText.getText().toString();
                if (!TextUtils.isEmpty(message)){
                    intent.putExtra("MESSAGE",message);
                }
                //TODO:SEND TUTOR MODEL HERE
                if (userDataClass!=null){
                    intent.putExtra("DATA",userDataClass);
                    startActivity(intent);
                }

            }
        });

    }
}
