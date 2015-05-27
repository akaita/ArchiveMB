package com.akaita.fda;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by mikel on 20/05/2015.
 */
public class ArtistThumbViewHolder extends RecyclerView.ViewHolder {
    private TextView nameView;
    private TextView genreView;
    private ImageView pictureView;
    private View rootView;

    public ArtistThumbViewHolder(View itemView) {
        super(itemView);
        nameView = (TextView) itemView.findViewById(R.id.artistName);
        genreView = (TextView) itemView.findViewById(R.id.artistGenres);
        pictureView = (ImageView) itemView.findViewById(R.id.imageView);
        rootView = itemView;
    }

    public TextView getNameView() {
        return nameView;
    }

    public TextView getGenreView() {
        return genreView;
    }

    public ImageView getPictureView() {
        return pictureView;
    }

    public View getRootView() {
        return rootView;
    }
}