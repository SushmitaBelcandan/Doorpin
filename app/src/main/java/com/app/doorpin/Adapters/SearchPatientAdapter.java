package com.app.doorpin.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.doorpin.R;
import com.app.doorpin.reference.SessionManager;

import java.util.ArrayList;

public class SearchPatientAdapter extends RecyclerView.Adapter<SearchPatientAdapter.ViewHolder> {

    Context mContext;
    SessionManager session;
    ArrayList<String> arr_list_patient_name;
    ArrayList<String> arr_list_patient_id;
    ArrayList<String> arr_list_display_id;
    private final OnItemClickListener listener;
    RecyclerView rv_view;


    public SearchPatientAdapter(Context context, RecyclerView rv_list, ArrayList<String> patient_name,
                                ArrayList<String> display_id, ArrayList<String> patient_id, OnItemClickListener listener) {
        this.mContext = context;
        this.arr_list_patient_name = patient_name;
        this.arr_list_patient_id = patient_id;
        this.arr_list_display_id = display_id;
        this.listener = listener;
        this.rv_view = rv_list;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.surgery_search_patient_adapter, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        session = new SessionManager(mContext);
        if (!arr_list_display_id.get(position).equals(null) && !arr_list_display_id.get(position).equals("null")
                && !arr_list_display_id.get(position).equals("NA") && !arr_list_display_id.get(position).isEmpty()
                && !arr_list_patient_name.get(position).equals(null)) {
            holder.tv_patient_name.setText(arr_list_patient_name.get(position));
            holder.tv_patient_id.setText("Patient Id -" + " " + arr_list_display_id.get(position));
        }

        holder.cv_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //return patient id
                listener.onItemClick(arr_list_patient_id.get(position), arr_list_patient_name.get(position));//callback selected patient row's value to activity
               /* rv_view.removeAllViews();
                notifyDataSetChanged();*/
            }
        });

    }

    @Override
    public int getItemCount() {
        return arr_list_patient_id.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_patient_name, tv_patient_id;
        public CardView cv_container;

        public ViewHolder(View itemView) {
            super(itemView);

            this.tv_patient_name = (TextView) itemView.findViewById(R.id.tv_patient_name);
            this.tv_patient_id = (TextView) itemView.findViewById(R.id.tv_patient_id);
            this.cv_container = (CardView) itemView.findViewById(R.id.cv_container);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String str_patient_id, String str_patient_name);
    }
}
