package com.example.workingwithcalling;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.CallLogViewHolder> {

    private List<CallLogEntry> callLogEntries;
    private OnItemLongClickListener onItemLongClickListener;

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public CallLogAdapter(List<CallLogEntry> callLogEntries, OnItemLongClickListener onItemLongClickListener) {
        this.callLogEntries = callLogEntries;
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @NonNull
    @Override
    public CallLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_call_log, parent, false);
        return new CallLogViewHolder(view, onItemLongClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CallLogViewHolder holder, int position) {
        CallLogEntry entry = callLogEntries.get(position);
        holder.numberTextView.setText(entry.getNumber());
        holder.typeTextView.setText(entry.getType());
        holder.dateTextView.setText(entry.getDate());
        holder.durationTextView.setText(entry.getDuration());
    }

    @Override
    public int getItemCount() {
        return callLogEntries.size();
    }

    static class CallLogViewHolder extends RecyclerView.ViewHolder {
        TextView numberTextView, typeTextView, dateTextView, durationTextView;

        public CallLogViewHolder(@NonNull View itemView, OnItemLongClickListener onItemLongClickListener) {
            super(itemView);
            numberTextView = itemView.findViewById(R.id.numberTextView);
            typeTextView = itemView.findViewById(R.id.typeTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            durationTextView = itemView.findViewById(R.id.durationTextView);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemLongClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemLongClickListener.onItemLongClick(position);
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
    }
}
