package com.akaita.fda;

/**
 * Created by mikel on 20/05/2015.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akaita.fda.database.Album;
import com.akaita.fda.database.Artist;
import com.akaita.fda.database.Genre;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumThumbViewHolder> {
    private final static String SEPARATOR = " ";

    private List<Album> albumList;
    private Context mContext;

    public AlbumAdapter(Context context) {
        this.mContext = context;
        albumList = new ArrayList<>();
    }

    @Override
    public AlbumThumbViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_thumb_card, parent, false);
        return new AlbumThumbViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AlbumThumbViewHolder holder, final int position) {
        holder.nameView.setText(albumList.get(position).title);
        SetImage.setImage(mContext, holder.picture, albumList.get(position).pictureUrl);
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public void add(Album item, int position) {
        albumList.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Album item) {
        //TODO check whether Album comparison works or not
        int position = albumList.indexOf(item);
        albumList.remove(position);
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
