package com.akaita.fda.database.objects;

import com.akaita.fda.database.DaoFactory;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
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
    public static final String ID_FIELD_NAME = "name";

    @DatabaseField(id = true, columnName = ID_FIELD_NAME)
    private String name;

    Genre() {
        // for ormlite
    }

    public Genre(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /*
     * many-to-many queries
     */

    private static PreparedQuery<Artist> artistPreparedQuery = null;

    public List<Artist> artists() throws SQLException {
        if (artistPreparedQuery == null) {
            artistPreparedQuery = createArtistPreparedQuery();
        }
        artistPreparedQuery.setArgumentHolderValue(0, this);
        Dao<Artist, Long> artistDao = DaoFactory.getInstance().getArtistDao();
        return artistDao.query(artistPreparedQuery);
    }

    private PreparedQuery<Artist> createArtistPreparedQuery() throws SQLException {
        Dao<ArtistGenre, Integer> artistGenreDao = DaoFactory.getInstance().getArtistGenreDao();
        QueryBuilder<ArtistGenre, Integer> artistGenreQb = artistGenreDao.queryBuilder();
        artistGenreQb.selectColumns(ArtistGenre.ARTIST_ID_FIELD_NAME);
        SelectArg genreSelectArg = new SelectArg();
        artistGenreQb.where().eq(ArtistGenre.GENRE_ID_FIELD_NAME, genreSelectArg);

        Dao<Artist, Long> artistDao = DaoFactory.getInstance().getArtistDao();
        QueryBuilder<Artist, Long> artistQb = artistDao.queryBuilder();
        artistQb.where().in(Artist.ID_FIELD_NAME, artistGenreQb);
        return artistQb.prepare();
    }

}
