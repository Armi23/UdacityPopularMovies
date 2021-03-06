package com.armi.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.armi.popularmovies.MovieData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manages db for movie information
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    /**
     * File name for db
     */
    public static final String DATABASE_NAME = "movies.db";

    /**
     * First version of the database supported by app
     */
    public static final int INITIAL_DATABASE_VERSION = 1;

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
    public List<String> recordMovieDatas(List<MovieData> movieDatas) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        List<String> idList = new ArrayList<>();
        for (MovieData movieData : movieDatas) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry._ID, movieData.getId());
            contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movieData.getTitle());
            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER, movieData.getPosterPath());
            contentValues.put(MovieContract.MovieEntry.COLUMN_SUMMARY, movieData.getSummary());
            contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, movieData.getRating());
            contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movieData.getReleaseDate());

            sqLiteDatabase.replace(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
            idList.add(movieData.getId());
        }

        return idList;
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
     * Gets a cursor to the database that has all the ids requested
     *
     * @param ids ids requested
     * @return cursor to database with all those entries
     */
    public Cursor getMovieDatasCursor(List<String> ids) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String selection = "_id IN (" + listToString(ids, DB_QUERY_DELIMITER) + ")";
        return sqLiteDatabase.query(MovieContract.MovieEntry.TABLE_NAME, null, selection, null, null, null, null);
    }

    /**
     * Gets a list of movie data for provided ids
     *
     * @param ids ID of movie
     * @return list of movie data
     */
    public List<MovieData> getMovieDatas(List<String> ids) {
        List<MovieData> movieDatas = new ArrayList<>();
        Cursor cursor = getMovieDatasCursor(ids);
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry._ID));
            String title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
            String posterUrl = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER));
            String summary = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_SUMMARY));
            double rating = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING));
            String releaseDateString = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
            MovieData movieData = new MovieData(title, posterUrl, id, summary, rating, releaseDateString);
            movieDatas.add(movieData);
        }

        cursor.close();
        return movieDatas;
    }

    /**
     * Takes a list of strings and turns it into one string to be stored in DB
     *
     * @param strings   list of strings
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
     * @param string    input string
     * @param delimiter string used to separate information in db friendly string
     * @return List of strings
     */
    private List<String> stringToList(String string, String delimiter) {
        if (TextUtils.isEmpty(string)) {
            return new ArrayList<>();
        }
        return Arrays.asList(string.split(delimiter));
    }
}
