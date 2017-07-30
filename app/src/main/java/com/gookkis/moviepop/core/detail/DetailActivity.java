package com.gookkis.moviepop.core.detail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gookkis.moviepop.R;
import com.gookkis.moviepop.models.Result;
import com.gookkis.moviepop.models.ReviewModel;
import com.gookkis.moviepop.models.VideoModel;
import com.gookkis.moviepop.utils.Const;
import com.gookkis.moviepop.utils.DialogFactory;
import com.gookkis.moviepop.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailActivity extends AppCompatActivity implements DetailView {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_poster)
    ImageView ivPoster;
    @BindView(R.id.tv_released_date)
    TextView tvReleasedDate;
    @BindView(R.id.tv_rating)
    TextView tvRating;
    @BindView(R.id.tv_synopsis)
    TextView tvSynopsis;
    @BindView(R.id.tv_favorite)
    TextView tvFavorite;
    @BindView(R.id.ly_videos)
    LinearLayout lyVideos;
    @BindView(R.id.ly_reviews)
    LinearLayout lyReviews;

    private DetailPresenter detailPresenter;
    private ProgressBar mProgressBar = null;
    private String TAG = "DetailAct";
    private Result result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        initComponent();

    }

    private void initComponent() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        detailPresenter = new DetailPresenter(this, getApplicationContext());
        if (getIntent() != null) {
            result = getIntent().getParcelableExtra(Const.RESULT);
            detailPresenter.loadMovie(result);

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showProgress() {
        if (mProgressBar == null) {
            mProgressBar = DialogFactory.DProgressBar(DetailActivity.this);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }


    @Override
    public void showMovie(Result data) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate;
        try {
            Date dateRelease = sdf.parse(data.getRelease_date());
            formattedDate = DateFormat.format("dd MMM yyyy", dateRelease).toString();
        } catch (ParseException e) {
            e.printStackTrace();
            formattedDate = data.getRelease_date();
        }
        tvTitle.setText(data.getOriginal_title());
        tvRating.setText(String.valueOf(data.getVote_average()) + "/10");
        tvReleasedDate.setText(formattedDate);
        tvSynopsis.setText(data.getOverview());

        if (data.isFavorite()) {
            tvFavorite.setText(getString(R.string.remove_from_favorite));
        } else {
            tvFavorite.setText(getString(R.string.add_to_favorite));
        }

        Picasso.with(getApplicationContext()).load(Const.URL_POSTER + data.getPoster_path())
                .resize(Helpers.getWidthPoster(this) * 2, 0).into(ivPoster);

        detailPresenter.showVideos();
        // detailPresenter.showReviews();
    }

    @Override
    public void showVideos(List<VideoModel> videos) {
        lyVideos.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(DetailActivity.this);
        for (int i = 0; i < videos.size(); i++) {
            VideoModel video = videos.get(i);
            String urlPic = Const.ROOT_VIDEO_THUMBNAIL + video.getKey() + "/0.jpg";
            View view = inflater.inflate(R.layout.item_video, lyVideos, false);
            ImageView ivThumb = (ImageView) view.findViewById(R.id.iv_video_thumbnail);
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_video_title);

            tvTitle.setText(video.getName());

            Picasso.with(getBaseContext()).load(urlPic)
                    .placeholder(R.drawable.ic_photo_album_deep_purple_a200_48dp)
                    .error(R.drawable.ic_error_outline_red_400_48dp)
                    .into(ivThumb);

            view.setOnClickListener(v1 -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getYoutubeUrl(video.getKey())));
                startActivity(intent);
            });
            lyVideos.addView(view);
        }
        detailPresenter.showReviews();

    }

    @Override
    public void showReviews(List<ReviewModel> reviews) {
        lyReviews.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(DetailActivity.this);
        for (int i = 0; i < reviews.size(); i++) {
            ReviewModel review = reviews.get(i);

            View view = inflater.inflate(R.layout.item_review, lyReviews, false);
            TextView tvContent = (TextView) view.findViewById(R.id.tv_review_content);
            TextView tvAuthor = (TextView) view.findViewById(R.id.tv_review_author);

            tvContent.setText("\"" + review.getContent() + "\"");
            tvAuthor.setText(review.getAuthor());

            lyReviews.addView(view);
        }
    }

    @Override
    public String getYoutubeUrl(String key) {
        return Const.ROOT_VIDEO_KEY + key;
    }

    @OnClick(R.id.tv_favorite)
    void onFavoriteClicked() {
        if (result.isFavorite()) {
            tvFavorite.setText(getString(R.string.remove_from_favorite));
        } else {
            tvFavorite.setText(getString(R.string.add_to_favorite));
        }
        detailPresenter.updateMovie(result, true);
    }


}
