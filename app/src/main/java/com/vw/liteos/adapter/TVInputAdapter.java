package com.vw.liteos.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vw.liteos.R;
import com.vw.liteos.manager.RemoteNavigationManager;
import com.vw.liteos.model.TVInputSource;

import java.util.ArrayList;
import java.util.List;

public class TVInputAdapter extends RecyclerView.Adapter<TVInputAdapter.InputViewHolder> {
    public interface OnInputClickListener {
        void onInputClick(TVInputSource source);
    }

    private final List<TVInputSource> inputs = new ArrayList<>();
    private final OnInputClickListener listener;

    public TVInputAdapter(OnInputClickListener listener) {
        this.listener = listener;
    }

    public void setInputs(List<TVInputSource> list) {
        inputs.clear();
        if (list != null) inputs.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InputViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tv_input, parent, false);
        return new InputViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull InputViewHolder holder, int position) {
        holder.bind(inputs.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return inputs.size();
    }

    static class InputViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
        TextView tvType;

        public InputViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivInputIcon);
            tvTitle = itemView.findViewById(R.id.tvInputTitle);
            tvType = itemView.findViewById(R.id.tvInputType);
            RemoteNavigationManager.applyFocusAnimation(itemView);
        }

        public void bind(TVInputSource source, OnInputClickListener listener) {
            tvTitle.setText(source.getTitle());
            tvType.setText(source.getType().name());

            if (source.getType() == TVInputSource.Type.TUNER) {
                ivIcon.setImageResource(R.drawable.ic_tv);
            } else {
                ivIcon.setImageResource(R.drawable.ic_hdmi);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onInputClick(source);
            });
        }
    }
}
