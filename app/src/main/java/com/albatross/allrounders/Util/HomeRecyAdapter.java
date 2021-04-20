package com.albatross.allrounders.Util;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.albatross.allrounders.Fragments.HomeFragment;
import com.albatross.allrounders.MAIN.TutorProfileActivity;
import com.albatross.allrounders.MAIN.TutorSetupActivity;
import com.albatross.allrounders.R;
import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeRecyAdapter extends RecyclerView.Adapter<HomeRecyAdapter.HomeViewHolder> {

    ArrayList<TutorUserUtilClass> tutorsList;
    ArrayList<String> uidList;
    Context context;
    View v;
    SingleShotLocationProvider.GPSCoordinates currentLocation;

    public HomeRecyAdapter(ArrayList<TutorUserUtilClass> tutorsList, ArrayList<String> uidList, Context context,SingleShotLocationProvider.GPSCoordinates currentLocation) {
        this.tutorsList = tutorsList;
        this.uidList = uidList;
        this.context = context;
        this.currentLocation = currentLocation;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        v = LayoutInflater.from(context).inflate(R.layout.home_recy_lyt,parent,false);

        return new HomeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, final int position) {
        holder.nameTxt.setText(tutorsList.get(position).getName());
        holder.addTxt.setText(tutorsList.get(position).getAddress_L()+"\n"+tutorsList.get(position).getAddress_C()+"\n"+tutorsList.get(position).getAddress_C());
        Glide.with(context).load(tutorsList.get(position).getProfile_Url()).into(holder.circleImageView);
        Map m = tutorsList.get(position).getSubjects();
        ArrayList<String> sList = new ArrayList<>();
        sList.addAll(m.keySet());
        holder.chipGroup.removeAllViews();
        for (String s:sList){
            final Chip chip = new Chip(context);
            chip.setText(s);
            chip.setClickable(false);
            chip.setCheckable(false);
            holder.chipGroup.addView(chip);
        }
      //  currentLocation = HomeFragment.currLocation;
        if (currentLocation!=null){
            double tLat,tLan;
            tLat = Double.parseDouble(tutorsList.get(position).getLat());
            tLan = Double.parseDouble(tutorsList.get(position).getLng());
            float dist = getDistanceBetweenTwoPoints(currentLocation.latitude,currentLocation.longitude,tLat,tLan);
            dist = dist/1000;
            dist = (float) Math.ceil(dist);
            holder.distTxt.setVisibility(View.VISIBLE);
            holder.distTxt.setText(dist+" Kms");
        }else {
            holder.distTxt.setVisibility(View.GONE);
            Toast.makeText(context,"Getting location failed",Toast.LENGTH_SHORT).show();
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, TutorProfileActivity.class);
                i.putExtra("DATA",tutorsList.get(position));
                i.putExtra("UID",uidList.get(position));
                context.startActivity(i);
            }
        });

    }
    private float getDistanceBetweenTwoPoints(double lat1, double lon1, double lat2, double lon2) {

        float[] distance = new float[2];

        Location.distanceBetween(lat1, lon1,
                lat2, lon2, distance);

        return distance[0];
    }

    @Override
    public int getItemCount() {
        return tutorsList.size();
    }

    class HomeViewHolder extends RecyclerView.ViewHolder{

        CircleImageView circleImageView;
        TextView nameTxt,addTxt,distTxt;
        ChipGroup chipGroup;
        CardView cardView;
        public HomeViewHolder(@NonNull View itemView) {
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
