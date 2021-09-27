package com.example.survirun.activity.account;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.example.survirun.R;

public class ProgressDialog extends Dialog {
    public ProgressDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_progress_dialog);
    }
}