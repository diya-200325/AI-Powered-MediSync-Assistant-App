package com.example.sympto;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class SeasonalDiseasesAdapter extends ArrayAdapter<SeasonalDisease> {

    private Context context;
    private List<SeasonalDisease> diseases;

    public SeasonalDiseasesAdapter(Context context, List<SeasonalDisease> diseases) {
        super(context, 0, diseases);
        this.context = context;
        this.diseases = diseases;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false);
        }

        SeasonalDisease disease = diseases.get(position);

        TextView diseaseName = convertView.findViewById(R.id.diseaseName);
        TextView diseasePrecautions = convertView.findViewById(R.id.diseasePrecautions);

        diseaseName.setText(disease.getName());
        diseasePrecautions.setText(TextUtils.join(", ", disease.getPrecautions()));

        return convertView;
    }
}