package com.armi.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.ParseException;
import java.util.Date;

/**
 * A movie item which the MovieDataAdapter holds
 */
public class MovieData implements Parcelable {

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
     * Creator needed to make MovieData Parcelable
     */
    public static final Creator<MovieData> CREATOR = new Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel in) {
            try {
                return new MovieData(in);
            } catch (ParseException e) {
                Log.e(getClass().toString(), "Could not parse Parsel", e);
                return null;
            }
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getTitle());
        parcel.writeString(getPosterUrl());
        parcel.writeString(getId());
        parcel.writeString(getSummary());
        parcel.writeDouble(getRating());
        parcel.writeString(MovieDateFormatter.getDateFormatter().format(getReleaseDate()));
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

