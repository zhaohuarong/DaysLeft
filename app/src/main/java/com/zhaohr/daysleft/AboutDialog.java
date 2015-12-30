package com.zhaohr.daysleft;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class AboutDialog extends Dialog {
    public AboutDialog(Context context) {
        super(context, R.style.dialog);

        View mView = LayoutInflater.from(getContext()).inflate(R.layout.about_dialog, null);
        super.setContentView(mView);

        initUI();
    }

    private void initUI() {
        TextView verView = (TextView)findViewById(R.id.app_version);

        try {
            PackageManager manager = getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getContext().getPackageName(), 0);
            verView.setText("V " + info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
