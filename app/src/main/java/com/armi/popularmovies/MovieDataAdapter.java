package com.armi.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter that handles the interactions between displaying movies and updating movies we have
 */
public class MovieDataAdapter extends BaseAdapter {

    /**
     * TMDB API base URL
     */
    public static final String BASE_MOVIE_API_URL = "http://image.tmdb.org/t/p/";

    /**
     * Default recommended phone size
     */
    public static final String DEFAULT_IMAGE_SIZE = "w185";

    /**
     * Data of movies that can be displayed
     */
    private List<MovieData> movieDataList = new ArrayList<>();

    @Override
    public int getCount() {
        return movieDataList.size();
    }

    @Override
    public Object getItem(int i) {
        return movieDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Context context = viewGroup.getContext();
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.grid_item, viewGroup, false);
        }
        ImageView movieArtImageView = (ImageView) view.findViewById(R.id.movie_art);
        Picasso.with(context).load(BASE_MOVIE_API_URL + DEFAULT_IMAGE_SIZE + movieDataList.get(i).getMovieImageUrl()).into(movieArtImageView);
        return view;
    }

    /**
     * Updates movies shown to user
     *
     * @param movieDataList movies shown to user
     */
    public void setMovieDataList(List<MovieData> movieDataList) {
        this.movieDataList = movieDataList;
    }
}
