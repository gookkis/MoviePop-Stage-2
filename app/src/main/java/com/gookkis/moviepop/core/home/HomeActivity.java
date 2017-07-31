package com.gookkis.moviepop.core.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gookkis.moviepop.R;
import com.gookkis.moviepop.adapter.MovieAdapter;
import com.gookkis.moviepop.db.MoviePopContract;
import com.gookkis.moviepop.db.MoviePopDbHelper;
import com.gookkis.moviepop.models.BaseListApi;
import com.gookkis.moviepop.models.Result;
import com.gookkis.moviepop.utils.ApiConst;
import com.gookkis.moviepop.utils.Const;
import com.gookkis.moviepop.utils.Helpers;
import com.gookkis.moviepop.utils.RecyclerItemClickListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements HomeView {

    private HomePresenter homePresenter;
    private ArrayList<Result> movieModel = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    private String[] mSortArray;
    private int mSortSelected = 0;
    private String mSort = Const.SORT_POPULAR;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private String TAG = "HomeActivity";
    private MovieAdapter mAdapter;
    private String RV_STATE = "RV_STATE";
    private String RV_POS = "RV_POS";
    private String SORT_BY_KEY = "SORT_BY_KEY";
    private String SORT_ID_KEY = "SORT_ID_KEY";
    private int currentItemPos = 0;
    private SharedPreferences sharedPref;

    private Parcelable listState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        initComponent();

        if ((savedInstanceState != null) && savedInstanceState.containsKey(RV_STATE)) {
            loadLastSortBy();
            currentItemPos = savedInstanceState.getInt(RV_POS);
        } else {
            loadLastSortBy();
        }

    }

    private void loadLastSortBy() {
        mSortSelected = sharedPref.getInt(SORT_ID_KEY, mSortSelected);
        mSort = sharedPref.getString(SORT_BY_KEY, mSort);
        switch (mSortSelected) {
            case 0:
                mSort = Const.SORT_POPULAR;
                homePresenter.loadMovie(mSort, ApiConst.API_KEY);
                break;
            case 1:
                mSort = Const.SORT_HIGHEST_RATED;
                homePresenter.loadMovie(mSort, ApiConst.API_KEY);
                break;
            case 2:
                new LoadFavoriteMoviesTask().execute();
                break;
        }
    }

    private void initComponent() {
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        homePresenter = new HomePresenter(this, getBaseContext());
        gridLayoutManager = new GridLayoutManager(this, Helpers.calculateNoOfColumns(this));
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addOnItemTouchListener(itemClickListener());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mSortArray = new String[]{getString(R.string.main_sort_most_popular),
                getString(R.string.main_sort_highest_rated),
                getString(R.string.main_sort_favorites)};
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by:
                showSort();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void movieDetailSuccess(BaseListApi<Result> respMovieModel) {
        if (respMovieModel.getPage() > 0) {
            this.movieModel = respMovieModel.getResults();
            mAdapter = new MovieAdapter(getApplicationContext(),
                    movieModel, R.layout.item_movie, Helpers.getWidthPoster(this));
            recyclerView.setAdapter(mAdapter);
        }
    }


    private RecyclerItemClickListener itemClickListener() {
        return new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Result result = movieModel.get(position);
                homePresenter.goToDetails(result);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        });
    }

    @Override
    public void movieDetailFailed(String message) {
        Toast.makeText(this, "Error: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void moveToDetails(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void showSort() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.main_sort_by)
                .setSingleChoiceItems(mSortArray,
                        mSortSelected,
                        (dialog, which) -> {
                            mSortSelected = which;
                            switch (mSortSelected) {
                                case 0:
                                    mSort = Const.SORT_POPULAR;
                                    homePresenter.loadMovie(mSort, ApiConst.API_KEY);
                                    break;
                                case 1:
                                    mSort = Const.SORT_HIGHEST_RATED;
                                    homePresenter.loadMovie(mSort, ApiConst.API_KEY);
                                    break;
                                case 2:
                                    new LoadFavoriteMoviesTask().execute();
                                    break;
                            }
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(SORT_BY_KEY, mSort);
                            editor.putInt(SORT_ID_KEY, mSortSelected);
                            editor.apply();

                            dialog.dismiss();
                        });
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        homePresenter.destroyData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(RV_POS, gridLayoutManager.findFirstVisibleItemPosition());
        outState.putParcelable(RV_STATE, gridLayoutManager.onSaveInstanceState());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (listState != null) {
            gridLayoutManager.onRestoreInstanceState(listState);
        }


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        listState = savedInstanceState.getParcelable(RV_STATE);
        currentItemPos = savedInstanceState.getInt(RV_POS);
        recyclerView.smoothScrollToPosition(currentItemPos);
    }

    private class LoadFavoriteMoviesTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            movieModel.clear();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showLoading();
                }
            });
            Uri favoriteMovieUri = MoviePopContract.MovieEntry.CONTENT_URI;
            Cursor favMovieCursor = getApplicationContext().getContentResolver().query(
                    favoriteMovieUri,
                    MoviePopDbHelper.FAV_MOVIE_COLUMNS,
                    null,
                    null,
                    null);

            if (favMovieCursor.moveToFirst()) {
                do {
                    Result result = new Result(favMovieCursor.getInt(1),
                            favMovieCursor.getString(2),
                            favMovieCursor.getString(3),
                            favMovieCursor.getString(4),
                            favMovieCursor.getDouble(5),
                            favMovieCursor.getString(6),
                            favMovieCursor.getString(7)
                    );
                    movieModel.add(result);
                } while (favMovieCursor.moveToNext());
            }
            favMovieCursor.close();
            return movieModel.size();
        }

        @Override
        protected void onPostExecute(Integer size) {
            super.onPostExecute(size);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideLoading();
                    mAdapter = new MovieAdapter(getApplicationContext(),
                            movieModel, R.layout.item_movie, Helpers.getWidthPoster(HomeActivity.this));
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    if (size < 1) {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.empty_favorite), Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }
}
