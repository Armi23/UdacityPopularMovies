package com.armi.popularmovies;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailsFragment extends Fragment {

    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Intent intent = getActivity().getIntent();
        MovieData movieData = intent.getParcelableExtra(MovieDetailsActivity.MOVIE_DATA_PARCEL_KEY);
        Log.e("armiii", "MovieDetailsFragment#onCreateView:28 received - " + movieData);
        return inflater.inflate(R.layout.fragment_movie_details, container, false);
    }

}
