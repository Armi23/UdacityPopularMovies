package com.armi.popularmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
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
    private boolean isShowingTrailers = false;

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
        viewDateFormatter = new SimpleDateFormat("yyyy", Locale.getDefault());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER_TYPE) {
            View headerView = LayoutInflater.from(context).inflate(R.layout.movie_detail_header, parent, false);
            return new HeaderViewHolder(headerView);
        } else if (isShowingTrailers) {
            return new TrailerViewHolder(LayoutInflater.from(context).inflate(R.layout.movie_detail_trailer, parent, false));
        } else {
            return new ReviewViewHolder(LayoutInflater.from(context).inflate(R.layout.movie_detail_review, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == HEADER_TYPE) {
            if (currentMovie == null) {
                return;
            }
            final HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.titleTextView.setText(currentMovie.getTitle());
            Picasso.with(context).load(currentMovie.getDefaultSizePosterUrl()).into(headerViewHolder.artImageView, new Callback.EmptyCallback() {
                @Override
                public void onSuccess() {
                    super.onSuccess();
                    Bitmap bitmap = ((BitmapDrawable) headerViewHolder.artImageView.getDrawable()).getBitmap();
                    Palette palette = Palette.from(bitmap).generate();
                    Palette.Swatch vibrant = palette.getDarkVibrantSwatch();
                    int backgroundColor;
                    int titleTextColor;
                    int bodyTextColor;

                    if (vibrant != null) {
                        backgroundColor = vibrant.getRgb();
                        titleTextColor = vibrant.getTitleTextColor();
                        bodyTextColor = vibrant.getBodyTextColor();
                    } else {
                        List<Palette.Swatch> swatches = palette.getSwatches();

                        if (swatches.size() == 0) {
                            Log.e(getClass().toString(), "Could not get swatch");
                            backgroundColor = Color.BLACK;
                            titleTextColor = Color.WHITE;
                            bodyTextColor = Color.WHITE;
                        } else {
                            Palette.Swatch swatch = swatches.get(0);
                            backgroundColor = swatch.getRgb();
                            titleTextColor = swatch.getTitleTextColor();
                            bodyTextColor = swatch.getBodyTextColor();
                        }
                    }

                    headerViewHolder.rootView.setBackgroundColor(backgroundColor);
                    headerViewHolder.titleTextView.setTextColor(titleTextColor);
                    headerViewHolder.releaseDateTextView.setTextColor(bodyTextColor);
                    headerViewHolder.voteAverageTextView.setTextColor(bodyTextColor);
                    headerViewHolder.summaryTextView.setTextColor(bodyTextColor);
                    headerViewHolder.trailersTextView.setTextColor(bodyTextColor);
                    headerViewHolder.trailersTextView.getBackground().setAlpha(128);
                    headerViewHolder.reviewsTextView.setTextColor(bodyTextColor);
                    headerViewHolder.reviewsTextView.getBackground().setAlpha(128);

                }
            });
            headerViewHolder.releaseDateTextView.setText(String.format(context.getString(R.string.release_date), viewDateFormatter.format(currentMovie.getReleaseDate())));
            headerViewHolder.voteAverageTextView.setText(String.format(context.getString(R.string.rating), String.valueOf(currentMovie.getRating())));
            headerViewHolder.summaryTextView.setText(currentMovie.getSummary());
            headerViewHolder.trailersTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    swapListType(true);
                }
            });

            headerViewHolder.reviewsTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    swapListType(false);
                }
            });
        } else if (isShowingTrailers) {
            TrailerViewHolder trailerViewHolder = (TrailerViewHolder) holder;
            trailerViewHolder.titleTextView.setText(currentMovie.getTrailerUrls().get(position - 1).getName());
        } else {
            ReviewViewHolder reviewViewHolder = (ReviewViewHolder) holder;
            reviewViewHolder.titleTextView.setText(currentMovie.getUserReview().get(position - 1).getContent());
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
        notifyItemRangeChanged(1, getItemCount());
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
         * Text view for trailer option of list
         */
        public TextView trailersTextView;

        /**
         * Text view for reviews option of list
         */
        public TextView reviewsTextView;

        public View rootView;

        /**
         * Constructor
         *
         * @param rootView root view to be populated
         */
        public HeaderViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            artImageView = (ImageView) rootView.findViewById(R.id.movie_art);
            titleTextView = (TextView) rootView.findViewById(R.id.movie_title);
            releaseDateTextView = (TextView) rootView.findViewById(R.id.movie_release_date);
            voteAverageTextView = (TextView) rootView.findViewById(R.id.movie_vote_average);
            summaryTextView = (TextView) rootView.findViewById(R.id.movie_summary);
            trailersTextView = (TextView) rootView.findViewById(R.id.trailer_button);
            reviewsTextView = (TextView) rootView.findViewById(R.id.review_button);
        }
    }

    /**
     * Holds views for trailer objects
     */
    private class TrailerViewHolder extends RecyclerView.ViewHolder {

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

    /**
     * Holds view for Review objects
     */
    private class ReviewViewHolder extends RecyclerView.ViewHolder {

        /**
         * Text view for title
         */
        TextView titleTextView;

        /**
         * Constructor
         *
         * @param itemView view of item
         */
        public ReviewViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.title);
        }

    }

    private final class PalleteTransformation implements Transformation {

        @Override
        public Bitmap transform(Bitmap source) {

            return source;
        }

        @Override
        public String key() {
            return "";
        }
    }
}
