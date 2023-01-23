package com.divtec.blatnoa.snakegame.Leaderboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.divtec.blatnoa.snakegame.R;

public class RankingView extends RecyclerView.ViewHolder {

    private TextView rankingTextView;
    private TextView nameTextView;
    private TextView scoreTextView;
    private TextView timeDateTextView;

    private boolean isTitle;

    public RankingView(View view) {
        super(view);

        rankingTextView = view.findViewById(R.id.txt_ranking);
        nameTextView = view.findViewById(R.id.txt_name);
        scoreTextView = view.findViewById(R.id.txt_score);
        timeDateTextView = view.findViewById(R.id.txt_date);

        initComponent();
    }

    private void initComponent() {
        // Set the style of the view
        if (isTitle) {
            rankingTextView.setTypeface(null, Typeface.BOLD);
            nameTextView.setTypeface(null, Typeface.BOLD);
            scoreTextView.setTypeface(null, Typeface.BOLD);
            timeDateTextView.setTypeface(null, Typeface.BOLD);

            setData("Ranking", "Name", "Score", "Date");
        }
    }

    /**
     * Set if the view is a title
     * @param isTitle Whether the view is a title
     */
    public void setIsTitle(boolean isTitle) {
        this.isTitle = isTitle;
        initComponent();
    }

    public void setData(String ranking, String name, String score, String timeDate) {
        rankingTextView.setText(ranking);
        nameTextView.setText(name);
        scoreTextView.setText(score);
        timeDateTextView.setText(timeDate);
    }
}
