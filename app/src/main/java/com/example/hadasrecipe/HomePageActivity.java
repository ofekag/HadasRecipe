package com.example.hadasrecipe;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hadasrecipe.utils.BaseActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends BaseActivity implements RecipesInterface {

    String TAG = HomePageActivity.class.getSimpleName();
    List<Recipe> recipesList = new ArrayList<>();

    RecyclerView recipesRV;
    RecipesAdapter adapter;
    ImageView isFavoriteFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        isFavoriteFilter = findViewById(R.id.is_favorite_filter);
        isFavoriteFilter.setOnClickListener(v -> {
            if (adapter.isFilteredByFavorite()) {
                adapter.filterByIsFavorite(false);
                isFavoriteFilter.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            } else {
                adapter.filterByIsFavorite(true);
                isFavoriteFilter.setImageResource(R.drawable.ic_baseline_favorite_24);
            }
        });

        FloatingActionButton addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AddNewRecipe.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle()));

        FloatingActionButton searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ApiRecipesPage.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle()));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_exit_to_app_24);

        recipesRV = findViewById(R.id.recipes_list);
        adapter = new RecipesAdapter(getApplicationContext(), this);
        recipesRV.setLayoutManager(new LinearLayoutManager(this));
        recipesRV.setAdapter(adapter);

        Spinner categoriesSpinner = findViewById(R.id.category_spinner);
        List<Category> categoryList = new ArrayList<>(dbCategoriesList);
        categoryList.add(0, new Category("All"));

        ArrayAdapter categoriesAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryList);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesSpinner.setAdapter(categoriesAdapter);

        categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    adapter.resetFilter();
                } else {
                    adapter.getFilteredList(categoryList.get(position).getName());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getData();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            showAlertDialog(HomePageActivity.this, "Are You Sure?", "Want To Log Out?", "Yes", () -> {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private void getData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            recipesRef = firebaseDatabase.getReference(user.getUid() + "/recipes");
            recipesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    recipesList.clear();
                    for (DataSnapshot s : snapshot.getChildren()) {
                        Recipe recipe = s.getValue(Recipe.class);
                        recipe.setId(s.getKey());
                        recipesList.add(recipe);
                    }
                    adapter.updateList(recipesList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(HomePageActivity.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        showAlertDialog(HomePageActivity.this, "title", "message", "Exit", this::finishAffinity);
    }

    @Override
    public void goToRecipePage(String id) {
        Intent intent = new Intent(getApplicationContext(), RecipePage.class);
        intent.putExtra("recipeId", id);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    @Override
    public void updateRecipe(Recipe recipe) {
        if(recipe.isFavorite){
            showAnimationDialog(HomePageActivity.this, R.raw.heart);
        }else{
            showAnimationDialog(HomePageActivity.this, R.raw.broken_hear);
        }
        recipesRef.child(recipe.getId()).setValue(recipe);
    }

    @Override
    public void deleteRecipe(String id) {
        showAlertDialog(HomePageActivity.this, "Are You Sure?", "Delete Recipe?", "Delete", () -> {
            recipesRef.child(id).removeValue();
        });

    }
}

