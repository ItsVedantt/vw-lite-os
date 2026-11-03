package com.vw.liteos.manager;

import android.content.Context;
import android.content.Intent;
import android.media.tv.TvContract;
import android.media.tv.TvInputInfo;
import android.net.Uri;
import android.widget.Toast;

import com.vw.liteos.model.TVInputSource;

import java.util.ArrayList;
import java.util.List;

public class TVInputManager {
    private final Context context;
    private android.media.tv.TvInputManager systemTvManager;

    public TVInputManager(Context context) {
        this.context = context.getApplicationContext();
        try {
            this.systemTvManager = (android.media.tv.TvInputManager) this.context.getSystemService(Context.TV_INPUT_SERVICE);
        } catch (Exception ignored) {}
    }

    public List<TVInputSource> getAvailableInputs() {
        List<TVInputSource> list = new ArrayList<>();

        // 1. Tuner / Live TV
        list.add(new TVInputSource("tuner", "Live TV (Antenna/Cable)", TVInputSource.Type.TUNER, true));

        // 2. HDMI 1
        list.add(new TVInputSource("hdmi1", "HDMI 1", TVInputSource.Type.HDMI1, true));

        // 3. HDMI 2
        list.add(new TVInputSource("hdmi2", "HDMI 2", TVInputSource.Type.HDMI2, true));

        // 4. HDMI 3
        list.add(new TVInputSource("hdmi3", "HDMI 3", TVInputSource.Type.HDMI3, true));

        // 5. AV Composite
        list.add(new TVInputSource("av", "AV Input", TVInputSource.Type.AV, true));

        // Detect dynamic TIF inputs if framework is active
        if (systemTvManager != null) {
            try {
                List<TvInputInfo> inputs = systemTvManager.getTvInputList();
                if (inputs != null) {
                    for (TvInputInfo info : inputs) {
                        String id = info.getId();
                        String label = info.loadLabel(context).toString();
                        if (id.toLowerCase().contains("hdmi")) {
                            // Can match or augment
                        }
                    }
                }
            } catch (Exception ignored) {}
        }

        return list;
    }

    public void switchInput(TVInputSource source, Context activityContext) {
        if (source == null) return;

        if (source.getType() == TVInputSource.Type.TUNER) {
            new LiveTVManager(context).launchLiveTV(activityContext);
            return;
        }

        // Try standard Android TV Input Framework passthrough URI
        boolean launched = false;
        try {
            Uri passthroughUri = TvContract.buildChannelUriForPassthroughInput("com.android.tv/" + source.getId());
            Intent intent = new Intent(Intent.ACTION_VIEW, passthroughUri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activityContext.startActivity(intent);
            launched = true;
        } catch (Exception ignored) {}

        // If TIF standard intent wasn't handled, try standard vendor hardware inputs for MStar / CVTE / Realtek
        if (!launched) {
            launched = tryVendorInputSwitch(source, activityContext);
        }

        if (!launched) {
            Toast.makeText(context, "Hardware TV Input (" + source.getTitle() +
                    ") requires vendor TV app or TV Source key on remote.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean tryVendorInputSwitch(TVInputSource source, Context activityContext) {
        String[] possibleIntents = {
                "com.mstar.tv.tvplayer.ui.TvPictureFrameActivity",
                "com.mstar.android.tv.SOURCE_CHANGE",
                "com.cvte.tv.SOURCE_CHANGE",
                "android.intent.action.VIEW_TV_SOURCE"
        };

        for (String act : possibleIntents) {
            try {
                Intent i = new Intent(act);
                i.putExtra("source", source.getId());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activityContext.startActivity(i);
                return true;
            } catch (Exception ignored) {}
        }
        return false;
    }
}
