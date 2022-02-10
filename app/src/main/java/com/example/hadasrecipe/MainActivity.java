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

import androidx.annotation.NonNull;

import com.example.hadasrecipe.utils.BaseActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends BaseActivity {
    EditText editUsername;
    EditText editPassword;
    TextView submitBt;
    private FirebaseAuth mAuth;
    String TAG = MainActivity.class.getSimpleName();
    CustomLoadingDialog customLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        submitBt.setOnClickListener(v -> {
            verifyLogin(editUsername.getText().toString(), editPassword.getText().toString());
            customLoadingDialog.show();
        });
    }

    private void initViews() {
        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();

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

        TextView registerBt = findViewById(R.id.register_bt);
        registerBt.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), RegistrationPage.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle()));
    }

    private void checkFormValidations() {
        boolean isValid = isEmailValid(editUsername.getText().toString()) && !editPassword.getText().toString().isEmpty();
        submitBt.setEnabled(isValid);
    }

    private void verifyLogin(String username, String password) {
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, task -> {
                    customLoadingDialog.dismiss();
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(MainActivity.this, "Authentication Success!",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, HomePageActivity.class);
                        startActivity(intent);
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(MainActivity.this, Objects.requireNonNull(task.getException()).getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getIngredientsFromDb() {
        ingredientsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dbIngredientsList = new ArrayList<>();
                for (DataSnapshot s : snapshot.getChildren()) {
                    Ingredient ingredient = s.getValue(Ingredient.class);
                    dbIngredientsList.add(ingredient);
                }
                finishedFetchingFromDB();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCategoriesFromDb() {
        categoriesRef.get()
                .addOnSuccessListener(documentSnapshots -> {
                    if (documentSnapshots.isEmpty()) {
                        Log.d(TAG, "onSuccess: LIST EMPTY");
                    } else {
                        dbCategoriesList = documentSnapshots.toObjects(Category.class);
                        finishedFetchingFromDB();
                    }
                }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error getting data!!!", Toast.LENGTH_LONG).show());

    }

    private void finishedFetchingFromDB() {
        if (dbCategoriesList != null && dbIngredientsList != null) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                recipesRef = firebaseDatabase.getReference(currentUser.getUid() + "/recipes");
                Toast.makeText(MainActivity.this, "User: " + currentUser.getEmail(),
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, HomePageActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            }
            customLoadingDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        customLoadingDialog.dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        customLoadingDialog = showLoadingDialog(MainActivity.this);

        getCategoriesFromDb();
        getIngredientsFromDb();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}