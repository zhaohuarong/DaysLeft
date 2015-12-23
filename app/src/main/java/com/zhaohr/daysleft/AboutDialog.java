package com.zhaohr.daysleft;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class AboutDialog extends Dialog {
    public AboutDialog(Context context) {
        super(context, R.style.dialog);

        View mView = LayoutInflater.from(getContext()).inflate(R.layout.about_dialog, null);
        super.setContentView(mView);
    }
}
