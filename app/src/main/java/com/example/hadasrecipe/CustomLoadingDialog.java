package com.example.hadasrecipe;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class CustomLoadingDialog extends Dialog {
    Context context;
    String text;
    int resource;

    LottieAnimationView animationView;
    TextView loadingText;

    public CustomLoadingDialog(Context context) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.context = context;
    }

    public CustomLoadingDialog(Context context, String text, int resource) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.context = context;
        this.text = text;
        this.resource = resource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_loading_dialog);

        loadingText = findViewById(R.id.loading_text);
        animationView = findViewById(R.id.animation_view);

        if (text != null) {
            loadingText.setText(text);
        }

        if (resource != 0) {
            animationView.setAnimation(resource);
        }
    }
}
