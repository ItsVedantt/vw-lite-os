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
import com.vw.liteos.model.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class AppGridAdapter extends RecyclerView.Adapter<AppGridAdapter.AppViewHolder> {
    public interface OnAppClickListener {
        void onAppClick(AppInfo app);
        void onAppLongClick(AppInfo app, View anchorView);
    }

    private final List<AppInfo> apps = new ArrayList<>();
    private final OnAppClickListener listener;

    public AppGridAdapter(OnAppClickListener listener) {
        this.listener = listener;
    }

    public void setApps(List<AppInfo> newApps) {
        apps.clear();
        if (newApps != null) {
            apps.addAll(newApps);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_card, parent, false);
        return new AppViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppInfo app = apps.get(position);
        holder.bind(app, listener);
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvLabel;
        ImageView ivFavorite;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivAppIcon);
            tvLabel = itemView.findViewById(R.id.tvAppLabel);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            RemoteNavigationManager.applyFocusAnimation(itemView);
        }

        public void bind(AppInfo app, OnAppClickListener listener) {
            tvLabel.setText(app.getLabel());
            if (app.getIcon() != null) {
                ivIcon.setImageDrawable(app.getIcon());
            } else {
                ivIcon.setImageResource(R.drawable.ic_launcher_vw);
            }

            ivFavorite.setVisibility(app.isFavorite() ? View.VISIBLE : View.GONE);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onAppClick(app);
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onAppLongClick(app, v);
                    return true;
                }
                return false;
            });
        }
    }
}
