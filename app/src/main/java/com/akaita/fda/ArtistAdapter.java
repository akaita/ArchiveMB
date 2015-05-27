package com.akaita.fda;

/**
 * Created by mikel on 20/05/2015.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akaita.fda.database.Artist;
import com.akaita.fda.database.Genre;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistThumbViewHolder> {
    public static final String SEPARATOR = " ";
    OnArtistItemSelectedListener mCallback;

    private List<Artist> artistList;
    private Context mContext;

    public ArtistAdapter(Context context) {
        this.mContext = context;
        artistList = new ArrayList<>();
    }

    public void setOnArtistItemSelectedListener(OnArtistItemSelectedListener listener) {
        mCallback = listener;
    }
    public interface OnArtistItemSelectedListener {
        public void onArtistItemSelected(Artist artist);
    }

    @Override
    public ArtistThumbViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_thumb_card, parent, false);
        return new ArtistThumbViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ArtistThumbViewHolder holder, final int position) {
        try {
            holder.genreView.setText(concatenate(artistList.get(position).genres(), SEPARATOR));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        holder.nameView.setText(artistList.get(position).name);
        if (mCallback != null) {
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onArtistItemSelected(artistList.get(position));
                }
            });
        }

        SetImage.setImage(mContext, holder.picture, artistList.get(position).pictureUrl);
    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    public void add(Artist item, int position) {
        artistList.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Artist item) {
        //TODO check whether Artist comparison works or not
        int position = artistList.indexOf(item);
        artistList.remove(position);
        notifyItemRemoved(position);
    }

    public static String concatenate(List<Genre> list, String separator) {
        StringBuffer result = new StringBuffer();
        for (Genre genre : list) {
            result.append( genre.name );
            result.append( separator );
        }
        return result.delete(result.length()-separator.length(), result.length()).toString();
    }
}
