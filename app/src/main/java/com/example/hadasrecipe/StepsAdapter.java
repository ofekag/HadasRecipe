package com.example.hadasrecipe;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.ViewHolder> {

    private List<Step> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    StepsAdapterInterface navigator;

    StepsAdapter(Context context, StepsAdapterInterface navigator) {
        this.mInflater = LayoutInflater.from(context);
        this.navigator = navigator;
    }

    public void updateList(Step step) {
        mData.add(step);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_editable_step, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Step step = mData.get(position);
        step.setPhase(position);
        holder.phase.setText(position+1 + ")");

        holder.description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                step.setDescription(s.toString());
                navigator.stepDescriptionChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        holder.removeStep.setOnClickListener(v -> {
            mData.remove(step);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public List<Step> getList() {
        return mData;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        EditText description;
        TextView phase;
        ImageView removeStep;


        ViewHolder(View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.step_description);
            phase = itemView.findViewById(R.id.step_phase);
            removeStep = itemView.findViewById(R.id.remove_step);
        }

    }
}

interface StepsAdapterInterface{
    void stepDescriptionChanged();
}