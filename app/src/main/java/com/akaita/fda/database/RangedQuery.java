package com.akaita.fda.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by mikel on 20/05/2015.
 */
public class RangedQuery {
    public static List<Artist> getArtistRangeByGenre(long offset, long range, String genreFilter) throws SQLException {
        if (genreFilter==null){
            return getArtistRange(offset, range);
        }
        PreparedQuery<Artist> artistRangePreparedQuery = createArtistRangeByGenrePreparedQuery(offset, range, genreFilter);
        Dao<Artist, Long> artistDao = DaoFactory.getInstance().getArtistDao();
        Genre genre = DaoFactory.getInstance().getGenreDao().queryForId(genreFilter);
        artistRangePreparedQuery.setArgumentHolderValue(0, genre);
        return artistDao.query(artistRangePreparedQuery);
    }

    public static List<Artist> getArtistRange(long offset, long range) throws SQLException {
        PreparedQuery<Artist> artistRangePreparedQuery = createArtistRangePreparedQuery(offset, range);
        Dao<Artist, Long> artistDao = DaoFactory.getInstance().getArtistDao();
        return artistDao.query(artistRangePreparedQuery);
    }

    private static PreparedQuery<Artist> createArtistRangeByGenrePreparedQuery(long offset, long range, String genreFilter) throws SQLException {
        Dao<ArtistGenre, Integer> artistGenreDao = DaoFactory.getInstance().getArtistGenreDao();
        QueryBuilder<ArtistGenre, Integer> artistGenreQb = artistGenreDao.queryBuilder();
        artistGenreQb.selectColumns(ArtistGenre.ARTIST_ID_FIELD_NAME);
        SelectArg genreSelectArg = new SelectArg();
        artistGenreQb.where().eq(ArtistGenre.GENRE_ID_FIELD_NAME, genreSelectArg);

        Dao<Artist, Long> artistDao = DaoFactory.getInstance().getArtistDao();
        QueryBuilder<Artist, Long> artistQb = artistDao.queryBuilder();
        artistQb.where().in(Artist.ID_FIELD_NAME, artistGenreQb);
        artistQb.offset(offset);
        artistQb.limit(range);
        return artistQb.prepare();
    }

    private static PreparedQuery<Artist> createArtistRangePreparedQuery(long offset, long range) throws SQLException {
        Dao<Artist, Long> artistDao = DaoFactory.getInstance().getArtistDao();
        QueryBuilder<Artist, Long> artistQb = artistDao.queryBuilder();
        artistQb.offset(offset);
        artistQb.limit(range);
        return artistQb.prepare();
    }
}