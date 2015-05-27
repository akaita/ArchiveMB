package com.akaita.fda.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by mikel on 18/05/2015.
 */
@DatabaseTable(tableName = "artistgenre")
public class ArtistGenre {
    public static final String ARTIST_ID_FIELD_NAME = "artist_id";
    public static final String GENRE_ID_FIELD_NAME = "genre_id";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, columnName = ARTIST_ID_FIELD_NAME)
    private Artist artist;

    @DatabaseField(foreign = true, columnName = GENRE_ID_FIELD_NAME)
    private Genre genre;

    ArtistGenre() {
        // for ormlite
    }

    public ArtistGenre(Artist artist, Genre genre) {
        this.artist = artist;
        this.genre = genre;
    }
}
