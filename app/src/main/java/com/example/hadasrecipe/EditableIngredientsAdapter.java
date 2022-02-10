package com.example.hadasrecipe;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EditableIngredientsAdapter extends RecyclerView.Adapter<EditableIngredientsAdapter.ViewHolder> {

    private List<Ingredient> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;
    List<Ingredient> ingredientArrayList;
    EditableIngredientsAdapterInterface navigator;

    EditableIngredientsAdapter(Context context, List<Ingredient> ingredientArrayList, EditableIngredientsAdapterInterface navigator) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.ingredientArrayList = ingredientArrayList;
        this.navigator = navigator;
    }

    public void updateList(List<Ingredient> ingredientList) {
        mData = ingredientList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_editable_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Ingredient ingredient = mData.get(position);

        ArrayAdapter<Ingredient> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_dropdown_item_1line, ingredientArrayList);
        holder.name.setAdapter(adapter);

        holder.name.setOnItemClickListener((parent, view, position1, id) -> {
            Ingredient selected = (Ingredient) parent.getAdapter().getItem(position1);
            ingredient.setName(selected.getName());
        });

        holder.name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ingredient.setName(s.toString());
            }
        });

        holder.quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    ingredient.setQuantity(Integer.parseInt(s.toString()));
                } else {
                    ingredient.setQuantity(0);
                }
                navigator.onIngredientChanged();
            }
        });

        holder.removeIngredient.setOnClickListener(v -> {
            mData.remove(ingredient);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AutoCompleteTextView name;
        EditText quantity;
        ImageView removeIngredient;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.ingredient_name);
            quantity = itemView.findViewById(R.id.ingredient_quantity);
            removeIngredient = itemView.findViewById(R.id.remove_ingredient);
        }

    }
}

interface EditableIngredientsAdapterInterface {
    void onIngredientChanged();
}