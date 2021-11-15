package com.example.survirun.activity.account;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Window;
import android.widget.ImageView;

import com.example.survirun.R;

public class ProgressDialog extends Dialog {
    public ProgressDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_progress_dialog);

        ImageView iv = findViewById(R.id.imageView);
        final AnimationDrawable drawable =
                (AnimationDrawable) iv.getBackground();
        drawable.start();
    }
}