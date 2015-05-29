package com.akaita.fda.database.objects;

/**
 * Created by mikel on 18/05/2015.
 */
import com.akaita.fda.database.DaoFactory;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.util.List;

@DatabaseTable(tableName = "artist")
public class Artist {
    public static final String ID_FIELD_NAME = "id";
    public static final String NAME_FIELD_NAME = "name";

    @DatabaseField(id = true, columnName = ID_FIELD_NAME)
    private long id;

    @DatabaseField(columnName = NAME_FIELD_NAME)
    private String name;

    @DatabaseField
    private String description;

    @DatabaseField
    private String pictureUrl;

    Artist() {
        // for ormlite
    }

    public Artist(long id, String name, String description, String pictureUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.pictureUrl = pictureUrl;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }


    /*
     * many-to-many queries
     */

    private static PreparedQuery<Album> albumPreparedQuery = null;
    private static PreparedQuery<Genre> genrePreparedQuery = null;

    public List<Album> albums() throws SQLException {
        if (albumPreparedQuery == null) {
            albumPreparedQuery = createAlbumPreparedQuery();
        }
        albumPreparedQuery.setArgumentHolderValue(0, this);
        Dao<Album, Long> albumDao = DaoFactory.getInstance().getAlbumDao();
        return albumDao.query(albumPreparedQuery);
    }

    public List<Genre> genres() throws SQLException {
        if (genrePreparedQuery == null) {
            genrePreparedQuery = createGenrePreparedQuery();
        }
        genrePreparedQuery.setArgumentHolderValue(0, this);
        Dao<Genre, String> genreDao = DaoFactory.getInstance().getGenreDao();
        return genreDao.query(genrePreparedQuery);
    }

    private PreparedQuery<Album> createAlbumPreparedQuery() throws SQLException {
        Dao<ArtistAlbum, Integer> artistAlbumDao = DaoFactory.getInstance().getArtistAlbumDao();
        QueryBuilder<ArtistAlbum, Integer> artistAlbumQb = artistAlbumDao.queryBuilder();
        artistAlbumQb.selectColumns(ArtistAlbum.ALBUM_ID_FIELD_NAME);
        SelectArg artistSelectArg = new SelectArg();
        artistAlbumQb.where().eq(ArtistAlbum.ARTIST_ID_FIELD_NAME, artistSelectArg);

        Dao<Album, Long> albumDao = DaoFactory.getInstance().getAlbumDao();
        QueryBuilder<Album, Long> albumQb = albumDao.queryBuilder();
        albumQb.where().in(Album.ID_FIELD_NAME, artistAlbumQb);
        return albumQb.prepare();
    }

    private PreparedQuery<Genre> createGenrePreparedQuery() throws SQLException {
        Dao<ArtistGenre, Integer> artistGenreDao = DaoFactory.getInstance().getArtistGenreDao();
        QueryBuilder<ArtistGenre, Integer> artistGenreQb = artistGenreDao.queryBuilder();
        artistGenreQb.selectColumns(ArtistGenre.GENRE_ID_FIELD_NAME);
        SelectArg artistSelectArg = new SelectArg();
        artistGenreQb.where().eq(ArtistGenre.ARTIST_ID_FIELD_NAME, artistSelectArg);

        Dao<Genre, String> genreDao = DaoFactory.getInstance().getGenreDao();
        QueryBuilder<Genre, String> genreQb = genreDao.queryBuilder();
        genreQb.where().in(Genre.ID_FIELD_NAME, artistGenreQb);
        return genreQb.prepare();
    }
}
