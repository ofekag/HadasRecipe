package com.example.hadasrecipe.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.view.View;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hadasrecipe.Category;
import com.example.hadasrecipe.CustomAlertDialog;
import com.example.hadasrecipe.CustomAnimationDialog;
import com.example.hadasrecipe.CustomLoadingDialog;
import com.example.hadasrecipe.Ingredient;
import com.example.hadasrecipe.RecipePage;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class BaseActivity extends AppCompatActivity {

    public FirebaseFirestore firebaseFirestore;
    public static CollectionReference categoriesRef;

    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference recipesRef;
    public static DatabaseReference ingredientsRef;

    public static List<Ingredient> dbIngredientsList;
    public static List<Category> dbCategoriesList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDb();
    }

    public void showAlertDialog(Context context, String title, String message, String positiveText, Runnable r) {
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(context, title, message, positiveText, r);
        makeTransparent(customAlertDialog);
        customAlertDialog.show();
    }

    public CustomLoadingDialog showLoadingDialog(Context context) {
        CustomLoadingDialog customLoadingDialog = new CustomLoadingDialog(context);
        makeTransparent(customLoadingDialog);
        customLoadingDialog.show();
        return customLoadingDialog;
    }

    public CustomLoadingDialog showLoadingDialog(Context context, String text, int resource) {
        CustomLoadingDialog customLoadingDialog = new CustomLoadingDialog(context, text, resource);
        makeTransparent(customLoadingDialog);
        customLoadingDialog.show();
        return customLoadingDialog;
    }

    public void showAnimationDialog(Context context, int resource) {
        CustomAnimationDialog customAnimationDialog = new CustomAnimationDialog(context, resource);
        makeTransparent(customAnimationDialog);
        customAnimationDialog.show();
    }

    protected void makeTransparent(Dialog dialog) {
        Window window = getWindow();
        if (window != null) {
            View decorView = window.getDecorView();

            Window windowDialog = dialog.getWindow();
            if (decorView != null && windowDialog != null) {
                View rootView = decorView.getRootView();
                if (rootView != null) {
                    RSBlurProcessor blurProcessor = new RSBlurProcessor(RenderScript.create(this));
                    if (rootView.getWidth() != 0) {
                        windowDialog.setBackgroundDrawable(new BitmapDrawable(getResources(), blurProcessor.blur(
                                BitmapUtil.getBitmapFromView(rootView), 15, 1)));
                    } else {
                        rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                            @Override
                            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                                windowDialog.setBackgroundDrawable(new BitmapDrawable(getResources(), blurProcessor.blur(
                                        BitmapUtil.getBitmapFromView(rootView), 15, 1)));
                                v.removeOnLayoutChangeListener(this);
                            }
                        });
                    }
                }
            }
        }
    }

    private void initDb() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        ingredientsRef = firebaseDatabase.getReference("ingredients");
        categoriesRef = firebaseFirestore.collection("category");
    }


    public boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
