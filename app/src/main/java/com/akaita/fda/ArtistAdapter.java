package com.akaita.fda;

/**
 * Created by mikel on 20/05/2015.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akaita.fda.imagedownload.SetImage;
import com.akaita.fda.database.objects.Artist;
import com.akaita.fda.database.objects.Genre;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistThumbViewHolder> {
    public static final String SEPARATOR = " ";
    private OnArtistItemSelectedListener mOnArtistItemSelectedListener;

    private List<Artist> mArtistList;
    private Context mContext;

    public ArtistAdapter(Context context) {
        this.mContext = context;
        this.mArtistList = new ArrayList<>();
    }

    public void setOnArtistItemSelectedListener(OnArtistItemSelectedListener listener) {
        this.mOnArtistItemSelectedListener = listener;
    }
    public interface OnArtistItemSelectedListener {
        void onArtistItemSelected(Artist artist);
    }

    @Override
    public ArtistThumbViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_thumb_card, parent, false);
        return new ArtistThumbViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ArtistThumbViewHolder holder, final int position) {
        try {
            holder.getGenreView().setText(concatenate(mArtistList.get(position).genres(), SEPARATOR));
        } catch (SQLException e) {
            Log.e(getClass().toString(), "SQLException (" + e.getSQLState() + "): " + e.getMessage());
        }
        holder.getNameView().setText(this.mArtistList.get(position).getName());
        if (this.mOnArtistItemSelectedListener != null) {
            holder.getRootView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnArtistItemSelectedListener.onArtistItemSelected(mArtistList.get(position));
                }
            });
        }

        SetImage.setImage(mContext, holder.getPictureView(), mArtistList.get(position).getPictureUrl());
    }

    @Override
    public int getItemCount() {
        return mArtistList.size();
    }

    public void add(Artist item, int position) {
        mArtistList.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        mArtistList.remove(position);
        notifyItemRemoved(position);
    }

    public void removeAll() {
        for (int i=0 ; i<this.mArtistList.size() ; i++){
            remove(i);
        }
    }

    public static String concatenate(List<Genre> list, String separator) {
        StringBuffer result = new StringBuffer();
        for (Genre genre : list) {
            result.append( genre.getName() );
            result.append( separator );
        }
        return result.delete(result.length()-separator.length(), result.length()).toString();
    }
}
