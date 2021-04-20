package com.albatross.allrounders.MAIN;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.albatross.allrounders.Fragments.MyStudentsStudentsFragment;
import com.albatross.allrounders.R;
import com.albatross.allrounders.Util.ChatUtilClass;
import com.albatross.allrounders.Util.Constants;
import com.albatross.allrounders.Util.GenUserUtilClass;
import com.albatross.allrounders.Util.SendNotificationOnesignal;
import com.albatross.allrounders.Util.StudentUtilClass;
import com.bumptech.glide.Glide;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String myUid, toUid;
    RecyclerView recyclerView;
    EditText editText;
    ImageButton sendBtn;
    FirebaseRecyclerAdapter<ChatUtilClass, ChatViewHolder> adapter;
    DatabaseReference allUserRef;
    String intentMsg;
    GenUserUtilClass userData;
    Button blockBtn;
    String uBlocked;
    CircleImageView imageView;
    TextView nameTxt;
    GenUserUtilClass myDataClass;
    ImageButton backBtn;
    FrameLayout progLyt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.chat_act_recy_vw_id);
        editText = findViewById(R.id.chat_act_edtxt_id);
        sendBtn = findViewById(R.id.chat_act_send_btn_id);
        blockBtn = findViewById(R.id.chat_act_block_btn_id);
        imageView = findViewById(R.id.chat_act_img_id);
        nameTxt = findViewById(R.id.chat_act_name_txt_id);
        backBtn = findViewById(R.id.chat_Act_back_btn_id);
        progLyt = findViewById(R.id.chat_act_prog_lyt_id);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);


        toUid = getIntent().getStringExtra("UID");
        if (getIntent().hasExtra("MESSAGE")) {
            intentMsg = getIntent().getStringExtra("MESSAGE");
            editText.setText(intentMsg);
        }
        if (getIntent().hasExtra("DATA")) {
            userData = (GenUserUtilClass) getIntent().getSerializableExtra("DATA");
            Glide.with(ChatActivity.this).load(userData.getProfile_Url()).into(imageView);
            nameTxt.setText(userData.getName());
        }


        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        allUserRef = FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS);

        allUserRef.child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    myDataClass = dataSnapshot.getValue(GenUserUtilClass.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        firebaseSearch();

        allUserRef.child(myUid).child(Constants.ALL_CHATS).child(toUid).child(Constants.isBlocked).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    uBlocked = dataSnapshot.getValue().toString();
                    if (uBlocked.equals(Constants.TRUE)) {
                        blockBtn.setText(Constants.UNBLOCK);
                    } else {
                        blockBtn.setText(Constants.BLOCK);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editText.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    return;
                }
                sendMessage(message, false);
            }
        });

        blockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (blockBtn.getText().equals(Constants.BLOCK)) {
                    allUserRef.child(myUid).child(Constants.ALL_CHATS).child(toUid).child(Constants.isBlocked).setValue(Constants.TRUE).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //NOTIFYING THE BLOCK
                            String bMsg = Constants.You_have_been_blocked + " " + myDataClass.getName() + "~";
                            sendMessage(Constants.You_have_been_blocked, true);
                            new SendNotificationOnesignal(myDataClass.getName() + " blocked you", bMsg, userData.getNotif_Key(), Constants.N_CHAT_ACT);

                            Toast.makeText(ChatActivity.this, "User blocked successfully", Toast.LENGTH_SHORT).show();
                            uBlocked = Constants.TRUE;
                            blockBtn.setText(Constants.UNBLOCK);
                        }
                    });
                } else if (blockBtn.getText().equals(Constants.UNBLOCK)) {
                    allUserRef.child(myUid).child(Constants.ALL_CHATS).child(toUid).child(Constants.isBlocked).setValue(Constants.FALSE).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String bMsg = Constants.You_have_been_unblocked + " " + myDataClass.getName() + "~";
                            sendMessage(Constants.You_have_been_unblocked, true);
                            new SendNotificationOnesignal(myDataClass.getName() + " unblocked you", bMsg, userData.getNotif_Key(), Constants.N_CHAT_ACT);

                            Toast.makeText(ChatActivity.this, "User un blocked successfully", Toast.LENGTH_SHORT).show();
                            uBlocked = Constants.FALSE;
                            blockBtn.setText(Constants.BLOCK);

                        }
                    });
                }
            }
        });
    }


    private void sendMessage(final String msg, final boolean isNote) {
        ChatUtilClass myChat = new ChatUtilClass(Constants.ME, Constants.getCurrentTimestamp(), msg);
        final Map<String, Object> myMap = new HashMap<>();
        myMap.put("By", myChat.getBy());
        myMap.put("Timestamp", myChat.getTimestamp());
        myMap.put("Msg", myChat.getMsg());

        ChatUtilClass hisChat = new ChatUtilClass(Constants.HE, Constants.getCurrentTimestamp(), msg);
        final Map<String, Object> hisMap = new HashMap<>();
        hisMap.put("By", hisChat.getBy());
        hisMap.put("Timestamp", hisChat.getTimestamp());
        hisMap.put("Msg", hisChat.getMsg());


        if (uBlocked!=null){
            allUserRef.child(toUid).child(Constants.ALL_CHATS).child(myUid).child(Constants.isBlocked).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        String isBlocked = dataSnapshot.getValue().toString();
                        if (isBlocked.equals(Constants.TRUE)) {
                            Toast.makeText(ChatActivity.this, "You are blocked", Toast.LENGTH_SHORT).show();
                        } else if (uBlocked.equals(Constants.TRUE)) {
                            Toast.makeText(ChatActivity.this, "Unblock to send message", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!isNote) {
                                allUserRef.child(toUid).child(Constants.ALL_CHATS).child(myUid).child(Constants.Chats).push().updateChildren(hisMap).
                                        addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    allUserRef.child(myUid).child(Constants.ALL_CHATS).child(toUid).child(Constants.Chats).push().updateChildren(myMap).
                                                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        allUserRef.child(myUid).child(Constants.ALL_CHATS).child(toUid).child(Constants.LS).setValue(Constants.getCurrentTimestamp()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    allUserRef.child(toUid).child(Constants.ALL_CHATS).child(myUid).child(Constants.LS)
                                                                                            .setValue(Constants.getCurrentTimestamp()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                if (!isNote) {
                                                                                                    editText.setText("");
                                                                                                    Toast.makeText(ChatActivity.this, "Sent message", Toast.LENGTH_SHORT).show();
                                                                                                    if (myDataClass == null) {

                                                                                                        allUserRef.child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                            @Override
                                                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                                if (dataSnapshot.exists()) {
                                                                                                                    myDataClass = dataSnapshot.getValue(GenUserUtilClass.class);
                                                                                                                    new SendNotificationOnesignal(myDataClass.getName(), msg, userData.getNotif_Key(), Constants.N_CHAT_ACT);
                                                                                                                }
                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                                                                Toast.makeText(ChatActivity.this, "Couldn't send a notification", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        });
                                                                                                    } else {
                                                                                                        new SendNotificationOnesignal(myDataClass.getName(), msg, userData.getNotif_Key(), Constants.N_CHAT_ACT);
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        });
                            } else {
                                allUserRef.child(myUid).child(Constants.ALL_CHATS).child(toUid).child(Constants.Chats).push().updateChildren(myMap).
                                        addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    allUserRef.child(myUid).child(Constants.ALL_CHATS).child(toUid).child(Constants.LS).setValue(Constants.getCurrentTimestamp()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                allUserRef.child(toUid).child(Constants.ALL_CHATS).child(myUid).child(Constants.LS)
                                                                        .setValue(Constants.getCurrentTimestamp()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            if (!isNote) {
                                                                                editText.setText("");
                                                                                Toast.makeText(ChatActivity.this, "Sent message", Toast.LENGTH_SHORT).show();
                                                                                if (myDataClass == null) {

                                                                                    allUserRef.child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                            if (dataSnapshot.exists()) {
                                                                                                myDataClass = dataSnapshot.getValue(GenUserUtilClass.class);
                                                                                                new SendNotificationOnesignal(myDataClass.getName(), msg, userData.getNotif_Key(), Constants.N_CHAT_ACT);
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                                            Toast.makeText(ChatActivity.this, "Couldn't send a notification", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    });
                                                                                } else {
                                                                                    new SendNotificationOnesignal(myDataClass.getName(), msg, userData.getNotif_Key(), Constants.N_CHAT_ACT);
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });

                            }
                        }
                    } else {
                        allUserRef.child(toUid).child(Constants.ALL_CHATS).child(myUid).child(Constants.isBlocked).setValue(Constants.FALSE).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ChatActivity.this, "First chat initaiated", Toast.LENGTH_SHORT).show();
                                    allUserRef.child(toUid).child(Constants.ALL_CHATS).child(myUid).child(Constants.Chats).push().updateChildren(hisMap).
                                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        allUserRef.child(myUid).child(Constants.ALL_CHATS).child(toUid).child(Constants.Chats).push().updateChildren(myMap).
                                                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            allUserRef.child(myUid).child(Constants.ALL_CHATS).child(toUid).child(Constants.LS).setValue(Constants.getCurrentTimestamp()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        allUserRef.child(toUid).child(Constants.ALL_CHATS).child(myUid).child(Constants.LS)
                                                                                                .setValue(Constants.getCurrentTimestamp()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()) {
                                                                                                    editText.setText("");
                                                                                                    Toast.makeText(ChatActivity.this, "Sent message", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else {
            allUserRef.child(myUid).child(Constants.ALL_CHATS).child(toUid).child(Constants.isBlocked).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        uBlocked = dataSnapshot.getValue().toString();
                        if (uBlocked.equals(Constants.TRUE)) {
                            blockBtn.setText(Constants.UNBLOCK);
                        } else {
                            blockBtn.setText(Constants.BLOCK);
                        }
                        progLyt.setVisibility(View.VISIBLE);
                        allUserRef.child(toUid).child(Constants.ALL_CHATS).child(myUid).child(Constants.isBlocked).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {

                                    String isBlocked = dataSnapshot.getValue().toString();
                                    if (isBlocked.equals(Constants.TRUE)) {
                                        progLyt.setVisibility(View.GONE);

                                        Toast.makeText(ChatActivity.this, "You are blocked", Toast.LENGTH_SHORT).show();
                                    } else if (uBlocked.equals(Constants.TRUE)) {
                                        progLyt.setVisibility(View.GONE);

                                        Toast.makeText(ChatActivity.this, "Unblock to send message", Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (!isNote) {
                                            allUserRef.child(toUid).child(Constants.ALL_CHATS).child(myUid).child(Constants.Chats).push().updateChildren(hisMap).
                                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                allUserRef.child(myUid).child(Constants.ALL_CHATS).child(toUid).child(Constants.Chats).push().updateChildren(myMap).
                                                                        addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    allUserRef.child(myUid).child(Constants.ALL_CHATS).child(toUid).child(Constants.LS).setValue(Constants.getCurrentTimestamp()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                allUserRef.child(toUid).child(Constants.ALL_CHATS).child(myUid).child(Constants.LS)
                                                                                                        .setValue(Constants.getCurrentTimestamp()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        if (task.isSuccessful()) {
                                                                                                            if (!isNote) {
                                                                                                                editText.setText("");
                                                                                                                Toast.makeText(ChatActivity.this, "Sent message", Toast.LENGTH_SHORT).show();
                                                                                                                if (myDataClass == null) {
                                                                                                                    allUserRef.child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                        @Override
                                                                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                                            if (dataSnapshot.exists()) {
                                                                                                                                myDataClass = dataSnapshot.getValue(GenUserUtilClass.class);
                                                                                                                                new SendNotificationOnesignal(myDataClass.getName(), msg, userData.getNotif_Key(), Constants.N_CHAT_ACT);
                                                                                                                                progLyt.setVisibility(View.GONE);

                                                                                                                            }
                                                                                                                        }

                                                                                                                        @Override
                                                                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                                                                            progLyt.setVisibility(View.GONE);
                                                                                                                            Toast.makeText(ChatActivity.this, "Couldn't send a notification", Toast.LENGTH_SHORT).show();
                                                                                                                        }
                                                                                                                    });
                                                                                                                } else {
                                                                                                                    progLyt.setVisibility(View.GONE);

                                                                                                                    new SendNotificationOnesignal(myDataClass.getName(), msg, userData.getNotif_Key(), Constants.N_CHAT_ACT);
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                });
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    });
                                        } else {
                                            allUserRef.child(myUid).child(Constants.ALL_CHATS).child(toUid).child(Constants.Chats).push().updateChildren(myMap).
                                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                allUserRef.child(myUid).child(Constants.ALL_CHATS).child(toUid).child(Constants.LS).setValue(Constants.getCurrentTimestamp()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            allUserRef.child(toUid).child(Constants.ALL_CHATS).child(myUid).child(Constants.LS)
                                                                                    .setValue(Constants.getCurrentTimestamp()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        if (!isNote) {
                                                                                            editText.setText("");
                                                                                            Toast.makeText(ChatActivity.this, "Sent message", Toast.LENGTH_SHORT).show();
                                                                                            if (myDataClass == null) {

                                                                                                allUserRef.child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                    @Override
                                                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                        if (dataSnapshot.exists()) {
                                                                                                            myDataClass = dataSnapshot.getValue(GenUserUtilClass.class);
                                                                                                            progLyt.setVisibility(View.GONE);
                                                                                                            new SendNotificationOnesignal(myDataClass.getName(), msg, userData.getNotif_Key(), Constants.N_CHAT_ACT);
                                                                                                        }
                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                                                        progLyt.setVisibility(View.GONE);
                                                                                                        Toast.makeText(ChatActivity.this, "Couldn't send a notification", Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                });
                                                                                            } else {
                                                                                                progLyt.setVisibility(View.GONE);
                                                                                                new SendNotificationOnesignal(myDataClass.getName(), msg, userData.getNotif_Key(), Constants.N_CHAT_ACT);
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });

                                        }
                                    }
                                } else {
                                    allUserRef.child(toUid).child(Constants.ALL_CHATS).child(myUid).child(Constants.isBlocked).setValue(Constants.FALSE).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ChatActivity.this, "First chat initaiated", Toast.LENGTH_SHORT).show();
                                                allUserRef.child(toUid).child(Constants.ALL_CHATS).child(myUid).child(Constants.Chats).push().updateChildren(hisMap).
                                                        addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    allUserRef.child(myUid).child(Constants.ALL_CHATS).child(toUid).child(Constants.Chats).push().updateChildren(myMap).
                                                                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        allUserRef.child(myUid).child(Constants.ALL_CHATS).child(toUid).child(Constants.LS).setValue(Constants.getCurrentTimestamp()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()) {
                                                                                                    allUserRef.child(toUid).child(Constants.ALL_CHATS).child(myUid).child(Constants.LS)
                                                                                                            .setValue(Constants.getCurrentTimestamp()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                editText.setText("");
                                                                                                                progLyt.setVisibility(View.GONE);
                                                                                                                Toast.makeText(ChatActivity.this, "Sent message", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void firebaseSearch() {

        Query searchQuery = FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS).child(myUid).child(Constants.ALL_CHATS).child(toUid).child(Constants.Chats).orderByChild(Constants.Timestamp);


        /*searchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(getContext(),adapter.getItemCount()+"",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        FirebaseRecyclerOptions<ChatUtilClass> options = new FirebaseRecyclerOptions.Builder<ChatUtilClass>()
                .setQuery(searchQuery, ChatUtilClass.class)
                .build();


        adapter = new FirebaseRecyclerAdapter<ChatUtilClass, ChatViewHolder>(options) {
            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(ChatActivity.this).inflate(R.layout.chat_chat_recy_item_lyt, parent, false);
                return new ChatViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int position, @NonNull ChatUtilClass model) {


                holder.timeTxt.setText(Constants.getdatetimeFromTimestamp(model.getTimestamp()));
                holder.msgTxt.setText(model.getMsg());
                LinearLayout.LayoutParams lgparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams rgparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams cparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                lgparams.setMargins(120, 10, 10, 10);
                rgparams.setMargins(10, 10, 120, 10);
                cparams.setMargins(70, 10, 700, 10);


                if (model.getMsg().contains(Constants.You_have_been_blocked)||model.getMsg().contains(Constants.You_have_been_unblocked)||model.getMsg().contains(Constants.You_are_now_connected)||model.getMsg().contains(Constants.You_are_now_disconnected)){

                   // holder.linearLayout.setLayoutParams(cparams);
                    holder.leftView.setVisibility(View.INVISIBLE);
                    holder.rightView.setVisibility(View.INVISIBLE);

                }else {
                    if (model.getBy().equals(Constants.ME)) {
                      //  holder.linearLayout.setLayoutParams(lgparams);
                        holder.linearLayout.setBackground(getResources().getDrawable(R.drawable.left_gap_chat_bg));
                        holder.leftView.setVisibility(View.INVISIBLE);
                        holder.rightView.setVisibility(View.GONE);
                    } else {
                      //  holder.linearLayout.setLayoutParams(rgparams);
                        holder.leftView.setVisibility(View.GONE);
                        holder.rightView.setVisibility(View.INVISIBLE);
                        holder.linearLayout.setBackground(getResources().getDrawable(R.drawable.right_gap_chat_bg));
                    }
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

    public class ChatViewHolder extends RecyclerView.ViewHolder {


        TextView msgTxt, timeTxt;
        ImageView leftView, rightView;
        LinearLayout linearLayout;


        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            msgTxt = itemView.findViewById(R.id.chat_chat_recy_item_msg_id);
            timeTxt = itemView.findViewById(R.id.chat_chat_recy_item_time_id);
            leftView = itemView.findViewById(R.id.chat_chat_recy_item_left_gap_id);
            rightView = itemView.findViewById(R.id.chat_chat_recy_item_right_gap_id);
            linearLayout = itemView.findViewById(R.id.chat_chat_recy_item_lin_lyt_id);
        }

    }
}
