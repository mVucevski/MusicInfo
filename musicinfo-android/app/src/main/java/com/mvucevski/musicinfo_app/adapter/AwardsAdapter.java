package com.mvucevski.musicinfo_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mvucevski.musicinfo_app.R;
import com.mvucevski.musicinfo_app.adapter.viewholder.AwardViewHolder;
import com.mvucevski.musicinfo_app.model.Award;

import java.util.List;

public class AwardsAdapter extends RecyclerView.Adapter<AwardViewHolder> {

    private List<Award> awardsList;
    private Context context;

    public AwardsAdapter(List<Award> awardsList, Context context) {
        this.awardsList = awardsList;
        this.context = context;
    }

    @NonNull
    @Override
    public AwardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_award_item, parent, false);
        return new AwardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AwardViewHolder holder, int position) {
        holder.tvTitle.setText(awardsList.get(position).awardTitle);
        holder.tvAwardFor.setText(awardsList.get(position).awardFor);
        holder.tvYear.setText(awardsList.get(position).year);

    }

    @Override
    public int getItemCount() {
        return awardsList.size();
    }
}
