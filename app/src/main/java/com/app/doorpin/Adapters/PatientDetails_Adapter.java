package com.app.doorpin.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.app.doorpin.fragment.PatientDetails_Illness_Frag;
import com.app.doorpin.fragment.PatientDetails_PersoInfo_Frag;

public class PatientDetails_Adapter extends FragmentPagerAdapter {
    private Context mContext;
    int mTotalTab;

    public PatientDetails_Adapter(@NonNull FragmentManager fm, Context contxt, int countTab) {
        super(fm);
        this.mContext = contxt;
        this.mTotalTab = countTab;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                PatientDetails_PersoInfo_Frag personal_info_frag = new PatientDetails_PersoInfo_Frag();
                return personal_info_frag;
            case 1:
                PatientDetails_Illness_Frag illness_info_frag = new PatientDetails_Illness_Frag();
                return illness_info_frag;
        }
        return null;
    }

    @Override
    public int getCount() {
        return mTotalTab;
    }
}
