package com.example.hadasrecipe;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class CustomAnimationDialog extends Dialog {
    Context context;
    String text;
    int resource;

    LottieAnimationView animationView;
    TextView loadingText;

    public CustomAnimationDialog(Context context) {
super(context);
        this.context = context;
    }

    public CustomAnimationDialog(Context context, int resource) {
        super(context);
        this.context = context;
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
        } else {
            loadingText.setVisibility(View.GONE);
        }

        if (resource != 0) {
            animationView.setAnimation(resource);
        }

        animationView.loop(false);

        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}
