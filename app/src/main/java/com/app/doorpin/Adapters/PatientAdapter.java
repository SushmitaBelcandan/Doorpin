package com.app.doorpin.Adapters;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.doorpin.Activity.HomePage_Doctor;
import com.app.doorpin.Activity.PatientDetails;
import com.app.doorpin.R;
import com.app.doorpin.models.Patient;

import java.util.ArrayList;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.ViewHolder> {

    ArrayList<String> patient_id;
    ArrayList<String> patient_name;

    public PatientAdapter(ArrayList<String> patientId, ArrayList<String> patientName) {
        this.patient_id = patientId;
        this.patient_name = patientName;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.home_page_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tv_patientid.setText("Patient Id-" + " " + patient_id.get(position));
        holder.tv_patientname.setText(patient_name.get(position));

    }

    @Override
    public int getItemCount() {
        return patient_id.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //
        public TextView tv_patientname, tv_patientid;

        public ViewHolder(View itemView) {
            super(itemView);
//
            this.tv_patientname = (TextView) itemView.findViewById(R.id.patientname);
            this.tv_patientid = (TextView) itemView.findViewById(R.id.tv_patient_id);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), PatientDetails.class);
                    v.getContext().startActivity(intent);
                    ((HomePage_Doctor) v.getContext()).overridePendingTransition(R.anim.page_turn_in, R.anim.page_turn_out);
                  /*  AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(v.getContext(),
                            R.animator.page_turn);
                    set.setTarget(intent);
                    set.start();*/
                }
            });
        }
    }
}
