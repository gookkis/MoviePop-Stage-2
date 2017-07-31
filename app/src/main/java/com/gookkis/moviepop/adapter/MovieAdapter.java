package com.gookkis.moviepop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gookkis.moviepop.R;
import com.gookkis.moviepop.models.Result;
import com.gookkis.moviepop.utils.Const;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private Context context;
    private ArrayList<Result> results;
    private int rowLayout;
    private int widthPoster;

    public MovieAdapter(Context context, ArrayList<Result> results, int rowLayout, int widthPoster) {
        this.context = context;
        this.results = results;
        this.rowLayout = rowLayout;
        this.widthPoster = widthPoster;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        MovieViewHolder viewHolder = new MovieViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Result result = results.get(position);
        Picasso.with(context).load(Const.URL_POSTER + result.getPoster_path())
                .resize(widthPoster, 0)
                .placeholder(R.drawable.ic_photo_album_deep_purple_a200_48dp)
                .error(R.drawable.ic_error_outline_red_400_48dp)
                .into(holder.ivPoster);

    }


    @Override
    public int getItemCount() {
        return results.size();
    }

    public List<Result> getDatas() {
        return this.results;
    }

    public void addAll(List<Result> items) {
        add(items);
    }

    public void add(final List<Result> items) {
        final int size = items.size();
        for (int i = 0; i < size; i++) {
            this.results.add(items.get(i));
        }
        this.notifyDataSetChanged();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_poster)
        ImageView ivPoster;

        public MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void clear() {
        this.results.clear();
        this.notifyDataSetChanged();
    }

}
