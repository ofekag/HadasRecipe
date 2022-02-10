package com.example.hadasrecipe;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAlertDialog extends Dialog {
    Context context;
    String title;
    String message;
    String positiveText;
    Runnable r;
    TextView positiveBt, no;

    TextView titleTV, messageTV;
    ImageView cancelBt;

    public CustomAlertDialog(Context context, String title, String message, String positiveText, Runnable r) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.context = context;

        this.title = title;
        this.message = message;

        this.positiveText = positiveText;
        this.r = r;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_alert_dialog);

        cancelBt = findViewById(R.id.cancel_up);
        cancelBt.setOnClickListener(v -> dismiss());

        titleTV = findViewById(R.id.title);
        titleTV.setText(title);

        messageTV = findViewById(R.id.message);
        messageTV.setText(message);

        positiveBt = findViewById(R.id.positive_bt);
        positiveBt.setText(positiveText);
        positiveBt.setOnClickListener(v -> {
            r.run();
            dismiss();
        });

        no = findViewById(R.id.cancel_horizontal);
        no.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }
}
