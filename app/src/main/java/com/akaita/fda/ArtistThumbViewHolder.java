package com.akaita.fda;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by mikel on 20/05/2015.
 */
public class ArtistThumbViewHolder extends RecyclerView.ViewHolder {
    private TextView mNameView;
    private TextView mGenreView;
    private ImageView mPictureView;
    private View mRootView;

    public ArtistThumbViewHolder(View itemView) {
        super(itemView);
        this.mNameView = (TextView) itemView.findViewById(R.id.artistName);
        this.mGenreView = (TextView) itemView.findViewById(R.id.artistGenres);
        this.mPictureView = (ImageView) itemView.findViewById(R.id.imageView);
        this.mRootView = itemView;
    }

    public TextView getNameView() {
        return this.mNameView;
    }

    public TextView getGenreView() {
        return this.mGenreView;
    }

    public ImageView getPictureView() {
        return this.mPictureView;
    }

    public View getRootView() {
        return this.mRootView;
    }
}