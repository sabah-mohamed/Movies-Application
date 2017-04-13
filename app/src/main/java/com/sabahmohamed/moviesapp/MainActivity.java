package com.sabahmohamed.moviesapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Boolean tabMode = false;


    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("FavMovies", MODE_PRIVATE);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MoviesGridFragment movieGridFragment = new MoviesGridFragment();

        fragmentTransaction.add(R.id.gridFrameLayout, movieGridFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();

        if(findViewById(R.id.detailsFrameLayout)!= null){
            tabMode = true;
        }
    }
}
