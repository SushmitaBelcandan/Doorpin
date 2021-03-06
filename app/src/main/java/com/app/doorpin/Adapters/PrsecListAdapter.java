package com.app.doorpin.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.doorpin.R;
import com.app.doorpin.reference.SessionManager;

import java.util.ArrayList;

public class PrsecListAdapter extends RecyclerView.Adapter<PrsecListAdapter.ViewHolder> {

    Context mContext;
    SessionManager session;
    ArrayList<String> arr_list_prsec_name;

    public PrsecListAdapter(Context context, ArrayList<String> prsec_name) {
        this.mContext = context;
        this.arr_list_prsec_name = prsec_name;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.docs_list_adapter, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        session = new SessionManager(mContext);

        holder.tv_file_name.setText(arr_list_prsec_name.get(position));
        holder.iv_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arr_list_prsec_name.remove(holder.getAdapterPosition());
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, arr_list_prsec_name.size());
            }
        });

    }

    @Override
    public int getItemCount() {
        return arr_list_prsec_name.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_remove;
        public TextView tv_file_name;

        public ViewHolder(View itemView) {
            super(itemView);

            this.iv_remove = (ImageView) itemView.findViewById(R.id.iv_remove);
            this.tv_file_name = (TextView) itemView.findViewById(R.id.tv_file_name);
        }
    }
}
