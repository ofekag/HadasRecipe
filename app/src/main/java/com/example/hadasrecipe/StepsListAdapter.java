package com.example.hadasrecipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class StepsListAdapter extends ArrayAdapter<Step> {

    private final int resourceLayout;
    private final Context mContext;

    public StepsListAdapter(Context context, int resource, List<Step> items) {
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

        Step step = getItem(position);

        if (step != null) {
            TextView tt1 = v.findViewById(R.id.step_phase);
            TextView tt2 = v.findViewById(R.id.step_description);

            if (tt1 != null) {
                tt1.setText(String.valueOf(step.getPhase() + 1));
            }

            if (tt2 != null) {
                tt2.setText(step.getDescription());
            }
        }

        return v;
    }

}
