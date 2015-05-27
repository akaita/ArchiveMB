package com.akaita.fda.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by mikel on 18/05/2015.
 */
@DatabaseTable(tableName = "artistalbum")
public class ArtistAlbum {
    public static final String ARTIST_ID_FIELD_NAME = "artist_id";
    public static final String ALBUM_ID_FIELD_NAME = "album_id";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, columnName = ARTIST_ID_FIELD_NAME)
    private Artist artist;

    @DatabaseField(foreign = true, columnName = ALBUM_ID_FIELD_NAME)
    private Album album;

    ArtistAlbum() {
        // for ormlite
    }

    public ArtistAlbum(Artist artist, Album album) {
        this.artist = artist;
        this.album = album;
    }
}
