package com.vw.liteos.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * AppStoreActivity provides shortcut access to the built-in VW App Store
 * by delegating directly to LauncherActivity on the App Store tab.
 */
public class AppStoreActivity extends AppCompatActivity {

    public static final String EXTRA_TARGET_TAB = "TARGET_TAB";

    public static void start(Context context) {
        Intent intent = new Intent(context, LauncherActivity.class);
        intent.putExtra(EXTRA_TARGET_TAB, "store");
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
