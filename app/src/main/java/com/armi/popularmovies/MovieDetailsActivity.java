package com.armi.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Activity that holds details about a movie
 */
public class MovieDetailsActivity extends AppCompatActivity {

    /**
     * Key to access extra with movie ID
     */
    public static final String MOVIE_ID_KEY = "movie_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_details_container, new MovieDetailsFragment())
                    .commit();
        }
    }
}
