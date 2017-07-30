package com.gookkis.moviepop.core.home;

import android.content.Intent;

import com.gookkis.moviepop.models.BaseListApi;
import com.gookkis.moviepop.models.Result;

import java.util.List;


public interface HomeView {
    void showLoading();

    void hideLoading();

    void movieDetailSuccess(BaseListApi<Result> movieModel);

    void movieDetailFailed(String message);

    void moveToDetails(Intent intent);

    void showSort();

    void movieFavorite(List<Result> movieModel);

}
