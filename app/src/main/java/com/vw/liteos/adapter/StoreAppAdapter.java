package com.vw.liteos.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vw.liteos.R;
import com.vw.liteos.manager.RemoteNavigationManager;
import com.vw.liteos.model.StoreApp;

import java.util.ArrayList;
import java.util.List;

public class StoreAppAdapter extends RecyclerView.Adapter<StoreAppAdapter.StoreViewHolder> {
    public interface OnStoreAppClickListener {
        void onAppClick(StoreApp app);
        void onActionClick(StoreApp app);
    }

    private final List<StoreApp> list = new ArrayList<>();
    private final OnStoreAppClickListener listener;

    public StoreAppAdapter(OnStoreAppClickListener listener) {
        this.listener = listener;
    }

    public void setApps(List<StoreApp> apps) {
        list.clear();
        if (apps != null) list.addAll(apps);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store_app, parent, false);
        return new StoreViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        holder.bind(list.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class StoreViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
        TextView tvDev;
        TextView tvCategory;
        TextView tvVersion;
        TextView tvSize;
        TextView tvSource;
        Button btnAction;
        ProgressBar progressBar;

        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivStoreIcon);
            tvTitle = itemView.findViewById(R.id.tvStoreTitle);
            tvDev = itemView.findViewById(R.id.tvStoreDev);
            tvCategory = itemView.findViewById(R.id.tvStoreCategory);
            tvVersion = itemView.findViewById(R.id.tvStoreVersion);
            tvSize = itemView.findViewById(R.id.tvStoreSize);
            tvSource = itemView.findViewById(R.id.tvStoreSource);
            btnAction = itemView.findViewById(R.id.btnStoreAction);
            progressBar = itemView.findViewById(R.id.pbStoreDownload);

            RemoteNavigationManager.applyFocusAnimation(itemView);
        }

        public void bind(StoreApp app, OnStoreAppClickListener listener) {
            tvTitle.setText(app.getTitle());
            tvDev.setText(app.getDeveloper());
            tvCategory.setText(app.getCategory());
            tvVersion.setText("v" + app.getVersion());
            tvSize.setText(app.getFormattedSize());
            tvSource.setText(app.getSource());

            ivIcon.setImageResource(R.drawable.ic_launcher_vw);

            if (app.isInstalled()) {
                if (app.isUpdateAvailable()) {
                    btnAction.setText(R.string.btn_update);
                } else {
                    btnAction.setText(R.string.btn_open);
                }
            } else {
                btnAction.setText(R.string.btn_install);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onAppClick(app);
            });

            btnAction.setOnClickListener(v -> {
                if (listener != null) listener.onActionClick(app);
            });
        }
    }
}
