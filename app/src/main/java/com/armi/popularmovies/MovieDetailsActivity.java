package com.armi.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Activity that holds details about a movie
 */
public class MovieDetailsActivity extends AppCompatActivity {

    /**
     * Movie data parcel key
     */
    public static final String MOVIE_DATA_PARCEL_KEY = "movie_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.movie_details_container, new MovieDetailsFragment())
                .commit();
    }
}
