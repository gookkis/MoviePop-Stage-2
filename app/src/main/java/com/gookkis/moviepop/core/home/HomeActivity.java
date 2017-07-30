package com.gookkis.moviepop.core.home;

import android.content.Intent;
import android.os.Bundle;
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
import com.gookkis.moviepop.models.BaseListApi;
import com.gookkis.moviepop.models.Result;
import com.gookkis.moviepop.utils.ApiConst;
import com.gookkis.moviepop.utils.Const;
import com.gookkis.moviepop.utils.Helpers;
import com.gookkis.moviepop.utils.RecyclerItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements HomeView {

    private HomePresenter homePresenter;
    private BaseListApi<Result> movieModel;
    GridLayoutManager gridLayoutManager;
    private String[] mSortArray;
    private int mSortSelected = 0;
    private String mSort = Const.SORT_POPULAR;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private String TAG = "HomeActivity";
    private MovieAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        initComponent();

    }

    private void initComponent() {
        homePresenter = new HomePresenter(this, getBaseContext());
        gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addOnItemTouchListener(itemClickListener());
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mSortArray = new String[]{getString(R.string.main_sort_most_popular),
                getString(R.string.main_sort_highest_rated),
                getString(R.string.main_sort_favorites)};

        homePresenter.loadMovie(mSort, ApiConst.API_KEY);
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
            this.movieModel = respMovieModel;
            mAdapter = new MovieAdapter(getApplicationContext(),
                    movieModel.getResults(), R.layout.item_movie, Helpers.getWidthPoster(this));
            recyclerView.setAdapter(mAdapter);
        }
    }

    private RecyclerItemClickListener itemClickListener() {
        return new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Result result = movieModel.getResults().get(position);
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
                                    //Toast.makeText(getApplicationContext(), "You don't have favorites movies!", Toast.LENGTH_SHORT).show();
                                    homePresenter.loadFavorites();
                                    break;

                                /*default:
                                    mSort = Const.SORT_POPULAR;
                                    homePresenter.loadMovie(mSort, ApiConst.API_KEY);
                                    break;*/
                            }
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
    public void movieFavorite(List<Result> results) {
        if (!mAdapter.getDatas().isEmpty()) {
            mAdapter.clear();
        }
        mAdapter.addAll(results);
    }
}
