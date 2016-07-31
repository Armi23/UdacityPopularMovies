package com.armi.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    /**
     * TMDB API base URL
     */
    public static final String BASE_MOVIE_API_URL = "http://image.tmdb.org/t/p/";

    /**
     * Default recommended phone size
     */
    public static final String DEFAULT_IMAGE_SIZE = "w185";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
