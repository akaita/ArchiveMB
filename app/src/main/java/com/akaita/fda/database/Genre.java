package com.akaita.fda.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by mikel on 18/05/2015.
 */
@DatabaseTable(tableName = "genre")
public class Genre {
    // we use this field-name so we can query for posts with a certain id
    public final static String ID_FIELD_NAME = "name";

    @DatabaseField(id = true, columnName = ID_FIELD_NAME)
    public String name;

    Genre() {
        // for ormlite
    }

    public Genre(String name) {
        this.name = name;
    }

    /*
     * custom queries
     */
    private static PreparedQuery<Artist> artistPreparedQuery = null;

    public List<Artist> artists() throws SQLException {
        if (artistPreparedQuery == null) {
            artistPreparedQuery = createArtistPreparedQuery();
        }
        artistPreparedQuery.setArgumentHolderValue(0, this);
        Dao<Artist, Long> artistDao = DaoFactory.getInstance().getArtists();
        return artistDao.query(artistPreparedQuery);
    }

    private PreparedQuery<Artist> createArtistPreparedQuery() throws SQLException {
        Dao<ArtistGenre, Integer> artistGenreDao = DaoFactory.getInstance().getArtistGenre();
        // build our inner query for UserPost objects
        QueryBuilder<ArtistGenre, Integer> artistGenreQb = artistGenreDao.queryBuilder();
        // just select the post-id field
        artistGenreQb.selectColumns(ArtistGenre.ARTIST_ID_FIELD_NAME);
        SelectArg genreSelectArg = new SelectArg();
        // you could also just pass in user1 here
        artistGenreQb.where().eq(ArtistGenre.GENRE_ID_FIELD_NAME, genreSelectArg);

        Dao<Artist, Long> artistDao = DaoFactory.getInstance().getArtists();
        // build our outer query for Post objects
        QueryBuilder<Artist, Long> artistQb = artistDao.queryBuilder();
        // where the id matches in the post-id from the inner query
        artistQb.where().in(Artist.ID_FIELD_NAME, artistGenreQb);
        return artistQb.prepare();
    }

}
