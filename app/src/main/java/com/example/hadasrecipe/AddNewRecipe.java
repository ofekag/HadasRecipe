package com.example.hadasrecipe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hadasrecipe.utils.BaseActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AddNewRecipe extends BaseActivity implements StepsAdapterInterface, EditableIngredientsAdapterInterface {

    String TAG = AddNewRecipe.class.getSimpleName();
    List<Ingredient> ingredientList = new ArrayList<>();
    RecyclerView ingredientsRV, stepsRV;
    TextView addIngredientBt, addStepBt;
    EditableIngredientsAdapter adapter;
    StepsAdapter stepsAdapter;
    Button saveRecipeButton;
    TextInputEditText recipeName;
    private int PICK_IMAGE_REQUEST = 10001;
    private int TAKE_PICTURE_REQUEST = 20002;
    ImageView recipeImg;
    Spinner categorySpinner;
    EditText recipeDescription;
    Uri imageuri;
    Category selectedCategory;
    CustomLoadingDialog customLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_recipe);
        initViews();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        addIngredientBt = findViewById(R.id.add_ingredient);
        addStepBt = findViewById(R.id.add_step);
        recipeName = findViewById(R.id.recipe_name);
        recipeDescription = findViewById(R.id.recipe_description);
        recipeImg = findViewById(R.id.recipe_img);
        categorySpinner = findViewById(R.id.category_spinner);

        List<Category> categoryList = new ArrayList<>(dbCategoriesList);
        categoryList.add(0, new Category(""));

        ArrayAdapter categoriesAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryList);
        categorySpinner.setAdapter(categoriesAdapter);
        categorySpinner.setSelection(0, false);

        ingredientsRV = findViewById(R.id.ingredients_list);
        adapter = new EditableIngredientsAdapter(getApplicationContext(), dbIngredientsList, this);
        ingredientsRV.setLayoutManager(new LinearLayoutManager(this));
        ingredientsRV.setAdapter(adapter);

        stepsRV = findViewById(R.id.steps_list);
        stepsAdapter = new StepsAdapter(getApplicationContext(), this);
        stepsRV.setLayoutManager(new LinearLayoutManager(this));
        stepsRV.setAdapter(stepsAdapter);

        saveRecipeButton = findViewById(R.id.save_recipe);

        recipeImg.setOnClickListener(v -> selectImage());

        addIngredientBt.setOnClickListener(v -> {
            ingredientList.add(new Ingredient());
            adapter.updateList(ingredientList);
        });

        addStepBt.setOnClickListener(v -> stepsAdapter.updateList(new Step()));

        saveRecipeButton.setOnClickListener(v -> uploadRecipeImage());

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category category = categoryList.get(position);
                Toast.makeText(getApplicationContext(), "Selected: " + category.getName(), Toast.LENGTH_SHORT).show();
                selectedCategory = category;
                checkFormValidation();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = categoryList.get(0);
                checkFormValidation();
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFormValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        recipeName.addTextChangedListener(textWatcher);
        recipeDescription.addTextChangedListener(textWatcher);

        checkFormValidation();
    }

    private void checkFormValidation() {
        boolean isValid = !Objects.requireNonNull(recipeName.getText()).toString().isEmpty() && !recipeDescription.getText().toString().isEmpty();

        if (selectedCategory == null || selectedCategory.getName().equals("")) {
            isValid = false;
        }

        for (Ingredient i : ingredientList) {
            if (i.getName().isEmpty() || i.getQuantity() == 0) {
                isValid = false;
                break;
            }
        }

        for (Step s : stepsAdapter.getList()) {
            if (s.description.isEmpty()) {
                isValid = false;
                break;
            }
        }

        saveRecipeButton.setEnabled(isValid);
    }

    private void saveRecipe(Uri uri) {
        Map<String, Object> docData = new HashMap<>();
        docData.put("name", Objects.requireNonNull(recipeName.getText()).toString());
        docData.put("description", recipeDescription.getText().toString());
        docData.put("ingredients", ingredientList);
        docData.put("steps", stepsAdapter.getList());
        if (uri != null) {
            docData.put("imageUri", uri.toString());
        } else {
            docData.put("imageUri", null);
        }
        docData.put("isFavorite", false);
        docData.put("category", selectedCategory);
        saveNewIngredients(ingredientList);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        recipesRef = firebaseDatabase.getReference(user.getUid() + "/recipes");

        String key = recipesRef.push().getKey();
        recipesRef.child(key).setValue(docData);
        customLoadingDialog.dismiss();
        finish();
    }

    private void uploadRecipeImage() {
        customLoadingDialog = showLoadingDialog(AddNewRecipe.this, "New Recipe :)", R.raw.cooking);
        if (imageuri != null) {
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child("images/" + UUID.randomUUID().toString());

            mStorageRef.putFile(imageuri).addOnSuccessListener(taskSnapshot -> mStorageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                final Uri downloadUrl = uri;
                saveRecipe(downloadUrl);
            }));
        } else {
            saveRecipe(null);
        }
    }

    private void saveNewIngredients(List<Ingredient> newIngredientList) {
        for (Ingredient ingredient : newIngredientList) {
            Ingredient i = dbIngredientsList.stream().filter(ingredient1 -> ingredient1.getName().toLowerCase(Locale.ROOT).equals(ingredient.getName().toLowerCase(Locale.ROOT))).findFirst().orElse(null);
            if (i == null) {
                Map<String, Object> docData = new HashMap<>();
                docData.put("name", ingredient.getName());
                String key = ingredientsRef.push().getKey();
                ingredientsRef.child(key).setValue(docData);
            }
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewRecipe.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Take Photo")) {
                Toast.makeText(getApplicationContext(), "Not Implemented Yet", Toast.LENGTH_SHORT).show();
            } else if (items[item].equals("Choose from Library")) {
                if (ActivityCompat.checkSelfPermission(AddNewRecipe.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddNewRecipe.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_REQUEST);
                } else {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            PICK_IMAGE_REQUEST);
                }

            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null &&
                data.getData() != null) {
            imageuri = data.getData();
            Picasso.get().load(imageuri).fit().centerCrop().into(recipeImg);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
            } else {
                //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void stepDescriptionChanged() {
        checkFormValidation();
    }

    @Override
    public void onIngredientChanged() {
        checkFormValidation();
    }
}
