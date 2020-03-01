package com.app.doorpin.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.doorpin.R;
import com.app.doorpin.models.DoctorDocuments;


public class DoctorDocAdapter extends RecyclerView.Adapter<DoctorDocAdapter.ViewHolder> {

    DoctorDocuments doctorDocuments[];
    public DoctorDocAdapter(DoctorDocuments[] doctorDocuments){
        this.doctorDocuments=doctorDocuments;
    }

    @Override
    public DoctorDocAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.doctor_document_card, parent, false);
        DoctorDocAdapter.ViewHolder viewHolder = new DoctorDocAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorDocAdapter.ViewHolder holder, final int position) {

        holder.iv_documents.setImageResource(doctorDocuments[position].getDoctor_documents());

    }

    @Override
    public int getItemCount() {
        return doctorDocuments.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_documents;

        public ViewHolder(View itemView) {
            super(itemView);
            this.iv_documents = (ImageView) itemView.findViewById(R.id.iv_documents);
        }
    }
}
