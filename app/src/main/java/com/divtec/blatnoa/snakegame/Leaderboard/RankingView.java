package com.divtec.blatnoa.snakegame.Leaderboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.divtec.blatnoa.snakegame.R;

public class RankingView extends LinearLayout {

    private TextView rankingTextView;
    private TextView nameTextView;
    private TextView scoreTextView;
    private TextView timeDateTextView;

    boolean isTitle = false;

    public RankingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        isTitle = attrs.getAttributeBooleanValue(R.styleable.RankingView_isTitle, false);

        rankingTextView = findViewById(R.id.txt_ranking);
        nameTextView = findViewById(R.id.txt_name);
        scoreTextView = findViewById(R.id.txt_final_score);
        timeDateTextView = findViewById(R.id.txt_date);

        initComponent();
    }

    private void initComponent() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.leaderboard_ranking, this);

        if (isTitle) {
            rankingTextView.setTypeface(null, Typeface.BOLD);
            nameTextView.setTypeface(null, Typeface.BOLD);
            scoreTextView.setTypeface(null, Typeface.BOLD);
            timeDateTextView.setTypeface(null, Typeface.BOLD);

            rankingTextView.setText("Ranking");
            nameTextView.setText("Name");
            scoreTextView.setText("Score");
            timeDateTextView.setText("Date");
        }
    }
}
