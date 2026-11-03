package com.vw.liteos.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;
import com.vw.liteos.R;
import com.vw.liteos.manager.IPTVManager;
import com.vw.liteos.manager.RemoteNavigationManager;
import com.vw.liteos.model.IPTVChannel;

import java.util.ArrayList;
import java.util.List;

public class IPTVPlayerActivity extends AppCompatActivity {
    public static final String EXTRA_CHANNEL = "extra_channel";
    public static final String EXTRA_CHANNEL_INDEX = "extra_channel_index";

    private PlayerView playerView;
    private ExoPlayer player;
    private LinearLayout layoutBuffering;
    private LinearLayout layoutError;
    private LinearLayout layoutOsd;
    private TextView tvErrorMessage;
    private Button btnRetryStream;
    private TextView tvOsdChannelName;
    private TextView tvOsdCategory;
    private ImageView ivOsdLogo;

    private IPTVManager iptvManager;
    private List<IPTVChannel> channels = new ArrayList<>();
    private int currentChannelIndex = 0;
    private IPTVChannel currentChannel;

    private final Handler osdHandler = new Handler(Looper.getMainLooper());
    private final Runnable hideOsdRunnable = () -> {
        if (layoutOsd != null) layoutOsd.setVisibility(View.GONE);
    };

    public static void start(Context context, IPTVChannel channel, int index) {
        Intent intent = new Intent(context, IPTVPlayerActivity.class);
        intent.putExtra(EXTRA_CHANNEL, channel);
        intent.putExtra(EXTRA_CHANNEL_INDEX, index);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iptv_player);

        playerView = findViewById(R.id.playerView);
        layoutBuffering = findViewById(R.id.layoutBuffering);
        layoutError = findViewById(R.id.layoutError);
        layoutOsd = findViewById(R.id.layoutOsd);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        btnRetryStream = findViewById(R.id.btnRetryStream);
        tvOsdChannelName = findViewById(R.id.tvOsdChannelName);
        tvOsdCategory = findViewById(R.id.tvOsdCategory);
        ivOsdLogo = findViewById(R.id.ivOsdLogo);

        iptvManager = new IPTVManager(this);
        channels = iptvManager.getChannelsByCategory("All");

        currentChannel = (IPTVChannel) getIntent().getSerializableExtra(EXTRA_CHANNEL);
        currentChannelIndex = getIntent().getIntExtra(EXTRA_CHANNEL_INDEX, 0);

        if (currentChannel == null && !channels.isEmpty()) {
            currentChannel = channels.get(0);
            currentChannelIndex = 0;
        }

        btnRetryStream.setOnClickListener(v -> playChannel(currentChannel));

        initPlayer();
        if (currentChannel != null) {
            playChannel(currentChannel);
        }
    }

    private void initPlayer() {
        if (player == null) {
            player = new ExoPlayer.Builder(this)
                    .build();

            playerView.setPlayer(player);
            playerView.setUseController(false); // Clean cinematic TV look

            player.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    if (playbackState == Player.STATE_BUFFERING) {
                        layoutBuffering.setVisibility(View.VISIBLE);
                        layoutError.setVisibility(View.GONE);
                    } else if (playbackState == Player.STATE_READY) {
                        layoutBuffering.setVisibility(View.GONE);
                        layoutError.setVisibility(View.GONE);
                    } else if (playbackState == Player.STATE_ENDED) {
                        layoutBuffering.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onPlayerError(PlaybackException error) {
                    layoutBuffering.setVisibility(View.GONE);
                    layoutError.setVisibility(View.VISIBLE);
                    tvErrorMessage.setText("Stream playback error: " + error.getMessage());
                }
            });
        }
    }

    private void playChannel(IPTVChannel channel) {
        if (channel == null || player == null) return;
        currentChannel = channel;
        layoutError.setVisibility(View.GONE);
        layoutBuffering.setVisibility(View.VISIBLE);

        iptvManager.recordRecentChannel(channel);

        showOsd(channel);

        try {
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(channel.getStreamUrl()));
            player.setMediaItem(mediaItem);
            player.prepare();
            player.setPlayWhenReady(true);
        } catch (Exception e) {
            layoutBuffering.setVisibility(View.GONE);
            layoutError.setVisibility(View.VISIBLE);
            tvErrorMessage.setText("Invalid stream URL: " + e.getMessage());
        }
    }

    private void showOsd(IPTVChannel channel) {
        tvOsdChannelName.setText(channel.getName());
        tvOsdCategory.setText((channel.getCategory() != null ? channel.getCategory() : "Live TV") + " • Legal Stream");
        layoutOsd.setVisibility(View.VISIBLE);

        osdHandler.removeCallbacks(hideOsdRunnable);
        osdHandler.postDelayed(hideOsdRunnable, 4000);
    }

    private void nextChannel() {
        if (channels.isEmpty()) return;
        currentChannelIndex = (currentChannelIndex + 1) % channels.size();
        playChannel(channels.get(currentChannelIndex));
    }

    private void prevChannel() {
        if (channels.isEmpty()) return;
        currentChannelIndex--;
        if (currentChannelIndex < 0) currentChannelIndex = channels.size() - 1;
        playChannel(channels.get(currentChannelIndex));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (RemoteNavigationManager.isChannelUpKey(keyCode) || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            nextChannel();
            return true;
        } else if (RemoteNavigationManager.isChannelDownKey(keyCode) || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            prevChannel();
            return true;
        } else if (RemoteNavigationManager.isConfirmKey(keyCode)) {
            if (currentChannel != null) showOsd(currentChannel);
            return true;
        } else if (RemoteNavigationManager.isBackKey(keyCode)) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Immediately stop and release playback to free up TV RAM (512 MB constraint)
        releasePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        osdHandler.removeCallbacksAndMessages(null);
    }

    private void releasePlayer() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }
}
