package com.albatross.allrounders.MAIN;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.albatross.allrounders.Fragments.ActionsFragment;
import com.albatross.allrounders.Fragments.AdvancedOptionsFragment;
import com.albatross.allrounders.Fragments.ChatsFragment;
import com.albatross.allrounders.Fragments.HomeFragment;
import com.albatross.allrounders.Fragments.MyClassFragment;
import com.albatross.allrounders.Fragments.MyStudentsFragment;
import com.albatross.allrounders.Fragments.NotificationsFragment;
import com.albatross.allrounders.R;
import com.albatross.allrounders.Util.Constants;
import com.albatross.allrounders.Util.GenUserUtilClass;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public Toolbar toolbar;
    private MenuItem menuHome, menuPengurus, menuSaranDanKritikan, menuAbout, menuLogout;
    boolean doubleBackToExitPressedOnce = false;


    DrawerLayout drawerLayout;
    NavigationView navView;
    View headerView;

    FirebaseUser firebaseUser;
    DatabaseReference allUserRef;
    String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        allUserRef = FirebaseDatabase.getInstance().getReference().child(Constants.ALL_USERS);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myUid = firebaseUser.getUid();
        setDrawer();


    }

    private void setDrawer() {
        loadFragment(new HomeFragment());

        NavigationView navigationView = findViewById(R.id.main_act_nav_id);
        if (firebaseUser.getEmail().equals(Constants.ADMIN_EMAIL)){
            navigationView.inflateMenu(R.menu.nav_items_adv);
        }else {
            navigationView.inflateMenu(R.menu.nav_items);
        }
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null); //disable tint on each icon to use color icon svg

        DrawerLayout drawer = findViewById(R.id.main_act_drawer_layout_id);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //custom header view
        headerView = navigationView.getHeaderView(0);
        RelativeLayout container = headerView.findViewById(R.id.container);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*startActivity(new Intent(getApplicationContext(), ProfileActivity.class));*/
                Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_LONG).show();
            }
        });

        final AppCompatTextView navUserName = headerView.findViewById(R.id.atv_name_header);

        final TextView navEmail = headerView.findViewById(R.id.tv_email_header);
        final CircleImageView profImgVw = headerView.findViewById(R.id.tv_image_header);

        allUserRef.child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    GenUserUtilClass userUtilClass = dataSnapshot.getValue(GenUserUtilClass.class);
                    navUserName.setText(userUtilClass.getName());
                    navEmail.setText(firebaseUser.getEmail());
                    Glide.with(MainActivity.this).load(userUtilClass.getProfile_Url()).into(profImgVw);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    /**
     * Fragment
     **/
    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    /**
     * Menu Bottom Navigation Drawer
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment fragment = null;

        // Handle navigation view item clicks here, you can custom the fragment as you want.
        switch (item.getItemId()) {
            case R.id.nav_men_home_id:
                fragment = new HomeFragment();
                break;
            case R.id.nav_men_my_stud_id:
                fragment = new MyStudentsFragment();
                break;
            case R.id.nav_men_chats_id:
                fragment = new ChatsFragment();
                break;
            case R.id.nav_men_my_tutors_id:
                fragment = new MyClassFragment();
                break;
            case R.id.nav_men_notif_id:
                fragment = new NotificationsFragment();
                break;
            case R.id.nav_men_actions_id:
                fragment = new ActionsFragment();
                break;
            case R.id.nav_men_adv_id:
                fragment = new AdvancedOptionsFragment();
                break;
            case R.id.nav_men_logout:
                dialogExit();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.main_act_drawer_layout_id);
        drawer.closeDrawer(GravityCompat.START);
        return loadFragment(fragment);
    }

    /*@Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        //Hidden Menu Bard For All Fragments
        menuHome = menu.findItem(R.id.nav_men_home_id);
        *//*menuPengurus = menu.findItem(R.id.menu_pengurus);
        menuSaranDanKritikan = menu.findItem(R.id.menu_saran_dan_kritikan);
        menuAbout = menu.findItem(R.id.menu_about);*//*
        menuLogout = menu.findItem(R.id.nav_men_logout);

//        if(menuHome != null && menuPengurus != null && menuSaranDanKritikan != null &&
//                menuAbout != null && menuLogout != null)

            menuHome.setVisible(false);
//        menuPengurus.setVisible(false);
//        menuSaranDanKritikan.setVisible(false);
//        menuAbout.setVisible(false);
        menuLogout.setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }*/

    private void dialogExit() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Sure to logout?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AuthUI.getInstance().signOut(MainActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    OneSignal.setSubscription(false);
                                    startActivity(new Intent(MainActivity.this, ZeroActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                    finish();
                                    return;
                                } else {
                                    Toast.makeText(MainActivity.this, "Failed, try again", Toast.LENGTH_SHORT).show();
                                    //FAILED
                                }
                            }
                        });

            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.main_act_drawer_layout_id);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            // klik double tap to exit
            if (doubleBackToExitPressedOnce) {
                finishAffinity();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Tap again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_items, menu);
        return true;
    }
*/
    private void setNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.main_act_drawer_layout_id); // initiate a DrawerLayout
        navView = (NavigationView) findViewById(R.id.main_act_nav_id); // initiate a Navigation View
// implement setNavigationItemSelectedListener event on NavigationView
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Fragment frag = null; // create a Fragment Object
                int itemId = menuItem.getItemId(); // get selected menu item's id
// check selected menu item's id and replace a Fragment Accordingly
                if (itemId == R.id.nav_men_home_id) {
                    frag = new HomeFragment();
                } else if (itemId == R.id.nav_men_my_stud_id) {
                    frag = new MyStudentsFragment();
                } else if (itemId == R.id.nav_men_logout) {
                    //TODO LOGOUT USER
                    AuthUI.getInstance().signOut(MainActivity.this)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {


                                        startActivity(new Intent(MainActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                        finish();
                                        return;
                                    } else {
                                        Toast.makeText(MainActivity.this, "Failed, try again", Toast.LENGTH_SHORT).show();
                                        //FAILED
                                    }
                                }
                            });
                }
// display a toast message with menu item's title
                Toast.makeText(getApplicationContext(), menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                if (frag != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    //  transaction.replace(R.id.main_act_fram_lyt_id, frag); // replace a Fragment with Frame Layout
                    transaction.commit(); // commit the changes
                    drawerLayout.closeDrawers(); // close the all open Drawer Views
                    return true;
                }
                return false;
            }
        });
    }
}
