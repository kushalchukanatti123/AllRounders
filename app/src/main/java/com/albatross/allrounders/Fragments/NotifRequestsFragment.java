package com.albatross.allrounders.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.albatross.allrounders.MAIN.ChatActivity;
import com.albatross.allrounders.MAIN.StudentProfileActivity;
import com.albatross.allrounders.R;
import com.albatross.allrounders.Util.ChatUtilClass;
import com.albatross.allrounders.Util.Constants;
import com.albatross.allrounders.Util.GenUserUtilClass;
import com.albatross.allrounders.Util.GeneralNotifUtilClass;
import com.albatross.allrounders.Util.RequestNotifUtilClass;
import com.albatross.allrounders.Util.SendNotificationOnesignal;
import com.albatross.allrounders.Util.StudentUtilClass;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotifRequestsFragment extends Fragment {
    View v;
    RecyclerView recyclerView;
    String myUid;
    FirebaseRecyclerAdapter<RequestNotifUtilClass, NotifReqViewHolder> adapter;
    DatabaseReference allUserRef;
    FrameLayout loadingFramLyt;
    String myName;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.notif_requests_frag_lyt, container, false);

        recyclerView = v.findViewById(R.id.notif_req_frag_recy_vw_id);
        loadingFramLyt = v.findViewById(R.id.notif_req_loading_frag_lyt_id);

        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        allUserRef = FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS);


        allUserRef.child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    myName = dataSnapshot.child("Name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);

        firebaseSearch();
        return v;
    }
   /* private void sendMessage(final String msg, final boolean isNote, final String toUid) {
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
                                                        Toast.makeText(getContext(), "Connected", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });


    }*/
   private void sendMessage(final String msg, final boolean isNote, final String toUid) {
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


           allUserRef.child(myUid).child(Constants.ALL_CHATS).child(toUid).child(Constants.isBlocked).addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   if (dataSnapshot.exists()) {
                       allUserRef.child(toUid).child(Constants.ALL_CHATS).child(myUid).child(Constants.isBlocked).addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               if (dataSnapshot.exists()) {

                                   String isBlocked = dataSnapshot.getValue().toString();


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
                                                                                       Toast.makeText(getContext(),"Connected",Toast.LENGTH_SHORT).show();
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
                                   allUserRef.child(toUid).child(Constants.ALL_CHATS).child(myUid).child(Constants.isBlocked).setValue(Constants.FALSE).addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           if (task.isSuccessful()) {
                                               Toast.makeText(getContext(), "First chat initaiated", Toast.LENGTH_SHORT).show();
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

                                                                                                               Toast.makeText(getContext(), "Sent message", Toast.LENGTH_SHORT).show();
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



    private void firebaseSearch() {

        Query searchQuery = FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS).child(myUid).child(Constants.Notifications).child(Constants.Requests);

        /*searchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(getContext(),adapter.getItemCount()+"",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        FirebaseRecyclerOptions<RequestNotifUtilClass> options = new FirebaseRecyclerOptions.Builder<RequestNotifUtilClass>()
                .setQuery(searchQuery, RequestNotifUtilClass.class)
                .build();


        adapter = new FirebaseRecyclerAdapter<RequestNotifUtilClass, NotifReqViewHolder>(options) {
            @NonNull
            @Override
            public NotifReqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.req_notif_recy_item_lyt, parent, false);
                return new NotifReqViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull final NotifReqViewHolder holder, int position, @NonNull final RequestNotifUtilClass model) {
                final String suid = this.getSnapshots().getSnapshot(position).getKey();

                final GenUserUtilClass[] genUserUtilClass = new GenUserUtilClass[1];
                holder.timeTxt.setText(Constants.getdatetimeFromTimestamp(model.getTimestamp()));

                FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS).child(suid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            genUserUtilClass[0] = dataSnapshot.getValue(GenUserUtilClass.class);
                            holder.nameTxt.setText(genUserUtilClass[0].getName());
                            Glide.with(getContext()).load(genUserUtilClass[0].getProfile_Url()).into(holder.imageView);
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
                        startActivity(i);
                    }
                });

                if (model.getType().equals(Constants.Connection)) {
                    holder.msgTxt.setText("Connection Request");
                    holder.declineBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            allUserRef.child(myUid).child(Constants.Notifications).child(Constants.Requests).child(suid).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                allUserRef.child(suid).child(Constants.ACTIONS).child(Constants.Requests).child(myUid).child("Status").setValue(Constants.DECLINED)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    String msg = myUid + " declined your request";
                                                                    Map<String, Object> msgMap = new HashMap<>();
                                                                    msgMap.put("Msg", msg);
                                                                    msgMap.put("Timestamp", Constants.getCurrentTimestamp());
                                                                    allUserRef.child(suid).child(Constants.Notifications).child(Constants.General).push().setValue(msgMap)
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        if (myName!=null){
                                                                                            new SendNotificationOnesignal(myName+" declined",myName+" declined your connection request",genUserUtilClass[0].getNotif_Key(),Constants.N_NOTIF_ACT);
                                                                                        }else {
                                                                                            allUserRef.child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                @Override
                                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                    if (dataSnapshot.exists()) {
                                                                                                        myName = dataSnapshot.child("Name").getValue().toString();
                                                                                                        new SendNotificationOnesignal(myName+" declined",myName+" declined your connection request",genUserUtilClass[0].getNotif_Key(),Constants.N_NOTIF_ACT);

                                                                                                    }
                                                                                                }

                                                                                                @Override
                                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                                                    Toast.makeText(getContext(), "Couldn't send a notification", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                        Toast.makeText(getContext(), "Declined successfully", Toast.LENGTH_SHORT).show();
                                                                                        loadingFramLyt.setVisibility(View.GONE);
                                                                                    } else {
                                                                                        Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                                        loadingFramLyt.setVisibility(View.GONE);
                                                                                    }
                                                                                }
                                                                            });
                                                                } else {
                                                                    Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                    loadingFramLyt.setVisibility(View.GONE);
                                                                }
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                loadingFramLyt.setVisibility(View.GONE);

                                            }
                                        }
                                    });
                        }
                    });

                    holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //ADDING TO MY STUDENTS SECTION

                            Map<String, Object> studMap = new HashMap<>();
                            final Map<String, Object> tutMap = new HashMap<>();
                            Map<String, Object> tMap = new HashMap<>();
                            Map<String, Object> cMap = new HashMap<>();
                            Map<String, Object> tcMap = new HashMap<>();

                            tMap.put("Timestamp", Constants.getCurrentTimestamp());

                            cMap.put("NULL", "NULL");
                            tcMap.put("Timestamp", Constants.getCurrentTimestamp());
                            tcMap.put(Constants.Classes, cMap);
                            studMap.put(suid, tcMap);

                            tutMap.put(myUid, tMap);
                            allUserRef.child(myUid).child(Constants.My_Students).updateChildren(studMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        allUserRef.child(suid).child(Constants.My_Tutors).updateChildren(tutMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    allUserRef.child(suid).child(Constants.ACTIONS).child(Constants.Requests).child(myUid).removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        allUserRef.child(myUid).child(Constants.Notifications).child(Constants.Requests).child(suid)
                                                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                //Send a general notification
                                                                                if (task.isSuccessful()) {
                                                                                    String sMsg = myName + " accepted your invitation";
                                                                                    GeneralNotifUtilClass generalNotifUtilClass = new GeneralNotifUtilClass(sMsg, Constants.getCurrentTimestamp());
                                                                                    Map<String, Object> sMsgmap = new HashMap<>();
                                                                                    sMsgmap.put("Msg", generalNotifUtilClass.getMsg());
                                                                                    sMsgmap.put("Timestamp", generalNotifUtilClass.getTimestamp());

                                                                                    allUserRef.child(suid).child(Constants.Notifications).child(Constants.General).push().setValue(sMsgmap)
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    if (myName!=null){
                                                                                                        sendMessage(Constants.You_are_now_connected,true,suid);
                                                                                                        new SendNotificationOnesignal(myName+" accepted",myName+" accepted your connection request",genUserUtilClass[0].getNotif_Key(),Constants.N_NOTIF_ACT);
                                                                                                    }else {
                                                                                                        allUserRef.child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                            @Override
                                                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                                if (dataSnapshot.exists()) {
                                                                                                                    myName = dataSnapshot.child("Name").getValue().toString();
                                                                                                                    sendMessage(Constants.You_are_now_connected,true,suid);
                                                                                                                    new SendNotificationOnesignal(myName+" accepted",myName+" accepted your connection request",genUserUtilClass[0].getNotif_Key(),Constants.N_NOTIF_ACT);

                                                                                                                }
                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                                                                Toast.makeText(getContext(), "Couldn't send a notification", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        });
                                                                                                    }
                                                                                                    loadingFramLyt.setVisibility(View.GONE);
                                                                                                }
                                                                                            });
                                                                                } else {
                                                                                    loadingFramLyt.setVisibility(View.GONE);
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        loadingFramLyt.setVisibility(View.GONE);

                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    loadingFramLyt.setVisibility(View.GONE);

                                                }
                                            }
                                        });
                                    } else {
                                        loadingFramLyt.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                } else {
                    //TODO: HE IS ASKING FOR A DISCONNECTION

                    holder.msgTxt.setText("Disonnection Request");
                    holder.declineBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            allUserRef.child(myUid).child(Constants.Notifications).child(Constants.Requests).child(suid).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                allUserRef.child(suid).child(Constants.ACTIONS).child(Constants.Requests).child(myUid).child("Status").setValue(Constants.DECLINED)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    String msg = myUid + " declined your disconnection request";
                                                                    Map<String, Object> msgMap = new HashMap<>();
                                                                    msgMap.put("Msg", msg);
                                                                    msgMap.put("Timestamp", Constants.getCurrentTimestamp());
                                                                    allUserRef.child(suid).child(Constants.Notifications).child(Constants.General).push().setValue(msgMap)
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        try {
                                                                                            Toast.makeText(getContext(), "Declined successfully", Toast.LENGTH_SHORT).show();

                                                                                        }catch (Exception e){

                                                                                        }
                                                                                        loadingFramLyt.setVisibility(View.GONE);
                                                                                    } else {
                                                                                        Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                                        loadingFramLyt.setVisibility(View.GONE);
                                                                                    }
                                                                                }
                                                                            });
                                                                } else {
                                                                    Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                    loadingFramLyt.setVisibility(View.GONE);
                                                                }
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                loadingFramLyt.setVisibility(View.GONE);

                                            }
                                        }
                                    });
                        }
                    });


                    //DISCONNECTING
                    holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //ADDING TO MY STUDENTS SECTION
                            allUserRef.child(myUid).child(Constants.My_Tutors).child(suid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        allUserRef.child(myUid).child(Constants.Notifications).child(Constants.Requests).child(suid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    removeFromTutorSide(myUid, suid);
                                                }
                                            }
                                        });
                                    }
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

    private void removeFromTutorSide(final String myUid, final String tuid) {
        allUserRef.child(tuid).child(Constants.My_Students).child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    StudentUtilClass studentUtilClass = dataSnapshot.getValue(StudentUtilClass.class);
                    Map<String, Object> classMap = studentUtilClass.getClasses();
                    final ArrayList<String> classList = new ArrayList<>(classMap.keySet());
                    final int[] count = {0};
                    for (String s : classList) {
                        DatabaseReference classRef = allUserRef.child(tuid).child(Constants.Classes).child(s).child(Constants.Students).child(myUid);
                        classRef.removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            count[0]++;
                                            if (count[0] == classList.size()) {
                                                Toast.makeText(getContext(), "Removed from all classes", Toast.LENGTH_SHORT).show();
                                                allUserRef.child(tuid).child(Constants.My_Students).child(myUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            sendMessage(Constants.You_are_now_disconnected,true,tuid);
                                                            Toast.makeText(getContext(), "Successfully disconnected", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public class NotifReqViewHolder extends RecyclerView.ViewHolder {

        TextView nameTxt, timeTxt, msgTxt;
        Button acceptBtn, declineBtn;
        CircleImageView imageView;
        CardView cardView;


        public NotifReqViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.req_notif_item_lyt_name_txt_id);
            timeTxt = itemView.findViewById(R.id.greq_notif_item_lyt_time_txt_id);
            acceptBtn = itemView.findViewById(R.id.greq_notif_item_lyt_accept_btn_id);
            declineBtn = itemView.findViewById(R.id.greq_notif_item_lyt_decline_btn_id);
            imageView = itemView.findViewById(R.id.req_notif_item_lyt_image_id);
            cardView = itemView.findViewById(R.id.req_notif_item_lyt_card_vw_id);
            msgTxt = itemView.findViewById(R.id.greq_notif_item_lyt_msg_txt_id);
        }

    }

}
