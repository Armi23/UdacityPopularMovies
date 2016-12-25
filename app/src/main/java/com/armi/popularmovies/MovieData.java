package com.armi.popularmovies;

import android.os.Parcel;

import com.armi.popularmovies.data.Review;
import com.armi.popularmovies.data.Trailer;
import com.armi.popularmovies.network.MovieDataApiClient;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A movie item which the MovieDataAdapter holds
 */
public class MovieData {

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
        return MovieDataApiClient.getDefaultSizePosterUrl(getPosterUrl());
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
                "title='" + title + '\'' +
                ", posterUrl='" + posterUrl + '\'' +
                ", id='" + id + '\'' +
                ", summary='" + summary + '\'' +
                ", rating=" + rating +
                ", releaseDate=" + releaseDate +
                '}';
    }
}

