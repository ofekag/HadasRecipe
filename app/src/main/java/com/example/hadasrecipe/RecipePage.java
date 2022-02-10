package com.example.hadasrecipe;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.hadasrecipe.utils.BaseActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class RecipePage extends BaseActivity {

    Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_page);

        Intent intent = getIntent();
        listenToDocumentUpdate(intent.getStringExtra("recipeId"));
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(recipe.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView recipeName = findViewById(R.id.recipe_name);
        recipeName.setText(recipe.getName());

        ImageView isRecipeFavorite = findViewById(R.id.is_favorite_recipe);

        if (recipe.isFavorite) {
            isRecipeFavorite.setImageResource(R.drawable.ic_baseline_favorite_24);
        } else {
            isRecipeFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        }

        isRecipeFavorite.setOnClickListener(v -> {
            recipe.setFavorite(!recipe.isFavorite);

            if (recipe.isFavorite) {
                showAnimationDialog(RecipePage.this, R.raw.heart);
            } else {
                showAnimationDialog(RecipePage.this, R.raw.broken_hear);
            }

            recipesRef.child(recipe.getId()).setValue(recipe);
        });

        ImageView recipeImg = findViewById(R.id.recipe_img);
        if (recipe.imageUri != null) {
            Picasso.get().load(recipe.getImageUri())
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.progress_animation)
                    .into(recipeImg);
        }

        IngredientsListAdapter ingredientsListAdapter = new IngredientsListAdapter(getApplicationContext(), R.layout.item_ingredient, recipe.getIngredients());
        ListView ingredientsListView = findViewById(R.id.ingredients_list_view);
        ingredientsListView.setAdapter(ingredientsListAdapter);

        StepsListAdapter stepsListAdapter = new StepsListAdapter(getApplicationContext(), R.layout.item_step, recipe.getSteps());
        ListView stepsListView = findViewById(R.id.steps_list_view);
        stepsListView.setAdapter(stepsListAdapter);
    }

    private void listenToDocumentUpdate(String recipeId) {
        CustomLoadingDialog customLoadingDialog = showLoadingDialog(RecipePage.this);

        recipesRef.child(recipeId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipe = snapshot.getValue(Recipe.class);
                if (recipe != null) {
                    recipe.setId(recipeId);
                    customLoadingDialog.dismiss();
                    initView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RecipePage.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
                customLoadingDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}