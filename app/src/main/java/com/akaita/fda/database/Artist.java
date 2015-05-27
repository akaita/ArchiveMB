package com.akaita.fda.database;

/**
 * Created by mikel on 18/05/2015.
 */

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
    // we use this field-name so we can query for posts with a certain id
    public final static String ID_FIELD_NAME = "id";

    // this id is generated by the database and set on the object when it is passed to the create method
    @DatabaseField(id = true, columnName = ID_FIELD_NAME)
    public long id;

    @DatabaseField
    public String name;

    @DatabaseField
    public String description;

    @DatabaseField
    public String pictureUrl;

    Artist() {
        // for ormlite
    }

    public Artist(long id, String name, String description, String pictureUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.pictureUrl = pictureUrl;
    }


    /*
     * custom queries
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
        // build our inner query for UserPost objects
        QueryBuilder<ArtistAlbum, Integer> artistAlbumQb = artistAlbumDao.queryBuilder();
        // just select the post-id field
        artistAlbumQb.selectColumns(ArtistAlbum.ALBUM_ID_FIELD_NAME);
        SelectArg artistSelectArg = new SelectArg();
        // you could also just pass in user1 here
        artistAlbumQb.where().eq(ArtistAlbum.ARTIST_ID_FIELD_NAME, artistSelectArg);

        Dao<Album, Long> albumDao = DaoFactory.getInstance().getAlbumDao();
        // build our outer query for Post objects
        QueryBuilder<Album, Long> albumQb = albumDao.queryBuilder();
        // where the id matches in the post-id from the inner query
        albumQb.where().in(Album.ID_FIELD_NAME, artistAlbumQb);
        return albumQb.prepare();
    }

    private PreparedQuery<Genre> createGenrePreparedQuery() throws SQLException {
        Dao<ArtistGenre, Integer> artistGenreDao = DaoFactory.getInstance().getArtistGenreDao();
        // build our inner query for UserPost objects
        QueryBuilder<ArtistGenre, Integer> artistGenreQb = artistGenreDao.queryBuilder();
        // just select the post-id field
        artistGenreQb.selectColumns(ArtistGenre.GENRE_ID_FIELD_NAME);
        SelectArg artistSelectArg = new SelectArg();
        // you could also just pass in user1 here
        artistGenreQb.where().eq(ArtistGenre.ARTIST_ID_FIELD_NAME, artistSelectArg);

        Dao<Genre, String> genreDao = DaoFactory.getInstance().getGenreDao();
        // build our outer query for Post objects
        QueryBuilder<Genre, String> genreQb = genreDao.queryBuilder();
        // where the id matches in the post-id from the inner query
        genreQb.where().in(Genre.ID_FIELD_NAME, artistGenreQb);
        return genreQb.prepare();
    }

}
