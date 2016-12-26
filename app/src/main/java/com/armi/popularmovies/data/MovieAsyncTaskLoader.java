package com.armi.popularmovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.AsyncTaskLoader;

import com.armi.popularmovies.MovieData;
import com.armi.popularmovies.MovieDetailsActivity;
import com.armi.popularmovies.network.MovieDataApiClient;

import java.util.HashSet;
import java.util.Set;

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

    /**
     * Reference to shared prefs
     */
    private SharedPreferences sharedPreferences;

    public MovieAsyncTaskLoader(Context context, String movieId) {
        super(context);
        this.movieId = movieId;
        this.movieDbHelper = MovieDbHelper.getHelper(context);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    @Override
    public MovieData loadInBackground() {
        MovieData movieData = movieDbHelper.getMovieData(movieId);
        movieData.setTrailerUrls(MovieDataApiClient.fetchMovieTrailers(movieData.getId()));
        movieData.setUserReview(MovieDataApiClient.fetchUserReviews(movieData.getId()));
        Set<String> favorites = sharedPreferences.getStringSet(MovieDetailsActivity.FAVORITE_SET_KEY, new HashSet<String>());
        movieData.setFavorite(favorites.contains(movieData.getId()));

        cachedMovieData = movieData;
        return cachedMovieData;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (cachedMovieData != null) {
            deliverResult(cachedMovieData);
            return;
        }

        forceLoad();
    }

}
