package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.quiz.databinding.ActivityUserStatusBinding;

public class UserStatus extends AppCompatActivity {
    ActivityUserStatusBinding binding;

    int score;

    private static final String PREFS_NAME = "QuizPrefs";
    private static final String HIGH_SCORE_KEY = "HighScore";
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityUserStatusBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        score = sharedPreferences.getInt(HIGH_SCORE_KEY, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();



        binding.playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserStatus.this, SelectCategory.class));
            }
        });

        binding.highestScore.setText(String.valueOf(score));

        binding.resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt(HIGH_SCORE_KEY, 0);
                editor.apply();
                recreate();
            }
        });

        binding.refreshScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
    }
}