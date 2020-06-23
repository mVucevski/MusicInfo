package com.mvucevski.musicinfo_app.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mvucevski.musicinfo_app.R;

public class AwardViewHolder extends RecyclerView.ViewHolder {
    public View view;
    public TextView tvTitle, tvAwardFor, tvYear;

    public AwardViewHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        tvTitle = view.findViewById(R.id.tvAwardTitle);
        tvAwardFor = view.findViewById(R.id.tvAwardFor);
        tvYear = view.findViewById(R.id.tvYear);
    }
}
