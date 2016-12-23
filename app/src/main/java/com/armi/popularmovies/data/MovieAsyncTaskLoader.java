package com.armi.popularmovies.data;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.armi.popularmovies.MovieData;

/**
 * AsyncTaskLoader to get movie information from database
 */
public class MovieAsyncTaskLoader extends AsyncTaskLoader<MovieData> {

    /**
     * ID of movie getting fetched
     */
    private final String movieId;

    /**
     * DB helper used to fetch information
     */
    private final MovieDbHelper movieDbHelper;

    /**
     * cached movie data
     */
    private MovieData cachedMovieData;

    public MovieAsyncTaskLoader(Context context, String movieId) {
        super(context);
        this.movieId = movieId;
        this.movieDbHelper = MovieDbHelper.getHelper(context);
    }

    @Override
    public MovieData loadInBackground() {
        return movieDbHelper.getMovieData(movieId);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (cachedMovieData == null) {
            cachedMovieData = loadInBackground();
        }

        deliverResult(cachedMovieData);
    }
}
