package com.armi.popularmovies;

import com.armi.popularmovies.data.Review;
import com.armi.popularmovies.data.Trailer;
import com.armi.popularmovies.network.MovieDataApiClient;

import java.util.ArrayList;
import java.util.List;

/**
 * A movie item which the MovieDataAdapter holds
 */
public class MovieData {

    /**
     * Name of movie
     */
    private final String original_title;

    /**
     * Poster URL of movie
     */
    private final String poster_path;

    /**
     * ID of movie
     */
    private final String id;

    /**
     * Summary of movie
     */
    private final String overview;

    /**
     * User vote_average of movie
     */
    private final double vote_average;

    /**
     * Release date of movie
     */
    private final String release_date;

    /**
     * List of Trailers
     */
    private List<Trailer> trailerUrls = new ArrayList<>();

    /**
     * List of Reviews
     */
    private List<Review> userReview = new ArrayList<>();

    /**
     * Constructor
     *
     * @param title       name of movie
     * @param posterPath   URL of movie image
     * @param id          ID of movie
     * @param summary     overview of movie
     * @param rating      user vote_average of movie
     * @param releaseDate release date of movie
     */
    public MovieData(String title, String posterPath, String id, String summary,
                     double rating, String releaseDate) {
        this.original_title = title;
        this.poster_path = posterPath;
        this.id = id;
        this.overview = summary;
        this.vote_average = rating;
        this.release_date = releaseDate;
    }

    /**
     * Getter for movie name
     *
     * @return movie name
     */
    public String getTitle() {
        return original_title;
    }

    /**
     * Getter for movie image URl
     *
     * @return movie image URL
     */
    public String getPosterPath() {
        return poster_path;
    }

    /**
     * Returns the default image URL for the movie poster
     *
     * @return default movie poster URL
     */
    public String getDefaultSizePosterUrl() {
        return MovieDataApiClient.getDefaultSizePosterUrl(getPosterPath());
    }

    /**
     * Getter for ID of movie
     *
     * @return ID of movie
     */
    public String getId() {
        return id;
    }

    /**
     * Summary of movie
     *
     * @return overview of movie
     */
    public String getSummary() {
        return overview;
    }

    /**
     * User vote_average of movie
     *
     * @return user vote_average of movie
     */
    public double getRating() {
        return vote_average;
    }

    /**
     * Release date of movie
     *
     * @return release date of movie
     */
    public String getReleaseDate() {
        return release_date;
    }

    public List<Trailer> getTrailerUrls() {
        return trailerUrls;
    }

    public void setTrailerUrls(List<Trailer> trailerUrls) {
        this.trailerUrls = trailerUrls;
    }

    public List<Review> getUserReview() {
        return userReview;
    }

    public void setUserReview(List<Review> userReview) {
        this.userReview = userReview;
    }

    @Override
    public String toString() {
        return "MovieData{" +
                "original_title='" + original_title + '\'' +
                ", poster_path='" + poster_path + '\'' +
                ", id='" + id + '\'' +
                ", overview='" + overview + '\'' +
                ", vote_average=" + vote_average +
                ", release_date=" + release_date +
                '}';
    }
}

