package com.albatross.allrounders.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStructure;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.albatross.allrounders.MAIN.GeneralSetupActivity;
import com.albatross.allrounders.MAIN.TutorSetupActivity;
import com.albatross.allrounders.R;
import com.albatross.allrounders.Util.Constants;
import com.albatross.allrounders.Util.GenUserUtilClass;
import com.albatross.allrounders.Util.HomeRecyAdapter;
import com.albatross.allrounders.Util.SingleShotLocationProvider;
import com.albatross.allrounders.Util.TutorUserUtilClass;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {
    private View v;
    TextView searchEdTxt;
    EditText  searchEdTxt2;
    RecyclerView recyclerView, searchRecyclerview;
    ImageButton searchBackBtn,clearSearchBtn;
    String myUid;

    DatabaseReference tutorsRef, usersRef;
    ArrayList<TutorUserUtilClass> mTutorList = new ArrayList<>();
    ArrayList<String> mUIDList = new ArrayList<>();
    HomeRecyAdapter adapter;
    FrameLayout searchFramLyt;
    FirebaseRecyclerAdapter<String, HomeSubViewHolder> searchAdapter;

    ImageButton filterImgBtn;

    Dialog filterDialog;
    EditText fdChargeFromEdtxt, fdChargeToEdtxt, fdRangeFromEdTxt;
    SwitchCompat fdRangeSwitch, fdChargeSwitch;
    ChipGroup fdModeChipGrp, fdGenderChipGrp;
    Button fdClearbtn, fdApplyBtn;
    ImageButton fdCancelBtn;

    String mode = Constants.EMPTY_STR, gender = Constants.EMPTY_STR, text = Constants.EMPTY_STR;
    int charge = -1, range = -1;
    View fdrangeMask, fdchargeMask;
    final static int CHIGH = 100000;
    public static SingleShotLocationProvider.GPSCoordinates currLocation = null;
    DataSnapshot mDataSnapshot;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.home_frag_lyt, container, false);

        searchEdTxt = v.findViewById(R.id.home_frag_search_Edttxt_id);
        recyclerView = v.findViewById(R.id.home_frag_recy_vw_id);
        searchRecyclerview = v.findViewById(R.id.home_frag_search_recy_vw_id);
        searchEdTxt2 = v.findViewById(R.id.home_frag_search_Edttxt_id2);
        searchFramLyt = v.findViewById(R.id.home_frag_search_frame_lyt_id);
        searchBackBtn = v.findViewById(R.id.home_frag_search_back_img_btn_id2);
        filterImgBtn = v.findViewById(R.id.home_filter_btn_id);
        clearSearchBtn = v.findViewById(R.id.home_frag_search_clear_btn_id);


        checkRunTimePermission();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());
        searchRecyclerview.setLayoutManager(linearLayoutManager2);

        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        tutorsRef = FirebaseDatabase.getInstance().getReference().child(Constants.ALL_TUTORS);
        usersRef = FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS);

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mDataSnapshot = dataSnapshot;
                    adapter = new HomeRecyAdapter(mTutorList, mUIDList, getContext(),currLocation);
                    updateList(mDataSnapshot,Constants.EMPTY_STR, mode, charge, gender, range);

                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });


        // updateList("",Constants.EMPTY_STR,charge,gender,Constants.EMPTY_STR,charge);
        setFilterDialog();
        searchEdTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchFramLyt.setVisibility(View.VISIBLE);
                firebaseSubSearch("");
            }
        });
        searchBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchFramLyt.setVisibility(View.GONE);
            }
        });
        searchEdTxt2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString();
                firebaseSubSearch(s);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        filterImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDialog.show();
            }
        });
        clearSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEdTxt.setText("");
                updateList(mDataSnapshot,Constants.EMPTY_STR, mode, charge, gender, range);
                clearSearchBtn.setVisibility(View.GONE);
            }
        });
        return v;
    }

    private void setFilterDialog() {

        filterDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        filterDialog.setContentView(R.layout.filter_dlg_lyt);
        fdApplyBtn = filterDialog.findViewById(R.id.filter_apply_btn_id);
        fdClearbtn = filterDialog.findViewById(R.id.filter_clear_btn_id);
        fdCancelBtn = filterDialog.findViewById(R.id.filter_dlg_Cancel_btn);
        fdChargeFromEdtxt = filterDialog.findViewById(R.id.filter_mode_charge_from_edtxt_id);
        fdChargeToEdtxt = filterDialog.findViewById(R.id.filter_mode_charge_to_edtxt_id);
        fdGenderChipGrp = filterDialog.findViewById(R.id.filter_gender_chip_grp);
        fdModeChipGrp = filterDialog.findViewById(R.id.filter_mode_chip_grp);
        fdRangeFromEdTxt = filterDialog.findViewById(R.id.filter_range_edtxt_id);
        fdRangeSwitch = filterDialog.findViewById(R.id.filter_range_switch_id);
        fdChargeToEdtxt = filterDialog.findViewById(R.id.filter_mode_charge_to_edtxt_id);
        fdchargeMask = filterDialog.findViewById(R.id.filter_mode_charge_from_edtxt_mask_id);
        fdrangeMask = filterDialog.findViewById(R.id.filter_range_edtxt_mask_id);
        fdChargeSwitch = filterDialog.findViewById(R.id.filter_charge_switch_id);

        fdCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDialog.dismiss();
            }
        });
        fdRangeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    fdrangeMask.setVisibility(View.GONE);
                } else {
                    range = -1;
                    fdrangeMask.setVisibility(View.VISIBLE);
                    fdRangeFromEdTxt.setText("");
                }
            }
        });
        fdChargeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    fdchargeMask.setVisibility(View.GONE);
                } else {
                    charge = CHIGH;
                    fdchargeMask.setVisibility(View.VISIBLE);
                    fdChargeFromEdtxt.setText("");
                }
            }
        });
        fdModeChipGrp.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                switch (group.getCheckedChipId()) {
                    case R.id.filter_mode_chip_online_id:
                        mode = Constants.Online;
                        break;
                    case R.id.filter_mode_chip_offl_id:
                        mode = Constants.Offline;
                        break;
                    case R.id.filter_mode_chip_any_id:
                        mode = Constants.EMPTY_STR;
                        break;
                }
            }
        });
        fdGenderChipGrp.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                switch (group.getCheckedChipId()) {
                    case R.id.filter_gender_chip_any_id:
                        gender = Constants.EMPTY_STR;
                        break;
                    case R.id.filter_gender_chip_male_id:
                        gender = Constants.Male;
                        break;
                    case R.id.filter_gender_chip_female_id:
                        gender = Constants.Female;
                        break;
                }
            }
        });

        fdApplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO APPLY ALL FILTERS HERE
                String chargeStr = fdChargeFromEdtxt.getText().toString().trim();
                if (!TextUtils.isEmpty(chargeStr)) {
                    charge = Integer.parseInt(chargeStr);
                } else {
                    charge = CHIGH;
                }
                if (fdrangeMask.getVisibility() == View.VISIBLE) {
                    range = -1;
                } else {
                    String r = fdRangeFromEdTxt.getText().toString().trim();
                    if (!TextUtils.isEmpty(r)) {
                        range = Integer.parseInt(r);
                    } else {
                        range = -1;
                    }

                }
                if (fdchargeMask.getVisibility() == View.VISIBLE) {
                    charge = CHIGH;
                } else {
                    String c = fdChargeFromEdtxt.getText().toString().trim();
                    charge = Integer.parseInt(c);
                }
                if (currLocation != null) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            updateList(mDataSnapshot,text, mode, charge, gender, range);
                        }
                    });
                    filterDialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Couldn't fetch current location", Toast.LENGTH_SHORT).show();
                    updateList(mDataSnapshot,text, mode, charge, gender, range);
                    filterDialog.dismiss();
                }

            }
        });

        fdClearbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode = Constants.EMPTY_STR;
                gender = Constants.EMPTY_STR;

                charge = CHIGH;
                updateList(mDataSnapshot,"", mode, charge, gender, range);
            }
        });
    }

    private void firebaseSubSearch(final String text) {

        Query searchQuery = FirebaseDatabase.getInstance().getReference().child("SUBJECTS").orderByKey().limitToFirst(100).startAt(text).endAt(text + "\uf8ff");

        /*searchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(getContext(),adapter.getItemCount()+"",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        FirebaseRecyclerOptions<String> options = new FirebaseRecyclerOptions.Builder<String>()
                .setQuery(searchQuery, String.class)
                .build();


        searchAdapter = new FirebaseRecyclerAdapter<String, HomeSubViewHolder>(options) {
            @NonNull
            @Override
            public HomeSubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.tutor_setup_add_sub_dlg_recy_item_lyt, parent, false);
                return new HomeSubViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull HomeSubViewHolder holder, int position, @NonNull String model) {
                final String subject = this.getSnapshots().getSnapshot(position).getKey();
                if (model.equals(Constants.Others)){
                    holder.textView.setVisibility(View.GONE);
                }else {
                    holder.textView.setVisibility(View.VISIBLE);
                }
                holder.textView.setText(subject);
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        searchFramLyt.setVisibility(View.GONE);
                        clearSearchBtn.setVisibility(View.VISIBLE);
                        searchEdTxt.setText(subject);
                        updateList(mDataSnapshot,subject, mode, charge, gender, range);
                    }
                });
            }
        };
        searchAdapter.startListening();
        /*if (adapter.getItemCount()<=0){
            noUserFoundFramLyt.setVisibility(View.VISIBLE);
        }else {
            noUserFoundFramLyt.setVisibility(View.GONE);
        }*/
        /*LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(CoreAddMemberActivity.this, R.anim.layout_animation_fall_down);
        dlgSubRecyVw.setLayoutAnimation(animation);*/
        searchRecyclerview.setAdapter(searchAdapter);


    }

    @Override
    public void onStart() {
        super.onStart();
        if (searchAdapter != null) {
            searchAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (searchAdapter != null) {
            searchAdapter.stopListening();
        }
    }

    public class HomeSubViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView textView;


        public HomeSubViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tutor_setup_add_sub_dlg_recy_item_text_id);
        }

    }

    private void updateListFirst(DataSnapshot dataSnapshot,final String subject, final String mode, final int chargee, final String gender, final int rangee) {
        mTutorList.clear();
        mUIDList.clear();

        for (DataSnapshot d : dataSnapshot.getChildren()) {
            String isTutor  = d.child(Constants.Is_Tutor).getValue().toString();
            String uid = d.getKey();

            if (isTutor.equals(Constants.FALSE) || uid.equals(myUid)) {
                continue;
            }
            TutorUserUtilClass tutorUserUtilClass = d.getValue(TutorUserUtilClass.class);
            Map<String, Object> subMap = tutorUserUtilClass.getSubjects();
            int chrg = Integer.parseInt(tutorUserUtilClass.getAmount().trim());
            if (TextUtils.isEmpty(subject)) {
                if (tutorUserUtilClass.getGender().contains(gender) && tutorUserUtilClass.getMode().contains(mode) && chargee <= chrg) {
                    if (rangee != -1) {
                        float dist = getDistanceBetweenTwoPoints(Double.parseDouble(tutorUserUtilClass.getLat()), Double.parseDouble(tutorUserUtilClass.getLng()), currLocation.latitude, currLocation.longitude);

                        dist = dist / 1000;
                        //  Location.distanceBetween(Double.parseDouble(tutorUserUtilClass.getLat()),Double.parseDouble(tutorUserUtilClass.getLng()),currLocation.latitude,currLocation.longitude,);
                        int tutAmt = Integer.parseInt(tutorUserUtilClass.getAmount());
                        if (dist <= rangee) {
                            mTutorList.add(tutorUserUtilClass);
                            mUIDList.add(uid);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        mTutorList.add(tutorUserUtilClass);
                        mUIDList.add(uid);
                        adapter.notifyDataSetChanged();
                    }


                }
            } else {
                if (subMap.containsKey(subject)) {
                    //TODO FILTER HERE
                    if (tutorUserUtilClass.getGender().contains(gender) && tutorUserUtilClass.getMode().contains(mode) && chargee <= chrg) {
                        if (rangee != -1) {
                            float dist = getDistanceBetweenTwoPoints(Double.parseDouble(tutorUserUtilClass.getLat()), Double.parseDouble(tutorUserUtilClass.getLng()), currLocation.latitude, currLocation.longitude);

                            dist = dist / 1000;
                            //  Location.distanceBetween(Double.parseDouble(tutorUserUtilClass.getLat()),Double.parseDouble(tutorUserUtilClass.getLng()),currLocation.latitude,currLocation.longitude,);
                            int tutAmt = Integer.parseInt(tutorUserUtilClass.getAmount());
                            if (dist <= rangee) {
                                mTutorList.add(tutorUserUtilClass);
                                mUIDList.add(uid);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            mTutorList.add(tutorUserUtilClass);
                            mUIDList.add(uid);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

        }
    }


    private void updateList(DataSnapshot dataSnapshot,final String subject, final String mode, final int chargee, final String gender, final int rangee) {
        mTutorList.clear();
        mUIDList.clear();
        int chrg = CHIGH;

        for (DataSnapshot d : dataSnapshot.getChildren()) {
            String isTutor  = d.child(Constants.Is_Tutor).getValue().toString();
            String uid = d.getKey();

            if (isTutor.equals(Constants.FALSE) || uid.equals(myUid)) {
                continue;
            }
            TutorUserUtilClass tutorUserUtilClass = d.getValue(TutorUserUtilClass.class);
            String schrg = tutorUserUtilClass.getAmount();
            Log.e("CHRG",schrg);
          //  chrg = Integer.parseInt(schrg);
           //TODO: UPDATE CHARGE HERE WITHOUT FORGETTING

            if (TextUtils.isEmpty(subject)) {

                if (tutorUserUtilClass.getGender().contains(gender) && tutorUserUtilClass.getMode().contains(mode) && chargee <= chrg) {
                    if (rangee != -1) {
                        float dist = getDistanceBetweenTwoPoints(Double.parseDouble(tutorUserUtilClass.getLat()), Double.parseDouble(tutorUserUtilClass.getLng()), currLocation.latitude, currLocation.longitude);

                        dist = dist / 1000;
                        //  Location.distanceBetween(Double.parseDouble(tutorUserUtilClass.getLat()),Double.parseDouble(tutorUserUtilClass.getLng()),currLocation.latitude,currLocation.longitude,);
                        int tutAmt = Integer.parseInt(tutorUserUtilClass.getAmount());
                        if (dist <= rangee) {
                            mTutorList.add(tutorUserUtilClass);
                            mUIDList.add(uid);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        mTutorList.add(tutorUserUtilClass);
                        mUIDList.add(uid);
                        adapter.notifyDataSetChanged();
                    }


                }
            } else {
                Map<String, Object> subMap = tutorUserUtilClass.getSubjects();
                if (subMap.containsKey(subject)) {
                    //TODO FILTER HERE
                    if (tutorUserUtilClass.getGender().contains(gender) && tutorUserUtilClass.getMode().contains(mode) && chargee <= chrg) {
                        if (rangee != -1) {
                            float dist = getDistanceBetweenTwoPoints(Double.parseDouble(tutorUserUtilClass.getLat()), Double.parseDouble(tutorUserUtilClass.getLng()), currLocation.latitude, currLocation.longitude);

                            dist = dist / 1000;
                            //  Location.distanceBetween(Double.parseDouble(tutorUserUtilClass.getLat()),Double.parseDouble(tutorUserUtilClass.getLng()),currLocation.latitude,currLocation.longitude,);
                            int tutAmt = Integer.parseInt(tutorUserUtilClass.getAmount());
                            if (dist <= rangee) {
                                mTutorList.add(tutorUserUtilClass);
                                mUIDList.add(uid);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            mTutorList.add(tutorUserUtilClass);
                            mUIDList.add(uid);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

        }
    }


    //LOCATION STUFF
    private float getDistanceBetweenTwoPoints(double lat1, double lon1, double lat2, double lon2) {

        float[] distance = new float[2];

        Location.distanceBetween(lat1, lon1,
                lat2, lon2, distance);

        return distance[0];
    }

    public void checkRunTimePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getLocation();

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        10);
            }
        } else {
            getLocation(); //GPSTracker is class that is used for retrieve user current location
        }
    }

    private void getLocation() {

        // when you need location
        // if inside activity context = this;


        new Handler().post(new Runnable() {
            @Override
            public void run() {
                SingleShotLocationProvider.requestSingleUpdate(getActivity(),
                        new SingleShotLocationProvider.LocationCallback() {
                            @Override
                            public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                                currLocation = location;
                                adapter = new HomeRecyAdapter(mTutorList, mUIDList, getContext(),currLocation);
                                recyclerView.setAdapter(adapter);
                            }
                        });

            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // If User Checked 'Don't Show Again' checkbox for runtime permission, then navigate user to Settings
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setTitle("Permission Required");
                    dialog.setCancelable(false);
                    dialog.setMessage("You have to Allow permission to access user location");
                    dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package",
                                    getContext().getPackageName(), null));
                            //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityForResult(i, 1001);
                        }
                    });
                    AlertDialog alertDialog = dialog.create();
                    alertDialog.show();
                }
                //code for deny
            }
        }
    }

}
