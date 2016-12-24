package com.armi.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.armi.popularmovies.data.MovieAsyncTaskLoader;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<MovieData> {

    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * ID used for movie loader
     */
    private static final int LOADER_ID = 0;

    /**
     * Image view for the movie's art
     */
    private ImageView artImageView;

    /**
     * Text view for movie's title
     */
    private TextView titleTextView;

    /**
     * Text view for movie's release date
     */
    private TextView releaseDateTextView;

    /**
     * Text view for movie's voter average
     */
    private TextView voteAverageTextView;

    /**
     * Text view for movie's summary
     */
    private TextView summaryTextView;

    /**
     * Formatter used for release date on view
     */
    private DateFormat viewDateFormatter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewDateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        artImageView = (ImageView) rootView.findViewById(R.id.movie_art);
        titleTextView = (TextView) rootView.findViewById(R.id.movie_title);
        releaseDateTextView = (TextView) rootView.findViewById(R.id.movie_release_date);
        voteAverageTextView = (TextView) rootView.findViewById(R.id.movie_vote_average);
        summaryTextView = (TextView) rootView.findViewById(R.id.movie_summary);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<MovieData> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        String movieId = intent.getStringExtra(MovieDetailsActivity.MOVIE_ID_KEY);
        return new MovieAsyncTaskLoader(getContext(), movieId);
    }

    @Override
    public void onLoadFinished(Loader<MovieData> loader, MovieData currentMovie) {
        Picasso.with(getContext()).load(currentMovie.getDefaultSizePosterUrl()).into(artImageView);
        titleTextView.setText(currentMovie.getTitle());
        releaseDateTextView.setText(String.format(getString(R.string.release_date), viewDateFormatter.format(currentMovie.getReleaseDate())));
        voteAverageTextView.setText(String.format(getString(R.string.rating), String.valueOf(currentMovie.getRating())));
        summaryTextView.setText(currentMovie.getSummary());

    }

    @Override
    public void onLoaderReset(Loader<MovieData> loader) {

    }
}
