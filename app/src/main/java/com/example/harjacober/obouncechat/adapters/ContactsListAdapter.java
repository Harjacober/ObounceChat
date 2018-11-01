package com.example.harjacober.obouncechat.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.DataObjectHolder> {
    @NonNull
    @Override
    public DataObjectHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull DataObjectHolder dataObjectHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder{

        public DataObjectHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
