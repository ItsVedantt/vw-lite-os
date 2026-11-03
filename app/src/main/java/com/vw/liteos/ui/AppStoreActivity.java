package com.vw.liteos.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vw.liteos.R;
import com.vw.liteos.adapter.StoreAppAdapter;
import com.vw.liteos.manager.APKCompatibilityChecker;
import com.vw.liteos.manager.APKDownloadManager;
import com.vw.liteos.manager.AppDiscoveryManager;
import com.vw.liteos.manager.PackageInstallerManager;
import com.vw.liteos.manager.RemoteNavigationManager;
import com.vw.liteos.model.StoreApp;

import java.io.File;
import java.util.List;

public class AppStoreActivity extends AppCompatActivity implements StoreAppAdapter.OnStoreAppClickListener {
    private EditText etSearch;
    private Button btnSearch;
    private Button btnClearCache;
    private LinearLayout layoutCategories;
    private RecyclerView rvStoreApps;
    private StoreAppAdapter adapter;

    private AppDiscoveryManager discoveryManager;
    private APKDownloadManager downloadManager;
    private PackageInstallerManager installerManager;
    private String selectedCategory = "Featured";

    public static void start(Context context) {
        Intent intent = new Intent(context, AppStoreActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher); // Can reuse or populate layout

        discoveryManager = new AppDiscoveryManager(this);
        downloadManager = new APKDownloadManager(this);
        installerManager = new PackageInstallerManager(this);

        setupViews();
        loadCategoryPills();
        loadApps();
    }

    private void setupViews() {
        etSearch = findViewById(R.id.etStoreSearch);
        btnSearch = findViewById(R.id.btnStoreSearch);
        btnClearCache = findViewById(R.id.btnStoreClearCache);
        layoutCategories = findViewById(R.id.layoutStoreCategories);
        rvStoreApps = findViewById(R.id.rvStoreApps);

        findViewById(R.id.viewTabHome).setVisibility(View.GONE);
        findViewById(R.id.viewTabApps).setVisibility(View.GONE);
        findViewById(R.id.viewTabStore).setVisibility(View.VISIBLE);
        findViewById(R.id.viewTabInputs).setVisibility(View.GONE);
        findViewById(R.id.viewTabIptv).setVisibility(View.GONE);
        findViewById(R.id.viewTabNetwork).setVisibility(View.GONE);
        findViewById(R.id.viewTabSettings).setVisibility(View.GONE);

        adapter = new StoreAppAdapter(this);
        rvStoreApps.setLayoutManager(new LinearLayoutManager(this));
        rvStoreApps.setAdapter(adapter);

        btnSearch.setOnClickListener(v -> performSearch());
        btnClearCache.setOnClickListener(v -> {
            downloadManager.clearCache();
            Toast.makeText(this, "Download cache cleared.", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadCategoryPills() {
        layoutCategories.removeAllViews();
        for (String cat : AppDiscoveryManager.CATEGORIES) {
            Button pill = new Button(this, null, 0, R.style.TvNavTab);
            pill.setText(cat);
            pill.setFocusable(true);
            pill.setClickable(true);
            RemoteNavigationManager.applyFocusAnimation(pill);

            pill.setOnClickListener(v -> {
                selectedCategory = cat;
                loadApps();
            });

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(0, 0, 10, 0);
            pill.setLayoutParams(lp);
            layoutCategories.addView(pill);
        }
    }

    private void loadApps() {
        List<StoreApp> apps = discoveryManager.getAppsByCategory(selectedCategory);
        adapter.setApps(apps);
    }

    private void performSearch() {
        String q = etSearch.getText().toString();
        List<StoreApp> apps = discoveryManager.search(q);
        adapter.setApps(apps);
    }

    @Override
    public void onAppClick(StoreApp app) {
        showDetailsDialog(app);
    }

    @Override
    public void onActionClick(StoreApp app) {
        if (app.isInstalled() && !app.isUpdateAvailable()) {
            Intent intent = getPackageManager().getLaunchIntentForPackage(app.getPackageName());
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } else {
            showDetailsDialog(app);
        }
    }

    private void showDetailsDialog(StoreApp app) {
        Dialog dialog = new Dialog(this, R.style.Theme_VwLiteOS_Translucent);
        dialog.setContentView(R.layout.dialog_app_details);

        TextView tvTitle = dialog.findViewById(R.id.tvDialogTitle);
        TextView tvDev = dialog.findViewById(R.id.tvDialogDev);
        TextView tvPkg = dialog.findViewById(R.id.tvDialogPkg);
        TextView tvDesc = dialog.findViewById(R.id.tvDialogDesc);
        TextView tvMeta = dialog.findViewById(R.id.tvDialogMeta);
        ProgressBar pb = dialog.findViewById(R.id.pbDialogDownload);
        TextView tvStatus = dialog.findViewById(R.id.tvDialogStatus);
        Button btnCancel = dialog.findViewById(R.id.btnDialogCancel);
        Button btnInstall = dialog.findViewById(R.id.btnDialogInstall);

        tvTitle.setText(app.getTitle());
        tvDev.setText(app.getDeveloper() + " • " + app.getCategory());
        tvPkg.setText(app.getPackageName() + " (" + app.getSource() + ")");
        tvDesc.setText(app.getDescription());
        tvMeta.setText("Size: " + app.getFormattedSize() + " • Target: Android 9.0+ • " + app.getSupportedAbis());

        btnCancel.setOnClickListener(v -> {
            downloadManager.cancelDownload();
            dialog.dismiss();
        });

        btnInstall.setOnClickListener(v -> {
            btnInstall.setEnabled(false);
            pb.setVisibility(View.VISIBLE);
            tvStatus.setVisibility(View.VISIBLE);
            tvStatus.setText("Connecting to " + app.getSource() + "…");

            downloadManager.downloadApk(app, new APKDownloadManager.DownloadCallback() {
                @Override
                public void onProgress(int progressPercent, long downloadedBytes, long totalBytes) {
                    pb.setProgress(progressPercent);
                    tvStatus.setText("Downloading: " + progressPercent + "% (" +
                            (downloadedBytes / (1024 * 1024)) + " MB / " + (totalBytes / (1024 * 1024)) + " MB)");
                }

                @Override
                public void onSuccess(File apkFile, String sha256) {
                    tvStatus.setText("Verifying package architecture and signature…");
                    APKCompatibilityChecker.CompatibilityResult res = APKCompatibilityChecker.checkApk(AppStoreActivity.this, apkFile);
                    if (res.isCompatible) {
                        tvStatus.setText("Compatibility Verified. Opening Package Installer…");
                        installerManager.installApk(apkFile, AppStoreActivity.this);
                        dialog.dismiss();
                    } else {
                        tvStatus.setText(res.summaryMessage);
                        btnInstall.setEnabled(true);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    tvStatus.setText("Error: " + errorMessage);
                    btnInstall.setEnabled(true);
                }
            });
        });

        dialog.show();
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
