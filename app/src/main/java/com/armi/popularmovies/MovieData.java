package com.armi.popularmovies;

/**
 * A movie item which the MovieDataAdapter holds
 */
public class MovieData {

    /**
     * Name of movie
     */
    private final String movieName;

    /**
     * URL of movie
     */
    private final String movieImageUrl;

    /**
     * Constructor
     *
     * @param movieName     name of movie
     * @param movieImageUrl URL of movie image
     */
    public MovieData(String movieName, String movieImageUrl) {
        this.movieName = movieName;
        this.movieImageUrl = movieImageUrl;
    }

    /**
     * Getter for movie name
     *
     * @return movie name
     */
    public String getMovieName() {
        return movieName;
    }

    /**
     * Getter for movie image URl
     *
     * @return movie image URL
     */
    public String getMovieImageUrl() {
        return movieImageUrl;
    }
}

