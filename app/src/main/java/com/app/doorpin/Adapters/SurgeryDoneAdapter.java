package com.app.doorpin.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.doorpin.R;
import com.app.doorpin.models.SurgeriesDone;


public class SurgeryDoneAdapter extends RecyclerView.Adapter<SurgeryDoneAdapter.ViewHolder>{

    SurgeriesDone surgerydone[];
    public SurgeryDoneAdapter(SurgeriesDone[] surgerydone){
        this.surgerydone=surgerydone;
    }

    @Override
    public SurgeryDoneAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.surgeries_done_item, parent, false);
        SurgeryDoneAdapter.ViewHolder viewHolder = new SurgeryDoneAdapter.ViewHolder(listItem);
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull SurgeryDoneAdapter.ViewHolder holder, int position) {

        final SurgeriesDone surgeries=surgerydone[position];
        holder.tv_surgerydone.setText(surgerydone[position].getSurgeriesDone());
    }

    @Override
    public int getItemCount() {
        return surgerydone.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_surgerydone;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_surgerydone = (TextView) itemView.findViewById(R.id.tv_surgery_done);

        }
    }

}
