package com.example.hadasrecipe;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hadasrecipe.utils.BaseActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class RegistrationPage extends BaseActivity {

    String TAG = RegistrationPage.class.getSimpleName();
    EditText editUsername;
    EditText editPassword;
    TextView submitBt;

    CustomLoadingDialog customLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);
        editUsername = findViewById(R.id.edit_username);
        editPassword = findViewById(R.id.edit_password);
        submitBt = findViewById(R.id.submit_bt);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkFormValidations();
            }
        };

        editUsername.addTextChangedListener(textWatcher);
        editPassword.addTextChangedListener(textWatcher);

        submitBt.setOnClickListener(v -> {
            register(editUsername.getText().toString(), editPassword.getText().toString());
            customLoadingDialog = showLoadingDialog(RegistrationPage.this);
        });
    }

    private void checkFormValidations() {
        boolean isValid = isEmailValid(editUsername.getText().toString()) && editPassword.getText().toString().length() >= 6;
        submitBt.setEnabled(isValid);
    }

    private void register(String username, String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, task -> {
                    customLoadingDialog.dismiss();
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        startActivity(new Intent(getApplicationContext(), HomePageActivity.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegistrationPage.this, Objects.requireNonNull(task.getException()).getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (customLoadingDialog != null) {
            customLoadingDialog.dismiss();
        }
    }
}