package com.armi.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MovieGridFragment extends Fragment {

    /**
     * TMDB API base URL
     */
    public static final String BASE_MOVIE_API_URL = "http://image.tmdb.org/t/p/";

    public static final String POPULAR_MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/popular";

    /**
     * Default recommended phone size
     */
    public static final String DEFAULT_IMAGE_SIZE = "w185";

    /**
     * Used to set API KEY query parameter
     */
    public static final String API_KEY = "api_key";

    TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);
        textView = (TextView) rootView.findViewById(R.id.text_movie_grid);
//        textView.setText(fetchPopularMovies());

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        new FetchMoviesTask().execute();
    }

    public class FetchMoviesTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String popularMovies = null;

            try {
                Uri.Builder builder = Uri.parse(POPULAR_MOVIES_BASE_URL).buildUpon();
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

            Log.e("armiii", "FetchMoviesTask#doInBackground:101 movies - "  + popularMovies);
            return popularMovies;
        }

        @Override
        protected void onPostExecute(String s) {
            textView.setText(s);
        }
    }
}
