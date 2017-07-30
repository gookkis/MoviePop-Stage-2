package com.gookkis.moviepop.network;

import com.gookkis.moviepop.models.BaseListApi;
import com.gookkis.moviepop.models.Result;
import com.gookkis.moviepop.models.ReviewModel;
import com.gookkis.moviepop.models.VideoModel;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface RequestAPI {
    @GET("/3/movie/{sort}")
    Observable<BaseListApi<Result>> getMovieList(
            @Path("sort") String sort_type, @Query("api_key") String api_key);

    @GET("/3/movie/{movie_id}/reviews")
    Observable<BaseListApi<ReviewModel>> movieReviews(
            @Path("movie_id") long movieId, @Query("api_key") String apiKey);

    @GET("/3/movie/{movie_id}/videos")
    Observable<BaseListApi<VideoModel>> movieVideos(
            @Path("movie_id") long movieId, @Query("api_key") String apiKey);
}
