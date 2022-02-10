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

public class ApiRecipesAdapter extends RecyclerView.Adapter<ApiRecipesAdapter.ViewHolder> {

    private List<RecipesApiResponse.Hit> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;
    private ApiRecipesInterface navigator;

    ApiRecipesAdapter(Context context, ApiRecipesInterface navigator) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.navigator = navigator;
    }

    public void updateList(List<RecipesApiResponse.Hit> recipeList) {
        mData = recipeList;
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
        RecipesApiResponse.Hit hit = mData.get(position);
        RecipesApiResponse.ApiRecipe recipe = hit.recipe;

        holder.name.setText(recipe.getLabel());

        holder.itemView.setOnClickListener(v -> navigator.goToRecipePage(recipe.getUrl()));
        holder.name.setOnClickListener(v -> navigator.goToRecipePage(recipe.getUrl()));

        if (recipe.image != null) {
            Picasso.get().load(recipe.image).transform(new CircleTransform())
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.ic_baseline_add_24).fit().centerCrop().into(holder.recipeImg);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
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

interface ApiRecipesInterface {
    void goToRecipePage(String url);
}