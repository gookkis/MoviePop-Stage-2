package com.gookkis.moviepop.core.home;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.gookkis.moviepop.R;
import com.gookkis.moviepop.core.detail.DetailActivity;
import com.gookkis.moviepop.models.BaseListApi;
import com.gookkis.moviepop.models.Result;
import com.gookkis.moviepop.network.NetworkClient;
import com.gookkis.moviepop.network.RequestAPI;
import com.gookkis.moviepop.utils.Const;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

public class HomePresenter {
    private HomeView view;
    private CompositeDisposable mCompositeDisposable;
    private Context context;
    Realm mRealm = Realm.getDefaultInstance();

    public HomePresenter(HomeView view, Context context) {
        this.view = view;
        this.context = context;
    }

    public void loadMovie(String sort, String apikey) {
        view.showLoading();
        RequestAPI requestAPI = NetworkClient.getRetrofit().create(RequestAPI.class);
        if (mCompositeDisposable == null)
            mCompositeDisposable = new CompositeDisposable();

        mCompositeDisposable.add(
                requestAPI.getMovieList(sort, apikey)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                movieModel -> handleHomeSuccses(movieModel),
                                throwable -> handleHomeError(throwable)
                        )
        );
    }

    private void handleHomeSuccses(BaseListApi<Result> movieModel) {
        view.movieDetailSuccess(movieModel);
        view.hideLoading();
    }

    private void handleHomeError(Throwable throwable) {
        view.movieDetailFailed(throwable.getLocalizedMessage().toString());
        view.hideLoading();
    }

    public void destroyData() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
    }

    public void goToDetails(Result result) {
        Intent intent = new Intent(context, DetailActivity.class);
       /* intent.putExtra(Const.id, result.getId());
        intent.putExtra(Const.original_title, result.getOriginal_title());
        intent.putExtra(Const.poster_path, result.getPoster_path());
        intent.putExtra(Const.release_date, result.getRelease_date());
        intent.putExtra(Const.overview, result.getOverview());
        intent.putExtra(Const.vote_average, String.valueOf(result.getVote_average()));*/
        intent.putExtra(Const.RESULT, result);
        view.moveToDetails(intent);
    }

    public String getValueSort(int i) {
        return context.getResources().getStringArray(R.array.value_sort_by)[i];
    }

    void loadFavorites() {
        view.showLoading();

        if (!mRealm.isInTransaction()) {
            mRealm.beginTransaction();
        }

        final RealmResults<Result> results = mRealm.where(Result.class).equalTo("favorite", true).findAll();
        results.size();

        if (results.isEmpty()) {
            Toast.makeText(context, "Empty Favorites", Toast.LENGTH_SHORT).show();
        } else {
            view.movieFavorite(results);
        }

        view.hideLoading();
    }

}
