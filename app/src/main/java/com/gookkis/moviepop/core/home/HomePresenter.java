package com.gookkis.moviepop.core.home;

import android.content.Context;
import android.content.Intent;

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

public class HomePresenter {
    private HomeView view;
    private CompositeDisposable mCompositeDisposable;
    private Context context;

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
        intent.putExtra(Const.RESULT, result);
        view.moveToDetails(intent);
    }

    public String getValueSort(int i) {
        return context.getResources().getStringArray(R.array.value_sort_by)[i];
    }

}
