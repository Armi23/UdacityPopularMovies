package com.armi.popularmovies.network;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.armi.popularmovies.BuildConfig;
import com.armi.popularmovies.MovieData;
import com.armi.popularmovies.MovieDateFormatter;
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

/**
 * Handles API calls made to get movie data
 */
public class MovieDataApiClient {

    /**
     * Log tag used to make logs
     */
    public static final String LOG_TAG = "MovieDataApiClient";

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
     * TMDB API base URL
     */
    public static final String BASE_MOVIE_API_URL = "http://image.tmdb.org/t/p/";

    /**
     * Default recommended phone size
     */
    public static final String DEFAULT_IMAGE_SIZE = "w185";

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

    /**
     * Returns url to get default size poster for movie
     *
     * @param url part of URL of movie to get images
     * @return URL to get default size poster
     */
    public static String getDefaultSizePosterUrl(String url) {
        return BASE_MOVIE_API_URL + DEFAULT_IMAGE_SIZE + url;
    }

    /**
     * Gets trailers for this movie
     *
     * @param id ID of movie
     * @return list of URLs for trailers for movie
     */
    public static List<String> fetchMovieTrailers(String id) {
        return null;
    }

    /**
     * Gets reviews of movie
     *
     * @param id ID of movie
     * @return list of reviews of movie
     */
    public static List<String> fetchUserReviews(String id) {
        return null;
    }

    /**
     * Gets movie data, puts it in data base, and returns cursor to that data
     *
     * @param rankingType type of franking to fetch
     * @return Cursor to movie data in db
     */
    public static Cursor fetchMovieData(String rankingType, Context context) {
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
                Log.e(LOG_TAG, "Input stream was null, could not retrieve movies");
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
            Log.e(LOG_TAG, "Exception thrown while fetching popular movies", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Exception thrown while fetching popular movies", e);
                }
            }
        }

        return parseMovieData(popularMovies, context);
    }

    /**
     * Gets movies in the list
     *
     * @param jsonString JSON string with information to parse
     * @return List of movie data
     */
    private static Cursor parseMovieData(String jsonString, Context context) {
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
            Log.e(LOG_TAG, "parseMovieData: could not parse movie data", e);
        }

        MovieDbHelper movieDbHelper = MovieDbHelper.getHelper(context);
        movieDbHelper.recordMovieDatas(movieDataList);

        return movieDbHelper.getMovieDatasCursor(idList);
    }
}
