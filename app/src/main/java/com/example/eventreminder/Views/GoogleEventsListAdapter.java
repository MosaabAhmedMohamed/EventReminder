package com.example.eventreminder.Views;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GoogleEventsListAdapter extends RecyclerView.Adapter<GoogleEventsListAdapter.EventItemViewHolder> {
    
    @NonNull
    @Override
    public EventItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull EventItemViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class EventItemViewHolder extends RecyclerView.ViewHolder {

        public EventItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
