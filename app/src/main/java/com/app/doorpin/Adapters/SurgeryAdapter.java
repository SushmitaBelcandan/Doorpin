package com.app.doorpin.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.doorpin.Activity.NewSurgery;
import com.app.doorpin.R;
import com.app.doorpin.models.Surgery;

public class SurgeryAdapter extends RecyclerView.Adapter<SurgeryAdapter.ViewHolder> {

    Surgery surgery[];

    public SurgeryAdapter(Surgery[] surgery) {
        this.surgery = surgery;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.surgery_single_item_view, parent, false);
        SurgeryAdapter.ViewHolder viewHolder = new SurgeryAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Surgery surgeries = surgery[position];
        holder.surgeryname.setText(surgery[position].getSurgeryname());
    }

    @Override
    public int getItemCount() {
        return surgery.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView surgeryname;
        ImageButton imgBtnDropdown;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.surgeryname = (TextView) itemView.findViewById(R.id.tv_surgeryname);
            this.imgBtnDropdown = (ImageButton) itemView.findViewById(R.id.imgBtnDropdown);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), NewSurgery.class);
                    v.getContext().startActivity(intent);

                }
            });

        }
    }
}
