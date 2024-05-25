package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.quiz.databinding.ActivitySelectCategoryBinding;

public class SelectCategory extends AppCompatActivity {
    ActivitySelectCategoryBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivitySelectCategoryBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        binding.backFromCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.selectGK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectCategory.this,MainActivity.class);
                intent.putExtra("category","gk");
                startActivity(intent);
            }
        });

        binding.selectHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectCategory.this,MainActivity.class);
                intent.putExtra("category","his");
                startActivity(intent);
            }
        });

        binding.selectGeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectCategory.this,MainActivity.class);
                intent.putExtra("category","GEO");
                startActivity(intent);
            }
        });

        binding.selectScience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectCategory.this,MainActivity.class);
                intent.putExtra("category","SC");
                startActivity(intent);
            }
        });

        binding.selectLit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectCategory.this,MainActivity.class);
                intent.putExtra("category","LIT");
                startActivity(intent);
            }
        });

        binding.selectSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectCategory.this,MainActivity.class);
                intent.putExtra("category","SPT");
                startActivity(intent);
            }
        });

        binding.selectEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectCategory.this,MainActivity.class);
                intent.putExtra("category","ETR");
                startActivity(intent);
            }
        });

        binding.selectMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectCategory.this,MainActivity.class);
                intent.putExtra("category","music");
                startActivity(intent);
            }
        });

        binding.selectMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectCategory.this,MainActivity.class);
                intent.putExtra("category","movies");
                startActivity(intent);
            }
        });

        binding.selectTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectCategory.this,MainActivity.class);
                intent.putExtra("category","tv");
                startActivity(intent);
            }
        });

        binding.selectCPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectCategory.this,MainActivity.class);
                intent.putExtra("category","c");
                startActivity(intent);
            }
        });

        binding.selectJava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectCategory.this,MainActivity.class);
                intent.putExtra("category","ja");
                startActivity(intent);
            }
        });

        binding.selectPython.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectCategory.this,MainActivity.class);
                intent.putExtra("category","py");
                startActivity(intent);
            }
        });
    }
}