package com.albatross.allrounders.MAIN;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.albatross.allrounders.R;
import com.albatross.allrounders.Util.Constants;
import com.albatross.allrounders.Util.GenUserUtilClass;
import com.albatross.allrounders.Util.SingleShotLocationProvider;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onesignal.OneSignal;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class GeneralSetupActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    ImageButton chnImgBt;
    Bitmap selectedImageBitmap = null;
    ImageView profImgVw;
    Button saveBtn;
    FrameLayout progressLyt;
    String mName,mProfileUrl,mAddress,mLat,mLng;
    TextInputEditText nameEdtxt,descEdtxt,mobileEdtxt;
    TextInputEditText addLaneEdtxt,addCityEdtxt,addPincodeEdtxt;
    String uid;
    Spinner genderSpinner;
    ArrayAdapter<String> genderSpinnerAdapter;
    String gender=null;
    ImageButton datePickBtn;
    TextView dobTxt;
    String dob = null;
    String studDesc = null;
    String mobileNumber,addL,addC,addP,oneSignalNotifId;
    Button getCUrrentLocBtn;
    SingleShotLocationProvider.GPSCoordinates currLocation;
    boolean isCurrentlocPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_setup);

        chnImgBt = findViewById(R.id.gen_setup_act_prof_fram_lyt);
        profImgVw = findViewById(R.id.gen_setup_act_prof_img_vw);
        saveBtn = findViewById(R.id.gen_Set_act_save_btn_id);
        progressLyt = findViewById(R.id.gen_setup_prog_lyt_id);
        nameEdtxt = findViewById(R.id.gen_Set_act_name_edtxt_id);
        genderSpinner = findViewById(R.id.gen_Set_act_gender_spinner_id);
        descEdtxt = findViewById(R.id.gen_Set_act_age_id);
        datePickBtn = findViewById(R.id.gen_Set_act_dob_img_btn_id);
        dobTxt = findViewById(R.id.gen_Set_act_dob_txt_id);
        mobileEdtxt = findViewById(R.id.gen_Set_act_mobile_id);
        getCUrrentLocBtn = findViewById(R.id.gen_Set_act_current_loc_btn_id);

        addLaneEdtxt = findViewById(R.id.gen_Set_act_add_Lane_edtxt_id);
        addCityEdtxt = findViewById(R.id.gen_Set_act_add_city_edtxt_id);
        addPincodeEdtxt = findViewById(R.id.gen_Set_act_add_Pincode_edtxt_id);



        setSpinner();
        oneSignalNotifId = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();

        OneSignal.startInit(GeneralSetupActivity.this).init();
        OneSignal.setSubscription(true);


        chnImgBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = CropImage.activity()
                        .setAspectRatio(1,1)
                        .getIntent(GeneralSetupActivity.this);
                startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
                Toast t = Toast.makeText(GeneralSetupActivity.this,"Please crop face only",Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER,0,0);
                t.show();
            }
        });
        datePickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        getCUrrentLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRunTimePermission();
                /*if (currLocation==null){
                    checkRunTimePermission();
                }else {
                    if (currLocation.latitude==-1||currLocation.longitude==-1){
                        checkRunTimePermission();
                    }else {
                        Geocoder geocoder = new Geocoder(GeneralSetupActivity.this);

                        try {
                            List<Address> addresses = geocoder.getFromLocation(currLocation.latitude,currLocation.longitude,1);

                           addL =  addresses.get(0).getLocality();
                           addC = addresses.get(0).getSubAdminArea();
                           addP = addresses.get(0).getPostalCode();
                            mLat = currLocation.latitude+"";
                            mLng = currLocation.longitude+"";
                            addLaneEdtxt.setText(addL);
                            addCityEdtxt.setText(addC);
                            addPincodeEdtxt.setText(addP);

                        } catch (IOException e) {
                            mLng = null;
                            mLat = null;
                            Toast.makeText(GeneralSetupActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                }*/
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addL = addLaneEdtxt.getText().toString().trim();
                addC = addCityEdtxt.getText().toString().trim();
                addP = addPincodeEdtxt.getText().toString().trim();
                String address = addL+addC+addP;


                if (TextUtils.isEmpty(addL)){
                    Toast.makeText(GeneralSetupActivity.this,"Lane cannot be empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(addC)){
                    Toast.makeText(GeneralSetupActivity.this,"City cannot be empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(addP)){
                    Toast.makeText(GeneralSetupActivity.this,"Pincode cannot be empty",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!getAddress(address)){
                    Toast.makeText(GeneralSetupActivity.this,"Couldn't find address try some other",Toast.LENGTH_SHORT).show();
                    return;
                }


                if (selectedImageBitmap!=null){
                    if (gender==null){
                        Toast.makeText(GeneralSetupActivity.this,"Please select gender",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (dob==null){
                        Toast.makeText(GeneralSetupActivity.this,"Please select DOB",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (mLat==null||mLng==null){
                        Toast.makeText(GeneralSetupActivity.this,"Please provide valid address",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mName = nameEdtxt.getText().toString();
                    studDesc = descEdtxt.getText().toString().trim();
                    mobileNumber = mobileEdtxt.getText().toString().trim();
                    if (TextUtils.isEmpty(mName)){
                        Toast.makeText(GeneralSetupActivity.this,"Name cannot be empty",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(studDesc)){
                        Toast.makeText(GeneralSetupActivity.this,"Description cannot be empty",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(mobileNumber)||mobileNumber.length()>12){
                        Toast.makeText(GeneralSetupActivity.this,"Invalid mobile number",Toast.LENGTH_SHORT).show();
                        return;
                    }


                    final StorageReference imagesBucket = FirebaseStorage.getInstance().getReference().child("ALL_PROFILE");

                    progressLyt.setVisibility(View.VISIBLE);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
                    byte[] data2 = baos.toByteArray();

                    final String uName = System.currentTimeMillis()+""+new Random(100).nextInt();
                    UploadTask uploadTask = imagesBucket.child(uName).putBytes(data2);
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                imagesBucket.child(uName).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()){
                                            mProfileUrl = task.getResult().toString();
                                            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            updateData();

                                        }
                                    }
                                });
                            }
                        }
                    });
                }else {
                    Toast.makeText(GeneralSetupActivity.this,"Please select an image for profile",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void checkRunTimePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getLocation();

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        10);
            }
        } else {
            getLocation(); //GPSTracker is class that is used for retrieve user current location
        }
    }
    private void getLocation(){

            // when you need location
            // if inside activity context = this;


          new Handler().post(new Runnable() {
              @Override
              public void run() {
                  SingleShotLocationProvider.requestSingleUpdate(GeneralSetupActivity.this,
                          new SingleShotLocationProvider.LocationCallback() {
                              @Override public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                                  currLocation = location;
                                  Geocoder geocoder = new Geocoder(GeneralSetupActivity.this);

                                  try {
                                      List<Address> addresses = geocoder.getFromLocation(currLocation.latitude,currLocation.longitude,1);

                                      addL =  addresses.get(0).getLocality();
                                      addC = addresses.get(0).getSubAdminArea();
                                      addP = addresses.get(0).getPostalCode();
                                      mLat = currLocation.latitude+"";
                                      mLng = currLocation.longitude+"";
                                      addLaneEdtxt.setText(addL);
                                      addCityEdtxt.setText(addC);
                                      addPincodeEdtxt.setText(addP);
                                      Toast.makeText(GeneralSetupActivity.this,"Got location",Toast.LENGTH_SHORT).show();


                                  } catch (IOException e) {
                                      mLng = null;
                                      mLat = null;
                                      Toast.makeText(GeneralSetupActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                                  }
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
                if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // If User Checked 'Don't Show Again' checkbox for runtime permission, then navigate user to Settings
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("Permission Required");
                    dialog.setCancelable(false);
                    dialog.setMessage("You have to Allow permission to access user location");
                    dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package",
                                    getPackageName(), null));
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

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        switch (requestCode) {
            case 1001:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        getLocation();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION},10);
                    }
                }
                break;
            default:
                break;
        }
    }
    private boolean getAddress(String address) {
        Geocoder geocoder = new Geocoder(this);

        try {
            List<Address> addresses = geocoder.getFromLocationName(address,1);
            mAddress = address;
            mLat = addresses.get(0).getLatitude()+"";
            mLng = addresses.get(0).getLongitude()+"";

            return true;
        } catch (IOException e) {
            mLng = null;
            mLat = null;
            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void setSpinner() {
        genderSpinnerAdapter = new ArrayAdapter<String>(GeneralSetupActivity.this,android.R.layout.simple_spinner_item,Constants.GENDER_ARR);
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderSpinnerAdapter);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i){
                    case 0:
                        gender = null;
                        break;
                    case 1:
                        gender = Constants.GENDER_ARR[1];
                        break;
                    case 2:
                        gender = Constants.GENDER_ARR[2];
                        break;
                    case 3:
                        gender = Constants.GENDER_ARR[3];
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void updateData() {

        GenUserUtilClass userUtilClass = new GenUserUtilClass(mName,mProfileUrl,Constants.FALSE,dob,studDesc,gender,mobileNumber,mLat,mLng,addL,addC,addP,oneSignalNotifId);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS);
        Map<String,Object> map = new HashMap<>();
        map.put("Name",userUtilClass.getName());
        map.put(Constants.Address_L,userUtilClass.getAddress_L());
        map.put(Constants.Address_C,userUtilClass.getAddress_C());
        map.put(Constants.Address_P,userUtilClass.getAddress_P());
        map.put(Constants.Lat,userUtilClass.getLat());
        map.put(Constants.Lng,userUtilClass.getLng());

        map.put("Is_Tutor",userUtilClass.getIs_Tutor());
        map.put("Profile_Url",userUtilClass.getProfile_Url());
        map.put(Constants.Notif_Key,userUtilClass.getNotif_Key());

        map.put("DOB",userUtilClass.getDOB());
        map.put(Constants.S_Desc,userUtilClass.getS_Desc());
        map.put(Constants.Gender,userUtilClass.getGender());
        map.put(Constants.Mobile,userUtilClass.getMobile());



        userRef.child(uid).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(GeneralSetupActivity.this,"Saved user",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(GeneralSetupActivity.this,MainActivity.class);
                    startActivity(i);
                }else {
                    Toast.makeText(GeneralSetupActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final Uri resultUri = result.getUri();

                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    selectedImageBitmap = Glide.with(GeneralSetupActivity.this).asBitmap().load(resultUri).submit().get();
                                    selectedImageBitmap = getResizedBitmap(selectedImageBitmap,400);
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Glide.with(GeneralSetupActivity.this).load(selectedImageBitmap).into(profImgVw);
                                    }
                                });
                            }
                        }
                ).start();

                //  Bitmap bitmap = shrinkBitmap(selectedImagesList.get(j));

                //TODO:START ROG HERE




            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        DecimalFormat formatter = new DecimalFormat("00");
        String i1F =formatter.format(i1+1);
        String i2F =formatter.format(i2);
        dob = i+"-"+i1F+"-"+i2F;
        dobTxt.setText(dob);
    }
}
