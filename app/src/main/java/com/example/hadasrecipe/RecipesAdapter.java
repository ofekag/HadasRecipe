package com.example.hadasrecipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    private List<Recipe> mData = new ArrayList<>();
    private List<Recipe> filteredList = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;
    private RecipesInterface navigator;

    String filteredCategory;
    Boolean filteredIsFavorite = false;

    RecipesAdapter(Context context, RecipesInterface navigator) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.navigator = navigator;
    }

    public void updateList(List<Recipe> recipeList) {
        mData = recipeList;
        filteredList = recipeList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Recipe recipe = mData.get(position);
        holder.name.setText(recipe.getName());
        holder.ingredients.setText(recipe.getIngredientsNames());
        holder.stepsNumber.setText(context.getString(R.string.steps_number, recipe.getSteps().size()));

        if (recipe.imageUri != null) {
            Picasso.get().load(recipe.imageUri).transform(new CircleTransform())
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.ic_baseline_add_24).fit().centerCrop().into(holder.recipeImg);
        }

        if (recipe.getIsFavorite()) {
            holder.favoriteRecipe.setImageResource(R.drawable.ic_baseline_favorite_24);
        } else {
            holder.favoriteRecipe.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        }

        holder.favoriteRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipe.setFavorite(!recipe.isFavorite);
                navigator.updateRecipe(recipe);
            }
        });

        holder.deleteRecipe.setOnClickListener(v -> navigator.deleteRecipe(recipe.getId()));

        holder.itemView.setOnClickListener(v -> {
            navigator.goToRecipePage(recipe.getId());
        });

        holder.name.setOnClickListener(v -> {
            navigator.goToRecipePage(recipe.getId());
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
//        return mData.size();
    }

    public void getFilteredList(String category) {
        filteredCategory = category;
        if (!filteredIsFavorite) {
            filteredList = mData.stream().filter(recipe -> recipe.getCategory().getName().equals(category)).collect(Collectors.toList());
        } else {
            filteredList = mData.stream().filter(recipe -> recipe.getCategory().getName().equals(category) && recipe.getIsFavorite()).collect(Collectors.toList());
        }

        notifyDataSetChanged();
    }

    public void resetFilter() {
        filteredCategory = null;
        filteredList = mData;
        notifyDataSetChanged();
    }

    public boolean isFilteredByFavorite() {
        return filteredIsFavorite;
    }

    public void filterByIsFavorite(boolean isFavorite) {
        filteredIsFavorite = isFavorite;
        if (filteredCategory == null) {
            if (isFavorite) {
                filteredList = mData.stream().filter(Recipe::getIsFavorite).collect(Collectors.toList());
            } else {
                filteredList = mData;
            }
        } else {
            if (isFavorite) {
                filteredList = mData.stream().filter(recipe -> recipe.getCategory().getName().equals(filteredCategory) && recipe.getIsFavorite()).collect(Collectors.toList());
            } else {
                filteredList = mData.stream().filter(recipe -> recipe.getCategory().getName().equals(filteredCategory)).collect(Collectors.toList());
            }
        }

        notifyDataSetChanged();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, ingredients, stepsNumber, deleteRecipe;
        ImageView recipeImg, favoriteRecipe;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.recipe_name);
            ingredients = itemView.findViewById(R.id.ingredients_text);
            stepsNumber = itemView.findViewById(R.id.step_number);
            recipeImg = itemView.findViewById(R.id.recipe_img);
            favoriteRecipe = itemView.findViewById(R.id.favorite_recipe);
            deleteRecipe = itemView.findViewById(R.id.delete_recipe);
        }

    }
}

interface RecipesInterface {
    void goToRecipePage(String id);

    void updateRecipe(Recipe recipe);

    void deleteRecipe(String id);
}