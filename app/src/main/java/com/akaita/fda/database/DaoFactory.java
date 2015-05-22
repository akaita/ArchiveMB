package com.akaita.fda.database;

import android.app.Application;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * Created by mikel on 18/05/2015.
 */
public class DaoFactory extends Application {
    private DatabaseHelper databaseHelper = null;

    private Dao<Artist, Integer> artists = null;
    private Dao<ArtistAlbum, Integer> artistAlbum = null;
    private Dao<ArtistGenre, Integer> artistGenre = null;
    private Dao<Genre, Integer> genres = null;
    private Dao<Album, Integer> albums = null;
    private Dao<Type, Integer> types = null;
    private Dao<Property, Integer> properties = null;

    private static DaoFactory instance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        databaseHelper = new DatabaseHelper(this);
        instance = this;
    }

    public static DaoFactory getInstance(){
        return instance;
    }

    public Dao<Artist, Integer> getArtists() throws SQLException {
        if (artists == null) {
            artists = databaseHelper.getDao(Artist.class);
        }
        return artists;
    }

    public Dao<ArtistAlbum, Integer> getArtistAlbum() throws SQLException {
        if (artistAlbum == null) {
            artistAlbum = databaseHelper.getDao(ArtistAlbum.class);
        }
        return artistAlbum;
    }

    public Dao<ArtistGenre, Integer> getArtistGenre() throws SQLException {
        if (artistGenre == null) {
            artistGenre = databaseHelper.getDao(ArtistGenre.class);
        }
        return artistGenre;
    }

    public Dao<Genre, Integer> getGenres() throws SQLException {
        if (genres == null) {
            genres = databaseHelper.getDao(Genre.class);
        }
        return genres;
    }

    public Dao<Album, Integer> getAlbums() throws SQLException {
        if (albums == null) {
            albums = databaseHelper.getDao(Album.class);
        }
        return albums;
    }

    public Dao<Type, Integer> getTypes() throws SQLException {
        if (types == null) {
            types = databaseHelper.getDao(Type.class);
        }
        return types;
    }

    public Dao<Property, Integer> getProperties() throws SQLException {
        if (properties == null) {
            properties = databaseHelper.getDao(Property.class);
        }
        return properties;
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
}
