package com.vw.liteos.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * SettingsActivity provides shortcut access to VW Lite OS Settings
 * by delegating directly to LauncherActivity on the Settings tab.
 */
public class SettingsActivity extends AppCompatActivity {

    public static final String EXTRA_TARGET_TAB = "TARGET_TAB";

    public static void start(Context context) {
        Intent intent = new Intent(context, LauncherActivity.class);
        intent.putExtra(EXTRA_TARGET_TAB, "settings");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        start(this);
        finish();
    }
}
