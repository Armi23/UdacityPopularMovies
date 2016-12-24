package com.armi.popularmovies;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.armi.popularmovies.data.MovieDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MovieGridFragment extends Fragment {

    /**
     * Base ranking URL for TMDB
     */
    public static final String MOVIES_RANKING_BASE_URL = "http://api.themoviedb.org/3";

    /**
     * URL addition needed to get popular movies
     */
    public static final String POPULAR_MOVIES_RANKING = "/movie/popular";

    /**
     * URL addition needed to get top rated movies
     */
    public static final String TOP_RATED_RANKING = "/movie/top_rated";

    /**
     * Used to set API KEY query parameter
     */
    public static final String API_KEY = "api_key";

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
    private MovieDataAdapter movieDataAdapter;

    /**
     * Item click listener for adapter
     */
    private AdapterView.OnItemClickListener itemClickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movieDataAdapter = new MovieDataAdapter(getContext(), null, 0);
        itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MovieDataAdapter.ViewHolder viewHolder = (MovieDataAdapter.ViewHolder) view.getTag();
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
        gridView.setAdapter(movieDataAdapter);
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
            new FetchMoviesTask().execute(POPULAR_MOVIES_RANKING);
        } else {
            new FetchMoviesTask().execute(TOP_RATED_RANKING);
        }
    }

    /**
     * Gets Movies
     */
    public class FetchMoviesTask extends AsyncTask<String, Void, Cursor> {

        /**
         * Used to get array of movie data from API response
         */
        public static final String RESULTS_KEY = "results";

        /**
         * Used to get titles of movie
         */
        public static final String TITLE_KEY = "original_title";

        /**
         * Used to get poster URL
         */
        public static final String POSTER_PATH_KEY = "poster_path";

        /**
         * Used to get movie DI
         */
        public static final String ID_KEY = "id";

        /**
         * Used to get a movie's summary
         */
        public static final String SUMMARY_KEY = "overview";

        /**
         * Used to get the user rating
         */
        public static final String USER_RATING_KEY = "vote_average";

        /**
         * Used to get release date
         */
        public static final String RELEASE_DATE_KEY = "release_date";

        @Override
        protected void onPreExecute() {
            loadingBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Cursor doInBackground(String... type) {
            String rankingType = type[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String popularMovies = null;

            try {
                Uri.Builder builder = Uri.parse(MOVIES_RANKING_BASE_URL + rankingType).buildUpon();
                builder.appendQueryParameter(API_KEY, BuildConfig.MOVIE_API_KEY);
                URL url = new URL(builder.build().toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    Log.e(getClass().toString(), "Input stream was null, could not retrieve movies");
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                popularMovies = buffer.toString();

            } catch (IOException e) {
                Log.e(getClass().toString(), "Exception thrown while fetching popular movies", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(getClass().toString(), "Exception thrown while fetching popular movies", e);
                    }
                }
            }

            return parseMovieData(popularMovies);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor == null) {
                showMovieLoadingErrorDialog();
            }
            movieDataAdapter.swapCursor(cursor);
            loadingBar.setVisibility(View.GONE);
        }

        /**
         * Gets movies in the list
         *
         * @param jsonString JSON string with information to parse
         * @return List of movie data
         */
        private Cursor parseMovieData(String jsonString) {
            List<MovieData> movieDataList = new ArrayList<>();
            List<String> idList = new ArrayList<>();
            if (TextUtils.isEmpty(jsonString)) {
                return null;
            }

            DateFormat dateFormatter = MovieDateFormatter.getDateFormatter();
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray movieArray = jsonObject.getJSONArray(RESULTS_KEY);

                for (int i = 0; i < movieArray.length(); i++) {
                    JSONObject movieInfo = movieArray.getJSONObject(i);
                    String title = movieInfo.getString(TITLE_KEY);
                    String url = movieInfo.getString(POSTER_PATH_KEY);
                    String id = movieInfo.getString(ID_KEY);
                    String summary = movieInfo.getString(SUMMARY_KEY);
                    double rating = movieInfo.getDouble(USER_RATING_KEY);
                    Date releaseDate = dateFormatter.parse(movieInfo.getString(RELEASE_DATE_KEY));
                    movieDataList.add(new MovieData(title, url, id, summary, rating, releaseDate));
                    idList.add(id);
                }
            } catch (JSONException | ParseException e) {
                Log.e(getClass().toString(), "parseMovieData: could not parse movie data", e);
            }

            MovieDbHelper movieDbHelper = MovieDbHelper.getHelper(getContext());
            movieDbHelper.recordMovieDatas(movieDataList);

            return movieDbHelper.getMovieDatasCursor(idList);
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
