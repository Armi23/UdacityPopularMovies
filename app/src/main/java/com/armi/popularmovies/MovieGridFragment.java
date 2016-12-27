package com.armi.popularmovies;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.armi.popularmovies.data.MovieDbHelper;
import com.armi.popularmovies.network.MovieDataApiClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class MovieGridFragment extends Fragment {

    /**
     * Favorite movie type
     */
    public static final String FAVORITE_MOVIES = "favorites";

    /**
     * GridView to display movies
     */
    private GridView gridView;

    /**
     * Reference to loading bar
     */
    private ProgressBar loadingBar;

    /**
     * Adapter that coordinates with grid view to display movies
     */
    private MovieGridAdapter movieGridAdapter;

    /**
     * Item click listener for adapter
     */
    private AdapterView.OnItemClickListener itemClickListener;

    /**
     * Reference to shared preferences
     */
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movieGridAdapter = new MovieGridAdapter(getContext(), null, 0);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MovieGridAdapter.ViewHolder viewHolder = (MovieGridAdapter.ViewHolder) view.getTag();
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                intent.putExtra(MovieDetailsActivity.MOVIE_ID_KEY, viewHolder.movieId);
                startActivity(intent);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);
        gridView = (GridView) rootView.findViewById(R.id.movie_grid);
        gridView.setAdapter(movieGridAdapter);
        gridView.setOnItemClickListener(itemClickListener);
        loadingBar = (ProgressBar) rootView.findViewById(R.id.loading_bar);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    /**
     * Loads the movies by the rankings saved in SharedPreferences
     */
    private void updateMovies() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String rankingType = preferences.getString(getString(R.string.ranking_pref_key), getString(R.string.pref_ranking_popular));
        if (rankingType.equals(getString(R.string.pref_ranking_popular))) {
            new FetchMoviesTask().execute(MovieDataApiClient.POPULAR_MOVIES_RANKING);
        } else if (rankingType.equals(getString(R.string.pref_ranking_top))) {
            new FetchMoviesTask().execute(MovieDataApiClient.TOP_RATED_RANKING);
        } else {
            new FetchMoviesTask().execute(FAVORITE_MOVIES);
        }
    }

    /**
     * Gets Movies
     */
    public class FetchMoviesTask extends AsyncTask<String, Void, Cursor> {

        @Override
        protected void onPreExecute() {
            loadingBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Cursor doInBackground(String... type) {
            String rankingType = type[0];
            if (rankingType.equals(FAVORITE_MOVIES)) {
                Set<String> idList = sharedPreferences.getStringSet(MovieDetailsActivity.FAVORITE_SET_KEY, new HashSet<String>());
                return MovieDbHelper.getHelper(getContext()).getMovieDatasCursor(new ArrayList<>(idList));
            }
            return MovieDataApiClient.fetchMovieData(rankingType, getContext());
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor == null) {
                showMovieLoadingErrorDialog();
            }
            movieGridAdapter.swapCursor(cursor);
            loadingBar.setVisibility(View.GONE);
        }
    }

    /**
     * Shows alert dialog if there was an error while getting movies
     */
    private void showMovieLoadingErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.movie_error_dialog_title);
        builder.setMessage(R.string.movie_error_dialog_message);
        builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateMovies();
            }
        });
        builder.show();
    }
}
