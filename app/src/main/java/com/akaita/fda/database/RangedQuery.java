package com.akaita.fda.database;

import com.akaita.fda.database.objects.Artist;
import com.akaita.fda.database.objects.ArtistGenre;
import com.akaita.fda.database.objects.Genre;
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
        PreparedQuery<Artist> artistRangePreparedQuery = createArtistRangeByGenrePreparedQuery(offset, range);
        Dao<Artist, Long> artistDao = DaoFactory.getInstance().getArtistDao();
        Genre genre = DaoFactory.getInstance().getGenreDao().queryForId(genreFilter);
        artistRangePreparedQuery.setArgumentHolderValue(0, genre);
        return artistDao.query(artistRangePreparedQuery);
    }

    public static List<Artist> getArtistRangeByName(long offset, long range, String nameFilter) throws SQLException {
        if (nameFilter==null){
            return getArtistRange(offset, range);
        }
        PreparedQuery<Artist> artistRangePreparedQuery = createArtistRangeByNamePreparedQuery(offset, range);
        Dao<Artist, Long> artistDao = DaoFactory.getInstance().getArtistDao();
        artistRangePreparedQuery.setArgumentHolderValue(0, "%"+nameFilter+"%");
        return artistDao.query(artistRangePreparedQuery);
    }

    public static List<Artist> getArtistRangeByGenreByName(long offset, long range, String genreFilter, String nameFilter) throws SQLException {
        if (genreFilter==null && nameFilter==null){
            return getArtistRange(offset, range);
        }
        if (genreFilter!=null && nameFilter==null){
            return getArtistRangeByGenre(offset, range, genreFilter);
        }
        if (genreFilter==null){
            return getArtistRangeByName(offset, range, nameFilter);
        }
        PreparedQuery<Artist> artistRangePreparedQuery = createArtistRangeByGenreByNamePreparedQuery(offset, range);
        Dao<Artist, Long> artistDao = DaoFactory.getInstance().getArtistDao();
        Genre genre = DaoFactory.getInstance().getGenreDao().queryForId(genreFilter);
        artistRangePreparedQuery.setArgumentHolderValue(0, genre);
        artistRangePreparedQuery.setArgumentHolderValue(1, "%"+nameFilter+"%");
        return artistDao.query(artistRangePreparedQuery);
    }

    public static List<Artist> getArtistRange(long offset, long range) throws SQLException {
        PreparedQuery<Artist> artistRangePreparedQuery = createArtistRangePreparedQuery(offset, range);
        Dao<Artist, Long> artistDao = DaoFactory.getInstance().getArtistDao();
        return artistDao.query(artistRangePreparedQuery);
    }



    /*
     * query preparations
     */

    private static PreparedQuery<Artist> createArtistRangeByGenrePreparedQuery(long offset, long range) throws SQLException {
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

    private static PreparedQuery<Artist> createArtistRangeByGenreByNamePreparedQuery(long offset, long range) throws SQLException {
        Dao<ArtistGenre, Integer> artistGenreDao = DaoFactory.getInstance().getArtistGenreDao();
        QueryBuilder<ArtistGenre, Integer> artistGenreQb = artistGenreDao.queryBuilder();
        artistGenreQb.selectColumns(ArtistGenre.ARTIST_ID_FIELD_NAME);
        SelectArg genreSelectArg = new SelectArg();
        artistGenreQb.where().eq(ArtistGenre.GENRE_ID_FIELD_NAME, genreSelectArg);

        Dao<Artist, Long> artistDao = DaoFactory.getInstance().getArtistDao();
        QueryBuilder<Artist, Long> artistQb = artistDao.queryBuilder();
        SelectArg artistSelectArg = new SelectArg();
        artistQb.where()
                .in(Artist.ID_FIELD_NAME, artistGenreQb)
                .and().like(Artist.NAME_FIELD_NAME, artistSelectArg);
        artistQb.offset(offset);
        artistQb.limit(range);
        return artistQb.prepare();
    }

    private static PreparedQuery<Artist> createArtistRangeByNamePreparedQuery(long offset, long range) throws SQLException {
        Dao<Artist, Long> artistDao = DaoFactory.getInstance().getArtistDao();
        QueryBuilder<Artist, Long> artistQb = artistDao.queryBuilder();
        SelectArg artistSelectArg = new SelectArg();
        artistQb.where().like(Artist.NAME_FIELD_NAME, artistSelectArg);
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