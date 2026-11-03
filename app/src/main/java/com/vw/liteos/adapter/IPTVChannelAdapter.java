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
import com.vw.liteos.model.IPTVChannel;

import java.util.ArrayList;
import java.util.List;

public class IPTVChannelAdapter extends RecyclerView.Adapter<IPTVChannelAdapter.ChannelViewHolder> {
    public interface OnChannelClickListener {
        void onChannelClick(IPTVChannel channel);
        void onFavoriteClick(IPTVChannel channel);
    }

    private final List<IPTVChannel> channels = new ArrayList<>();
    private final OnChannelClickListener listener;

    public IPTVChannelAdapter(OnChannelClickListener listener) {
        this.listener = listener;
    }

    public void setChannels(List<IPTVChannel> list) {
        channels.clear();
        if (list != null) channels.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_iptv_channel, parent, false);
        return new ChannelViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelViewHolder holder, int position) {
        holder.bind(channels.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    static class ChannelViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLogo;
        TextView tvName;
        TextView tvCategory;
        ImageView ivFav;

        public ChannelViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLogo = itemView.findViewById(R.id.ivChannelLogo);
            tvName = itemView.findViewById(R.id.tvChannelName);
            tvCategory = itemView.findViewById(R.id.tvChannelCategory);
            ivFav = itemView.findViewById(R.id.ivChannelFav);
            RemoteNavigationManager.applyFocusAnimation(itemView);
        }

        public void bind(IPTVChannel channel, OnChannelClickListener listener) {
            tvName.setText(channel.getName());
            tvCategory.setText(channel.getCategory() != null ? channel.getCategory() : "Live TV");
            ivFav.setImageResource(channel.isFavorite() ? R.drawable.ic_star_filled : R.drawable.ic_star);

            ivLogo.setImageResource(R.drawable.ic_tv);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onChannelClick(channel);
            });

            ivFav.setOnClickListener(v -> {
                if (listener != null) listener.onFavoriteClick(channel);
            });
        }
    }
}
