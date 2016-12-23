package com.armi.popularmovies.data;

import android.provider.BaseColumns;

/**
 * Contract to interact with movie database
 */
public class MovieContract {

//    /**
//     * Content authority for content provider
//     */
//    public static final String CONTENT_AUTHORITY = "com.armi.popularmovies.data";
//
//    /**
//     * Base URI for Movie content provider interactions
//     */
//    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
//
//    /**
//     * Path to movie content resolver
//     */
//    public static final String PATH_MOVIE = "movie";

    /**
     * Defines information contained in movie content storage
     */
    public static final class MovieEntry implements BaseColumns {

//        /**
//         * Content URI used to modify movie entries
//         */
//        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        /**
         * Table that stories movie information
         */
        public static final String TABLE_NAME = "movies";

        /**
         * Name of movie
         */
        public static final String COLUMN_TITLE = "title";

        /**
         * Poster URL of movie
         */
        public static final String COLUMN_POSTER = "posterUrl";

        /**
         * Summary of movie
         */
        public static final String COLUMN_SUMMARY = "summary";

        /**
         * User rating of movie
         */
        public static final String COLUMN_RATING = "rating";

        /**
         * Release date of movie
         */
        public static final String COLUMN_RELEASE_DATE = "releaseDate";

        /**
         * Trails of movie
         */
        public static final String COLUMN_TRAILER_URLS = "trailerUrls";

        /**
         * Reviews of movie
         */
        public static final String COLUMN_REVIEWS = "reviews";

        /**
         * Command used to create table
         */
        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                MovieEntry._ID + " TEXT PRIMARY KEY," +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_POSTER + " TEXT NOT NULL," +
                MovieEntry.COLUMN_SUMMARY + " TEXT NOT NULL," +
                MovieEntry.COLUMN_RATING + " REAL NOT NULL," +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_TRAILER_URLS + " TEXT NOT NULL," +
                MovieEntry.COLUMN_REVIEWS + " TEXT NOT NULL" +
                " );";
    }
}
