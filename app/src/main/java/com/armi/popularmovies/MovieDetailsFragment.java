package com.armi.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.armi.popularmovies.data.MovieAsyncTaskLoader;

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
     * Recycler view used for displaying view
     */
    private RecyclerView recyclerView;

    /**
     * Adapter used to display information about movie
     */
    private MovieDetailAdapter movieDetailAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.movie_details_list);
        movieDetailAdapter = new MovieDetailAdapter(getContext());
        recyclerView.setAdapter(movieDetailAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView.setAdapter(null);
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
        movieDetailAdapter.swapCurrentMovie(currentMovie);
    }

    @Override
    public void onLoaderReset(Loader<MovieData> loader) {

    }
}
