package com.app.doorpin.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.app.doorpin.R;
import com.app.doorpin.models.Illness;
import com.app.doorpin.models.Illness_List_Model;
import com.app.doorpin.models.SetDateonCalendar;
import com.mindorks.placeholderview.PlaceHolderView;

public class PatientDetails_Illness_Frag extends Fragment {

    PlaceHolderView phv_illness;
    SetDateonCalendar setDateonCalendar;


    public PatientDetails_Illness_Frag() {
        super();
        //empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.illness_details, container, false);

      /*  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/
        phv_illness = view.findViewById(R.id.phv_illness);
        phv_illness.addView(new Illness_List_Model(getActivity()));
        phv_illness.addView(new Illness_List_Model(getActivity()));
        phv_illness.addView(new Illness_List_Model(getActivity()));
        phv_illness.addView(new Illness_List_Model(getActivity()));
        phv_illness.addView(new Illness_List_Model(getActivity()));

        return view;
    }

}
