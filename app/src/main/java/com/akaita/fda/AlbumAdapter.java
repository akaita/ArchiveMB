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

public class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static String SEPARATOR = " ";
    private final static int HEADER_COUNT = 1; //TODO not really a COUNT, should change to List<View>mHeader

    public static class VIEW_TYPES {
        public static final int NORMAL = 1;
        public static final int HEADER = 2;
    }

    private List<Album> albumList;
    private View mHeader;
    private Context mContext;

    public AlbumAdapter(Context context) {
        this.mContext = context;
        albumList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case VIEW_TYPES.HEADER:
                return new ViewHolder(mHeader);
            case VIEW_TYPES.NORMAL:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_thumb_card, parent, false);
                return new AlbumThumbViewHolder(view);
        }
        return new ViewHolder(null);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (position < HEADER_COUNT) {
        } else {
            int listPosition = position - HEADER_COUNT;
            AlbumThumbViewHolder albumThumbViewHolder = ((AlbumThumbViewHolder) holder);
            albumThumbViewHolder.nameView.setText(albumList.get(listPosition).title);
            SetImage.setImage(mContext, albumThumbViewHolder.picture, albumList.get(listPosition).pictureUrl);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < HEADER_COUNT)
            return VIEW_TYPES.HEADER;
        else
            return VIEW_TYPES.NORMAL;
    }


    @Override
    public int getItemCount() {
        return albumList.size() + HEADER_COUNT;
    }

    public void add(Album item, int position) {
        int listPosition = position - HEADER_COUNT;
        albumList.add(listPosition, item);
        notifyItemInserted(position);
    }

    public void remove(Album item) {
        //TODO check whether Album comparison works or not
        int position = albumList.indexOf(item);
        int listPosition = position - HEADER_COUNT;
        albumList.remove(listPosition);
        notifyItemRemoved(position);
    }

    public void addHeader(View header) {
        this.mHeader = header;
        notifyItemChanged(HEADER_COUNT);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
