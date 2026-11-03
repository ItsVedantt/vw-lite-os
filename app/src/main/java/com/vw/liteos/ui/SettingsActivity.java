package com.vw.liteos.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.vw.liteos.R;
import com.vw.liteos.manager.DeviceInfoManager;
import com.vw.liteos.manager.MemoryMonitor;
import com.vw.liteos.manager.RemoteNavigationManager;
import com.vw.liteos.model.DeviceInfo;

public class SettingsActivity extends AppCompatActivity {
    private TextView tvHardware;
    private Button btnCleanRam;
    private Button btnOpenNative;
    private Button btnReset;

    public static void start(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        findViewById(R.id.viewTabHome).setVisibility(android.view.View.GONE);
        findViewById(R.id.viewTabApps).setVisibility(android.view.View.GONE);
        findViewById(R.id.viewTabStore).setVisibility(android.view.View.GONE);
        findViewById(R.id.viewTabInputs).setVisibility(android.view.View.GONE);
        findViewById(R.id.viewTabIptv).setVisibility(android.view.View.GONE);
        findViewById(R.id.viewTabNetwork).setVisibility(android.view.View.GONE);
        findViewById(R.id.viewTabSettings).setVisibility(android.view.View.VISIBLE);

        tvHardware = findViewById(R.id.tvSettingsHardware);
        btnCleanRam = findViewById(R.id.btnSettingsCleanRam);
        btnOpenNative = findViewById(R.id.btnSettingsOpenNative);
        btnReset = findViewById(R.id.btnSettingsResetLauncher);

        DeviceInfo info = DeviceInfoManager.getDeviceInfo(this);
        String details = "Manufacturer: " + info.getManufacturer() +
                "\nModel: " + info.getModel() +
                "\nMotherboard: " + info.getBoard() +
                "\nCPU Cores: " + info.getCpuCores() +
                "\nGPU: " + info.getGpuRenderer() +
                "\nRAM Total / Free: " + info.getTotalRamMb() + " MB / " + info.getAvailRamMb() + " MB (Reported: 512 MB)" +
                "\nAndroid Version: " + info.getAndroidVersion() + " (Pie, API " + info.getApiLevel() + ")" +
                "\nFirmware Build: " + info.getFirmwareBuild();
        tvHardware.setText(details);

        btnCleanRam.setOnClickListener(v -> {
            MemoryMonitor.getInstance(this).trimMemory();
            Toast.makeText(this, "Memory trimmed and caches cleaned.", Toast.LENGTH_SHORT).show();
        });

        btnOpenNative.setOnClickListener(v -> {
            try {
                Intent i = new Intent(Settings.ACTION_SETTINGS);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            } catch (Exception e) {
                Toast.makeText(this, "Cannot open native Android settings.", Toast.LENGTH_SHORT).show();
            }
        });

        btnReset.setOnClickListener(v -> {
            getSharedPreferences("vw_liteos_apps", MODE_PRIVATE).edit().clear().apply();
            getSharedPreferences("vw_liteos_iptv", MODE_PRIVATE).edit().clear().apply();
            Toast.makeText(this, "Launcher preferences reset to defaults.", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (RemoteNavigationManager.isBackKey(keyCode)) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
