package com.armi.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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
     * Adapter that coordinates with grid view to display movies
     */
    private MovieDataAdapter movieDataAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movieDataAdapter = new MovieDataAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);
        gridView = (GridView) rootView.findViewById(R.id.movie_grid);
        gridView.setAdapter(movieDataAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MovieData movieData = (MovieData) movieDataAdapter.getItem(i);
                Log.e("armiii", "MovieGridFragment#onItemClick:76 tapped movie - " + movieData.getMovieName());
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        new FetchMoviesTask().execute(TOP_RATED_RANKING);
    }

    /**
     * Gets Movies
     */
    public class FetchMoviesTask extends AsyncTask<String, Void, List<MovieData>> {

        /**
         * Used to get results from JSONObject of results
         */
        public static final String RESULTS_KEY = "results";

        /**
         * Used to get titles of JSONObject of movies
         */
        public static final String TITLE_KEY = "title";

        /**
         * Used to get poster URL from JSONObject of movies
         */
        public static final String POSTER_PATH_KEY = "poster_path";

        @Override
        protected List<MovieData> doInBackground(String... type) {
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
        protected void onPostExecute(List<MovieData> movieDataList) {
            movieDataAdapter.setMovieDataList(movieDataList);
            movieDataAdapter.notifyDataSetChanged();
        }

        /**
         * Gets movies in the list
         *
         * @param jsonString JSON string with information to parse
         * @return List of movie data
         */
        private List<MovieData> parseMovieData(String jsonString) {
            List<MovieData> movieDataList = new ArrayList<>();
            try {
                Log.e("armiii", "FetchMoviesTask#parseMovieData:171 movies - " + jsonString);
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray movieArray = jsonObject.getJSONArray(RESULTS_KEY);

                for (int i = 0; i < movieArray.length(); i++) {
                    JSONObject movieInfo = movieArray.getJSONObject(i);
                    String title = movieInfo.getString(TITLE_KEY);
                    String url = movieInfo.getString(POSTER_PATH_KEY);
                    movieDataList.add(new MovieData(title, url));
                }
            } catch (JSONException e) {
                Log.e(getClass().toString(), "parseMovieData: could not parse movie data", e);
            }

            return movieDataList;
        }
    }
}
