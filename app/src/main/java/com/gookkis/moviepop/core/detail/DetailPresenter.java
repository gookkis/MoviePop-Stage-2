package com.gookkis.moviepop.core.detail;

import android.content.Context;

import com.gookkis.moviepop.models.BaseListApi;
import com.gookkis.moviepop.models.Result;
import com.gookkis.moviepop.models.ReviewModel;
import com.gookkis.moviepop.models.VideoModel;
import com.gookkis.moviepop.network.NetworkClient;
import com.gookkis.moviepop.network.RequestAPI;
import com.gookkis.moviepop.utils.ApiConst;
import com.gookkis.moviepop.utils.DialogFactory;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

public class DetailPresenter {

    private DetailView mView;
    private Context mContext;
    private Result mMovieData;
    private BaseListApi<ReviewModel> mReviews;
    private BaseListApi<VideoModel> mVideos;
    private CompositeDisposable mCompositeDisposable;
    private RequestAPI requestAPI;
    Realm mRealm = Realm.getDefaultInstance();

    public DetailPresenter(DetailView mView, Context mContext) {
        this.mView = mView;
        this.mContext = mContext;
    }

    void loadMovie(Result movieData) {
        mMovieData = movieData;
        mView.showMovie(mMovieData);
    }

    void showReviews() {
        mView.showProgress();
        requestAPI = NetworkClient.getRetrofit().create(RequestAPI.class);
        if (mCompositeDisposable == null)
            mCompositeDisposable = new CompositeDisposable();

        mCompositeDisposable.add(
                requestAPI.movieReviews(mMovieData.getId(), ApiConst.API_KEY)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                mReviewModel -> handleDetailReviewSuccses(mReviewModel),
                                throwable -> handleDetailError(throwable)
                        )
        );
    }

    private void handleDetailReviewSuccses(BaseListApi<ReviewModel> mReviewModel) {
        mReviews = mReviewModel;
        mView.showReviews(mReviewModel.getResults());
        mView.hideProgress();
    }

    private void handleDetailError(Throwable throwable) {
        DialogFactory.createGenericErrorDialog(mContext, throwable.getLocalizedMessage().toString());
        mView.hideProgress();
    }

    void showVideos() {
        mView.showProgress();
        requestAPI = NetworkClient.getRetrofit().create(RequestAPI.class);
        if (mCompositeDisposable == null)
            mCompositeDisposable = new CompositeDisposable();

        mCompositeDisposable.add(
                requestAPI.movieVideos(mMovieData.getId(), ApiConst.API_KEY)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                mVideosModel -> handleDetailVideoSuccses(mVideosModel),
                                throwable -> handleDetailError(throwable)
                        )
        );
    }

    private void handleDetailVideoSuccses(BaseListApi<VideoModel> mVideosModel) {
        mVideos = mVideosModel;
        mView.showVideos(mVideosModel.getResults());
        mView.hideProgress();
    }

    void updateMovie(Result result, boolean isFavorite) {
        if (isFavorite)
            result.setFavorite(!result.isFavorite());

        if (!mRealm.isInTransaction()) {
            mRealm.beginTransaction();
        }

        mRealm.copyToRealmOrUpdate(result);
        mRealm.commitTransaction();

        mMovieData = result;

        mView.showMovie(mMovieData);
    }


}
