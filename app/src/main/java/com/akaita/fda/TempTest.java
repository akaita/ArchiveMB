package com.akaita.fda;

import android.util.Log;

import com.akaita.fda.database.Album;
import com.akaita.fda.database.Artist;
import com.akaita.fda.database.ArtistAlbum;
import com.akaita.fda.database.DaoFactory;
import com.akaita.fda.database.Genre;
import com.akaita.fda.database.Type;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by mikel on 20/05/2015.
 */
public class TempTest {
    public static void testStoredData(){
        try {
            List<Album> albumList = DaoFactory.getInstance().getAlbums().queryForAll();
            List<Artist> artistList = DaoFactory.getInstance().getArtists().queryForAll();
            for (int i=0; i<2 && i<artistList.size() ; i++){
                Artist artist = artistList.get(i);
                List<Album> albums = artist.albums();
                List<Genre> genres = artist.genres();
                String saddsf = "asdasd";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void testDatabase() throws SQLException {
        DaoFactory factory = DaoFactory.getInstance();
        Dao<Artist, Integer> artist = factory.getArtists();
        Dao<Album, Integer> album = factory.getAlbums();
        Dao<ArtistAlbum, Integer> artistAlbum = factory.getArtistAlbum();

        Type type1 = new Type("type1");
        Type type2 = new Type("type2");

        Album album1 = new Album(1, "title1", type1, "url1");
        album.createIfNotExists(album1);

        Album album2 = new Album(2, "title2", type2, "url2");
        album.createIfNotExists(album2);

        Artist newArtist1 = new Artist(1, "artist1", "descr1", "url1");
        artist.createIfNotExists(newArtist1);
        ArtistAlbum artistAlbum1 = new ArtistAlbum(newArtist1,album1);
        artistAlbum.createIfNotExists(artistAlbum1);

        Artist newArtist2 = new Artist(2, "artist2", "descr2", "url2");
        artist.createIfNotExists(newArtist2);
        ArtistAlbum artistAlbum2 = new ArtistAlbum(newArtist2,album1);
        ArtistAlbum artistAlbum3 = new ArtistAlbum(newArtist2,album2);
        artistAlbum.createIfNotExists(artistAlbum2);
        artistAlbum.createIfNotExists(artistAlbum3);

        List<Artist> todos = artist.queryForAll();
        Log.e("DB", "Todos son: " + todos.size());
        List<Album> albumList1 = newArtist1.albums();
        Log.e("DB", "Albums de artist 1: " + albumList1.size());
        List<Album> albumList2 = newArtist2.albums();
        Log.e("DB", "Albums de artist 2: " + albumList2.size());
    }

}
