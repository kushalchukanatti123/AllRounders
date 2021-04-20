package com.albatross.allrounders.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.albatross.allrounders.R;

public class NotificationsFragment extends Fragment {
    View v;
    TextView genTxt,reqTxt;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.notif_frag_lyt,container,false);
        genTxt = v.findViewById(R.id.notif_Frag_gen_txt_id);
        reqTxt = v.findViewById(R.id.notif_Frag_req_txt_id);


        genTxt.setBackground(getResources().getDrawable(R.drawable.color_acc_rect_bg));
        reqTxt.setBackground(getResources().getDrawable(R.drawable.white_rect_bg));
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.notif_frag_fram_lyt_id, new NotifGegneralFragment())
                .commit();
        genTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                genTxt.setBackground(getResources().getDrawable(R.drawable.color_acc_rect_bg));
                reqTxt.setBackground(getResources().getDrawable(R.drawable.white_rect_bg));
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.notif_frag_fram_lyt_id, new NotifGegneralFragment())
                        .commit();
            }
        });
        reqTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reqTxt.setBackground(getResources().getDrawable(R.drawable.color_acc_rect_bg));
                genTxt.setBackground(getResources().getDrawable(R.drawable.white_rect_bg));
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.notif_frag_fram_lyt_id, new NotifRequestsFragment())
                        .commit();
            }
        });
        return v;
    }
}
