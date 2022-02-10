package com.example.hadasrecipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class IngredientsListAdapter extends ArrayAdapter<Ingredient> {

    private final int resourceLayout;
    private final Context mContext;

    public IngredientsListAdapter(Context context, int resource, List<Ingredient> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

        Ingredient ingredient = getItem(position);

        if (ingredient != null) {
            TextView tt1 = v.findViewById(R.id.ingredient_name);
            TextView tt2 = v.findViewById(R.id.ingredient_quantity);

            if (tt1 != null) {
                tt1.setText(ingredient.getName());
            }

            if (tt2 != null) {
                tt2.setText("\u2022" + ingredient.getQuantity());
            }
        }

        return v;
    }

}
