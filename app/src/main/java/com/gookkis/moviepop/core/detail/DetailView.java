package com.gookkis.moviepop.core.detail;

import com.gookkis.moviepop.models.Result;
import com.gookkis.moviepop.models.ReviewModel;
import com.gookkis.moviepop.models.VideoModel;

import java.util.List;

public interface DetailView {
    void showProgress();

    void hideProgress();

    void showMovie(Result data);

    void showReviews(List<ReviewModel> reviews);

    void showVideos(List<VideoModel> videos);

    String getYoutubeUrl(String key);

    void udpateFavDB(int updateCode);

    void onUpdateFailureDB();
}
