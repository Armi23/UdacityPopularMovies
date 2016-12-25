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
import com.armi.popularmovies.data.Review;
import com.armi.popularmovies.data.Trailer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
    public static final String MOVIES_RANKING_BASE_URL = "http://api.themoviedb.org/3/movie/";

    /**
     * URL addition needed to get popular movies
     */
    public static final String POPULAR_MOVIES_RANKING = "popular";

    /**
     * URL addition needed to get top rated movies
     */
    public static final String TOP_RATED_RANKING = "top_rated";

    /**
     * URL addition needed to get trailers for movies
     */
    public static final String VIDEOS_PATH = "/videos";

    /**
     * URL addition needed to get user reviews for movie
     */
    public static final String REVIEW_PATH = "/reviews";

    /**
     * Used to set API KEY query parameter
     */
    public static final String API_KEY = "api_key";

    /**
     * TMDB API base URL
     */
    public static final String BASE_MOVIE_IMAGE_API_URL = "http://image.tmdb.org/t/p/";

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
     * Gson used for parsing API responses
     */
    private static Gson gson;

    /**
     * Returns url to get default size poster for movie
     *
     * @param url part of URL of movie to get images
     * @return URL to get default size poster
     */
    public static String getDefaultSizePosterUrl(String url) {
        return BASE_MOVIE_IMAGE_API_URL + DEFAULT_IMAGE_SIZE + url;
    }

    /**
     * Returns Gson instance to use for parsing
     *
     * @return Gson instance
     */
    private static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder().create();
        }

        return gson;
    }

    /**
     * Gets movie data, puts it in data base, and returns cursor to that data
     *
     * @param rankingType type of franking to fetch
     * @return Cursor to movie data in db
     */
    public static Cursor fetchMovieData(String rankingType, Context context) {
        String popularMovies = null;

        try {
            Uri.Builder builder = Uri.parse(MOVIES_RANKING_BASE_URL + rankingType).buildUpon();
            builder.appendQueryParameter(API_KEY, BuildConfig.MOVIE_API_KEY);
            URL url = new URL(builder.build().toString());

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            popularMovies = response.body().string();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Exception thrown while fetching popular movies", e);
        }

        return parseMovieData(popularMovies, context);
    }

    /**
     * Gets trailers for this movie
     *
     * @param id ID of movie
     * @return list of Trailer objects
     */
    public static List<Trailer> fetchMovieTrailers(String id) {
        try {
            Uri.Builder builder = Uri.parse(MOVIES_RANKING_BASE_URL + id + VIDEOS_PATH).buildUpon();
            builder.appendQueryParameter(API_KEY, BuildConfig.MOVIE_API_KEY);
            URL url = new URL(builder.build().toString());

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            return parseMovieTrailers(response.body().string());

        } catch (IOException e) {
            Log.e(LOG_TAG, "Exception thrown while fetching popular movies", e);
        }
        return new ArrayList<>();
    }

    /**
     * Gets reviews of movie
     *
     * @param id ID of movie
     * @return list of reviews of movie
     */
    public static List<Review> fetchUserReviews(String id) {
        try {
            Uri.Builder builder = Uri.parse(MOVIES_RANKING_BASE_URL + id + REVIEW_PATH).buildUpon();
            builder.appendQueryParameter(API_KEY, BuildConfig.MOVIE_API_KEY);
            URL url = new URL(builder.build().toString());

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            return parseMovieReviews(response.body().string());

        } catch (IOException e) {
            Log.e(LOG_TAG, "Exception thrown while fetching popular movies", e);
        }
        return new ArrayList<>();
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

    /**
     * Parses out movie trailer information from JSON response
     *
     * @param jsonString JSON response from API request
     * @return list of Trailers
     */
    private static List<Trailer> parseMovieTrailers(String jsonString) {
        try {
            Log.e("armiii", "MovieDataApiClient#parseMovieTrailers:280 json string - " + jsonString);
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray trailerArray = jsonObject.getJSONArray(RESULTS_KEY);
            Trailer[] trailerList = getGson().fromJson(trailerArray.toString(), Trailer[].class);
            return Arrays.asList(trailerList);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "parseMovieTrailers: could not parse movie trailers", e);
        }

        return new ArrayList<>();
    }

    /**
     * Parses out review information from JSON response
     *
     * @param jsonString JSON response from API request
     * @return list of Reviews
     */
    private static List<Review> parseMovieReviews(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray reviewArray = jsonObject.getJSONArray(RESULTS_KEY);
            Review[] reviewList = getGson().fromJson(reviewArray.toString(), Review[].class);
            return Arrays.asList(reviewList);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "parseMovieTrailers: could not parse movie trailers", e);
        }

        return new ArrayList<>();
    }
}
