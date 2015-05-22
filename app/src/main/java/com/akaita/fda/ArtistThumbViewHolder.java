package com.akaita.fda;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by mikel on 20/05/2015.
 */
public class ArtistThumbViewHolder extends RecyclerView.ViewHolder {
    public TextView nameView;
    public ImageView picture;
    public ArtistThumbViewHolder(View itemView) {
        super(itemView);
        nameView = (TextView) itemView.findViewById(R.id.artistName);
        picture = (ImageView) itemView.findViewById(R.id.imageView);
    }
}