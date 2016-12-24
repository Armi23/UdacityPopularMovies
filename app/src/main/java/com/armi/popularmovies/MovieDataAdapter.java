package com.armi.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.armi.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Adapter that handles the interactions between displaying movies and updating movies we have
 */
public class MovieDataAdapter extends CursorAdapter {

    /**
     * Constructor
     */
    public MovieDataAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.grid_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            viewHolder.movieArtImageView = (ImageView) view.findViewById(R.id.movie_art);
            view.setTag(viewHolder);
        }
        String url = MovieData.BASE_MOVIE_API_URL + MovieData.DEFAULT_IMAGE_SIZE + cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER));
        viewHolder.movieId = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry._ID));
        Picasso.with(context).load(url).into(viewHolder.movieArtImageView);
    }

    /**
     * ViewHolder for movie data grid adapter views
     */
    static class ViewHolder {

        /**
         * Movie cover art image holder
         */
        ImageView movieArtImageView;

        /**
         * Holds ID of movie being displayed
         */
        String movieId;
    }
}
