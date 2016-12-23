package com.armi.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.armi.popularmovies.MovieData;
import com.armi.popularmovies.MovieDateFormatter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Manages db for movie information
 */
public class MovieDbHelper extends SQLiteOpenHelper{

    /**
     * File name for db
     */
    public static final String DATABASE_NAME = "movies.db";

    /**
     * First version of the database supported by app
     */
    public static final int INITIAL_DATABASE_VERSION = 1;

    /**
     * String used to split a string from the db into items of a list
     */
    private static final String DB_STORAGE_DELIMITER = "|";

    /**
     * String used to separate items in a query
     */
    private static final String DB_QUERY_DELIMITER = ",";

    /**
     * Private singleton instance of MovieDbHelper
     */
    private static MovieDbHelper helper;

    /**
     * Constructor
     *
     * @param context context used to make db
     */
    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, INITIAL_DATABASE_VERSION);
    }

    /**
     * Gets a singleton instance of the MovieDbHelper
     *
     * @param context context used for helper
     * @return MovieDbHelper
     */
    public static MovieDbHelper getHelper(Context context) {
        if (helper == null) {
            helper = new MovieDbHelper(context);
        }

        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(MovieContract.MovieEntry.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // No upgrade changes needed
    }

    /**
     * Records movie data for all the entries into the database
     *
     * @param movieDatas list of MovieData
     */
    public void recordMovieDatas(List<MovieData> movieDatas) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        for (MovieData movieData : movieDatas) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry._ID, movieData.getId());
            contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movieData.getTitle());
            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER, movieData.getPosterUrl());
            contentValues.put(MovieContract.MovieEntry.COLUMN_SUMMARY, movieData.getSummary());
            contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, movieData.getRating());
            contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, MovieDateFormatter.getDateFormatter().format(movieData.getReleaseDate()));
            contentValues.put(MovieContract.MovieEntry.COLUMN_REVIEWS, listToString(movieData.getUserReview(), DB_STORAGE_DELIMITER));
            contentValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_URLS, listToString(movieData.getTrailerUrls(), DB_STORAGE_DELIMITER));

            sqLiteDatabase.replace(MovieContract.MovieEntry.TABLE_NAME, null,contentValues);
        }

    }

    /**
     * Gets the movie data for a single movie id
     *
     * @param id ID of movie
     * @return data for movie
     */
    public MovieData getMovieData(String id) {
        List<String> idList = new ArrayList<>();
        idList.add(id);
        return getMovieDatas(idList).get(0);
    }

    /**
     * Gets a list of movie data for provided ids
     *
     * @param ids ID of movie
     * @return list of movie data
     */
    public List<MovieData> getMovieDatas(List<String> ids) {
        List<MovieData> movieDatas = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String selection = "_id IN (" + listToString(ids, DB_QUERY_DELIMITER) + ")";
        Cursor cursor = sqLiteDatabase.query(MovieContract.MovieEntry.TABLE_NAME, null, selection, null, null, null, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry._ID));
            String title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
            String posterUrl = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER));
            String summary = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_SUMMARY));
            double rating = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING));
            String releaseDateString = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
            Date releaseDate;
            try {
                releaseDate = MovieDateFormatter.getDateFormatter().parse(releaseDateString);
            } catch (ParseException e) {
                releaseDate = new Date();
                Log.e(getClass().toString(), "MovieDbHelper#getMovieDates: could not parse release date for " + id, e);
            }
            String reviewsString = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_REVIEWS));
            List<String> userReviews = stringToList(reviewsString, DB_STORAGE_DELIMITER);
            String trailerUrlsString = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TRAILER_URLS));
            List<String> trailerUrls = stringToList(trailerUrlsString, DB_STORAGE_DELIMITER);

            MovieData movieData = new MovieData(title, posterUrl, id, summary, rating, releaseDate);
            movieData.setUserReview(userReviews);
            movieData.setTrailerUrls(trailerUrls);
            movieDatas.add(movieData);
        }

        cursor.close();
        return movieDatas;
    }

    /**
     * Takes a list of strings and turns it into one string to be stored in DB
     *
     * @param strings list of strings
     * @param delimiter string used to separate information in db friendly string
     * @return a DB friendly string
     */
    private String listToString(List<String> strings, String delimiter) {
        StringBuilder stringBuilder = new StringBuilder();
        if (strings.isEmpty()) {
            return stringBuilder.toString();
        }

        stringBuilder.append(strings.get(0));
        for (int i = 1; i < strings.size(); i++) {
            stringBuilder.append(delimiter);
            stringBuilder.append(strings.get(i));
        }

        return stringBuilder.toString();
    }

    /**
     * Takes db friendly string and returns list of strings which were the values in the db friendly string
     *
     * @param string input string
     * @param delimiter string used to separate information in db friendly string
     * @return List of strings
     */
    private List<String> stringToList(String string, String delimiter) {
        return Arrays.asList(string.split(delimiter));
    }
}
