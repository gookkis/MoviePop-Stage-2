package com.gookkis.moviepop.db;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.gookkis.moviepop.core.detail.DetailActivity;
import com.gookkis.moviepop.db.MoviePopContract.MovieEntry;
import com.gookkis.moviepop.models.Result;


public class UpdateFavMovieTask extends AsyncTask<Void, Void, Void> {

    private Context mContext;
    private Result mResult;
    private DetailActivity detailView;

    public static final int ADDED_TO_FAVORITE = 1;
    public static final int REMOVED_FROM_FAVORITE = 2;

    public UpdateFavMovieTask(Context context, Result result, DetailActivity detailActivity) {
        detailView = detailActivity;
        mContext = context;
        mResult = result;
    }

    @Override
    protected Void doInBackground(Void... params) {
        updateFavoriteMovie();
        return null;
    }

    private void updateFavoriteMovie() {
        Cursor favMovieCursor = mContext.getContentResolver().query(
                MoviePopContract.MovieEntry.CONTENT_URI,
                new String[]{MoviePopContract.MovieEntry.COLUMN_MOVIE_ID},
                MoviePopContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{String.valueOf(mResult.getId())},
                null);

        if (favMovieCursor.moveToFirst()) {
            int rowDeleted = mContext.getContentResolver().delete(MoviePopContract.MovieEntry.CONTENT_URI,
                    MoviePopContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[]{String.valueOf(mResult.getId())});

            if (rowDeleted > 0) {
                detailView.udpateFavDB(REMOVED_FROM_FAVORITE);
            } else {
                detailView.onUpdateFailureDB();
            }

        } else {
            ContentValues values = new ContentValues();

            values.put(MovieEntry.COLUMN_MOVIE_ID, mResult.getId());
            values.put(MovieEntry.COLUMN_TITLE, mResult.getTitle());
            values.put(MovieEntry.COLUMN_POSTER_PATH, mResult.getPoster_path());
            values.put(MovieEntry.COLUMN_OVERVIEW, mResult.getOverview());
            values.put(MovieEntry.COLUMN_VOTE_AVERAGE, mResult.getVote_average());
            values.put(MovieEntry.COLUMN_RELEASE_DATE, mResult.getRelease_date());
            values.put(MovieEntry.COLUMN_BACKDROP_PATH, mResult.getBackdrop_path());

            Uri uriInsert = mContext.getContentResolver().insert(
                    MoviePopContract.MovieEntry.CONTENT_URI,
                    values);

            long resultRowId = ContentUris.parseId(uriInsert);

            if (resultRowId > 0) {
                detailView.udpateFavDB(ADDED_TO_FAVORITE);
            } else {
                detailView.onUpdateFailureDB();
            }
        }
        favMovieCursor.close();
    }
}