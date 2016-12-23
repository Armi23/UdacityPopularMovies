package com.armi.popularmovies;

import android.os.Parcel;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A movie item which the MovieDataAdapter holds
 */
public class MovieData {

    /**
     * TMDB API base URL
     */
    public static final String BASE_MOVIE_API_URL = "http://image.tmdb.org/t/p/";

    /**
     * Default recommended phone size
     */
    public static final String DEFAULT_IMAGE_SIZE = "w185";

    /**
     * Name of movie
     */
    private final String title;

    /**
     * Poster URL of movie
     */
    private final String posterUrl;

    /**
     * ID of movie
     */
    private final String id;

    /**
     * Summary of movie
     */
    private final String summary;

    /**
     * User rating of movie
     */
    private final double rating;

    /**
     * Release date of movie
     */
    private final Date releaseDate;

    /**
     * Urls of trailer of movie
     */
    private List<String> trailerUrls = new ArrayList<>();

    /**
     * User reviews of movie
     */
    private List<String> userReview = new ArrayList<>();

    /**
     * Constructor
     *
     * @param title       name of movie
     * @param posterUrl   URL of movie image
     * @param id          ID of movie
     * @param summary     summary of movie
     * @param rating      user rating of movie
     * @param releaseDate release date of movie
     */
    public MovieData(String title, String posterUrl, String id, String summary,
                     double rating, Date releaseDate) {
        this.title = title;
        this.posterUrl = posterUrl;
        this.id = id;
        this.summary = summary;
        this.rating = rating;
        this.releaseDate = releaseDate;
    }

    /**
     * Constructor made from Parcel
     *
     * @param in input Parcel
     * @throws ParseException thrown when date cannot be formatted
     */
    protected MovieData(Parcel in) throws ParseException {
        title = in.readString();
        posterUrl = in.readString();
        id = in.readString();
        summary = in.readString();
        rating = in.readDouble();
        releaseDate = MovieDateFormatter.getDateFormatter().parse(in.readString());
    }

    /**
     * Getter for movie name
     *
     * @return movie name
     */
    public String getTitle() {
        return title;
    }

    /**
     * Getter for movie image URl
     *
     * @return movie image URL
     */
    public String getPosterUrl() {
        return posterUrl;
    }

    /**
     * Returns the default image URL for the movie poster
     *
     * @return default movie poster URL
     */
    public String getDefaultSizePosterUrl() {
        return BASE_MOVIE_API_URL + DEFAULT_IMAGE_SIZE + getPosterUrl();
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
     * @return summary of movie
     */
    public String getSummary() {
        return summary;
    }

    /**
     * User rating of movie
     *
     * @return user rating of movie
     */
    public double getRating() {
        return rating;
    }

    /**
     * Release date of movie
     *
     * @return release date of movie
     */
    public Date getReleaseDate() {
        return releaseDate;
    }

    /**
     * Getter for trailer URLs
     *
     * @retur list of trailer URLs
     */
    public List<String> getTrailerUrls() {
        return trailerUrls;
    }

    /**
     * Setter for list of trailer URLs
     *
     * @param trailerUrls list of trailer URLs
     */
    public void setTrailerUrls(List<String> trailerUrls) {
        this.trailerUrls = trailerUrls;
    }

    /**
     * Getter for list of user reviews
     *
     * @return list of user reviews
     */
    public List<String> getUserReview() {
        return userReview;
    }

    /**
     * Setter for list of user reviews
     *
     * @param userReview list of user reviews
     */
    public void setUserReview(List<String> userReview) {
        this.userReview = userReview;
    }

    @Override
    public String toString() {
        return "MovieData{" +
                "title='" + title + '\'' +
                ", posterUrl='" + posterUrl + '\'' +
                ", id='" + id + '\'' +
                ", summary='" + summary + '\'' +
                ", rating=" + rating +
                ", releaseDate=" + releaseDate +
                '}';
    }
}

