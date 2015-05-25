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
    public static List<Artist> getArtistRange(long offset, long range) throws SQLException {
        PreparedQuery<Artist> artistRangePreparedQuery = createArtistRangePreparedQuery(offset, range);
        Dao<Artist, Long> artistDao = DaoFactory.getInstance().getArtists();
        return artistDao.query(artistRangePreparedQuery);
    }

    private static PreparedQuery<Artist> createArtistRangePreparedQuery(long offset, long range) throws SQLException {
        Dao<Artist, Long> artistDao = DaoFactory.getInstance().getArtists();
        // build our inner query for UserPost objects
        QueryBuilder<Artist, Long> artistQb = artistDao.queryBuilder();
        artistQb.offset(offset);
        artistQb.limit(range);
        return artistQb.prepare();
    }

}
