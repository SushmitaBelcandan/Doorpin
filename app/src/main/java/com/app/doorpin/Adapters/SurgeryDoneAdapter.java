package com.app.doorpin.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.doorpin.R;
import com.app.doorpin.models.SurgeriesDone;

import java.util.ArrayList;


public class SurgeryDoneAdapter extends RecyclerView.Adapter<SurgeryDoneAdapter.ViewHolder> {

    ArrayList<String> arrListSurg;

    public SurgeryDoneAdapter(ArrayList<String> arr_list_surg) {
        this.arrListSurg = arr_list_surg;
    }

    @Override
    public SurgeryDoneAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.surgeries_done_item, parent, false);
        SurgeryDoneAdapter.ViewHolder viewHolder = new SurgeryDoneAdapter.ViewHolder(listItem);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull SurgeryDoneAdapter.ViewHolder holder, int position) {
        holder.tv_surgerydone.setText(arrListSurg.get(position));
    }

    @Override
    public int getItemCount() {
        return arrListSurg.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_surgerydone;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_surgerydone = (TextView) itemView.findViewById(R.id.tv_surgery_done);

        }
    }

}
