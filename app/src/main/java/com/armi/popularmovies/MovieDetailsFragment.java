package com.armi.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailsFragment extends Fragment {

    public MovieDetailsFragment() {
        // Required empty public constructor
    }

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
     * Current movie in view
     */
    private MovieData currentMovie;

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

        Intent intent = getActivity().getIntent();
        currentMovie = intent.getParcelableExtra(MovieDetailsActivity.MOVIE_DATA_PARCEL_KEY);
        Picasso.with(getContext()).load(currentMovie.getDefaultSizePosterUrl()).into(artImageView);
        titleTextView.setText(currentMovie.getTitle());
        releaseDateTextView.setText(String.format(getString(R.string.release_date), viewDateFormatter.format(currentMovie.getReleaseDate())));
        voteAverageTextView.setText(String.format(getString(R.string.rating), String.valueOf(currentMovie.getRating())));
        summaryTextView.setText(currentMovie.getSummary());
        return rootView;
    }

}
