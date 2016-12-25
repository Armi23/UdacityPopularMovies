package com.armi.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Adapter that controls what's shown in the Movie Detail screen
 */
public class MovieDetailAdapter extends RecyclerView.Adapter {

    /**
     * Header view type number
     */
    private static final int HEADER_TYPE = 0;

    /**
     * Trailer item type number
     */
    private static final int TRAILER_ITEM_TYPE = 1;

    /**
     * Review item type number
     */
    private static final int REVIEW_ITEM_TYPE = 2;

    /**
     * Boolean tracking if trailer list should be shown
     */
    private boolean isShowingTrailers = true;

    /**
     * Current movie being shown
     */
    private MovieData currentMovie;

    /**
     * Formatter used for release date on view
     */
    private DateFormat viewDateFormatter;

    /**
     * Context used for operatons
     */
    private Context context;

    /**
     * Constructor
     *
     * @param context context
     */
    public MovieDetailAdapter(Context context) {
        this.context = context;
        viewDateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e("armiii", "MovieDetailAdapter#onCreateViewHolder:70 creating views");
        if (viewType == HEADER_TYPE) {
            View headerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_detail_header, parent, false);
            return new HeaderViewHolder(headerView);
        }
        return new TrailerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_detail_trailer, parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.e("armiii", "MovieDetailAdapter#onBindViewHolder:80 binding views");
        if (getItemViewType(position) == HEADER_TYPE) {
            if (currentMovie == null) {
                return;
            }
            Log.e("armiii", "MovieDetailAdapter#onBindViewHolder:82 binding head");
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            Picasso.with(context).load(currentMovie.getDefaultSizePosterUrl()).into(headerViewHolder.artImageView);
            headerViewHolder.titleTextView.setText(currentMovie.getTitle());
            headerViewHolder.releaseDateTextView.setText(String.format(context.getString(R.string.release_date), viewDateFormatter.format(currentMovie.getReleaseDate())));
            headerViewHolder.voteAverageTextView.setText(String.format(context.getString(R.string.rating), String.valueOf(currentMovie.getRating())));
            headerViewHolder.summaryTextView.setText(currentMovie.getSummary());
        } else if (isShowingTrailers) {
            TrailerViewHolder trailerViewHolder = (TrailerViewHolder) holder;
            trailerViewHolder.titleTextView.setText(currentMovie.getTrailerUrls().get(position - 1).getName());
        } else {
            TrailerViewHolder trailerViewHolder = (TrailerViewHolder) holder;
            trailerViewHolder.titleTextView.setText(currentMovie.getUserReview().get(position - 1).getContent());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER_TYPE;
        }
        if (isShowingTrailers) {
            return TRAILER_ITEM_TYPE;
        } else {
            return REVIEW_ITEM_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        if (currentMovie == null) {
            return 0;
        }
        if (isShowingTrailers) {
            return currentMovie.getTrailerUrls().size() + 1;
        } else {
            return currentMovie.getUserReview().size() + 1;
        }
    }

    /**
     * Changes the movie data for the current movie
     *
     * @param movieData movie data
     */
    public void swapCurrentMovie(MovieData movieData) {
        currentMovie = movieData;
        notifyDataSetChanged();
    }

    /**
     * Switches list type between trailer and reviews or vice-versa
     *
     * @param showTrailers true if trailers should be shown.
     */
    private void swapListType(boolean showTrailers) {
        isShowingTrailers = showTrailers;
        notifyItemRangeChanged(1, 5);
        notifyDataSetChanged();
    }

    /**
     * Makes a view holder for the header
     */
    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        /**
         * Image view for the movie's art
         */
        public ImageView artImageView;

        /**
         * Text view for movie's title
         */
        public TextView titleTextView;

        /**
         * Text view for movie's release date
         */
        public TextView releaseDateTextView;

        /**
         * Text view for movie's voter average
         */
        public TextView voteAverageTextView;

        /**
         * Text view for movie's summary
         */
        public TextView summaryTextView;

        /**
         * Constructor
         *
         * @param rootView root view to be populated
         */
        public HeaderViewHolder(View rootView) {
            super(rootView);
            artImageView = (ImageView) rootView.findViewById(R.id.movie_art);
            titleTextView = (TextView) rootView.findViewById(R.id.movie_title);
            releaseDateTextView = (TextView) rootView.findViewById(R.id.movie_release_date);
            voteAverageTextView = (TextView) rootView.findViewById(R.id.movie_vote_average);
            summaryTextView = (TextView) rootView.findViewById(R.id.movie_summary);
        }
    }

    /**
     * Holds views for trailer objects
     */
    private class TrailerViewHolder extends RecyclerView.ViewHolder{

        /**
         * Text view for title
         */
        TextView titleTextView;

        /**
         * Constructor
         *
         * @param itemView view of item
         */
        public TrailerViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
