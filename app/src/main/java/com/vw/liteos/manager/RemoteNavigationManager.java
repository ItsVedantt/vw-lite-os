package com.vw.liteos.manager;

import android.view.KeyEvent;
import android.view.View;

import androidx.core.view.ViewCompat;

public class RemoteNavigationManager {

    public static void applyFocusAnimation(View view) {
        if (view == null) return;
        view.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                ViewCompat.animate(v)
                        .scaleX(1.06f)
                        .scaleY(1.06f)
                        .translationZ(8f)
                        .setDuration(120)
                        .start();
            } else {
                ViewCompat.animate(v)
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .translationZ(0f)
                        .setDuration(120)
                        .start();
            }
        });
    }

    public static boolean isConfirmKey(int keyCode) {
        return keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                || keyCode == KeyEvent.KEYCODE_ENTER
                || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER
                || keyCode == KeyEvent.KEYCODE_BUTTON_A;
    }

    public static boolean isBackKey(int keyCode) {
        return keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_ESCAPE;
    }

    public static boolean isChannelUpKey(int keyCode) {
        return keyCode == KeyEvent.KEYCODE_CHANNEL_UP
                || keyCode == KeyEvent.KEYCODE_PAGE_UP;
    }

    public static boolean isChannelDownKey(int keyCode) {
        return keyCode == KeyEvent.KEYCODE_CHANNEL_DOWN
                || keyCode == KeyEvent.KEYCODE_PAGE_DOWN;
    }
}
