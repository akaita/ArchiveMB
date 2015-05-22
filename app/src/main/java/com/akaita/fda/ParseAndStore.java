package com.akaita.fda;

import android.util.JsonReader;
import android.util.JsonToken;

import com.akaita.fda.database.Album;
import com.akaita.fda.database.Artist;
import com.akaita.fda.database.ArtistAlbum;
import com.akaita.fda.database.ArtistGenre;
import com.akaita.fda.database.DaoFactory;
import com.akaita.fda.database.Genre;
import com.akaita.fda.database.Type;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.DatabaseConnection;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by mikel on 19/05/2015.
 */
public class ParseAndStore {

    JsonReader jsonReader;
    DatabaseConnection dbConn;
    Dao<Album, Integer> albumDao;
    Dao<ArtistAlbum, Integer> artistAlbumDao;
    Dao<ArtistGenre, Integer> artistGenreDao;
    Dao<Artist, Integer> artistDao;
    Dao<Genre, Integer> genreDao;
    Dao<Type, Integer> typeDao;

    public ParseAndStore(InputStreamReader streamReader) {
        this.jsonReader = new JsonReader(streamReader);
    }

    public void artistAndAlbums() throws SQLException {
        /* example origin
        {
            "artists": [
            {
                "id": 1,
                "genres": "pop,rock",
                "picture": "http://pixabay.com/static/uploads/photo/2013/07/24/15/19/music-166646_640.jpg?i",
                "name": "Inioth",
                "description": "Lorem ipsum dolor"
            },
            {
                "id": 2,
                "genres": "rock",
                "picture": "http://pixabay.com/static/uploads/photo/2013/12/11/10/55/plucked-string-instrument-226788_640.jpg?i",
                "name": "Tebinth",
                "description": "Nunc vel nulla sed"
            }
            ],
            "albums": [
            {
                "id": 1,
                "artistId": 1,
                "title": "Cour Plunder",
                "type": "single",
                "picture": "http://pixabay.com/static/uploads/photo/2013/12/10/22/53/sunrise-226679_640.jpg?i"
            },
            {
                "id": 2,
                "artistId": 1,
                "title": "Grief of the Plunderers",
                "type": "album",
                "picture": "http://pixabay.com/static/uploads/photo/2013/12/10/19/48/cookies-226628_640.jpg?i"
            }
            ]
        }
        */

        prepareDbBatch();

        try {
            this.jsonReader.beginObject();
            while( this.jsonReader.hasNext() ){
                final String objName = this.jsonReader.nextName();
                final boolean isNull = this.jsonReader.peek() == JsonToken.NULL;
                if( objName.equals( "artists" ) && !isNull ) {
                    readArtistArray();
                } else if ( objName.equals( "albums" ) && !isNull ) {
                    readAlbumArray();
                } else {
                    this.jsonReader.skipValue();
                }
            }
            this.jsonReader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
            abortDbBatch();
        }

        finishDbBatch();
    }

    private void readArtistArray() throws IOException, SQLException {
        this.jsonReader.beginArray();
        while( this.jsonReader.hasNext() ) {
            readArtist();
        }
        this.jsonReader.endArray();
    }

    private void readAlbumArray() throws IOException, SQLException {
        this.jsonReader.beginArray();
        while( this.jsonReader.hasNext() ) {
            readAlbum();
        }
        this.jsonReader.endArray();
    }

    private void readArtist() throws IOException, SQLException {
        this.jsonReader.beginObject();
        Long id = 0L;
        String[] genres = null;
        String pictureUrl = null;
        String name = null;
        String description = null;
        while( this.jsonReader.hasNext() ) {
            final String innerInnerName = this.jsonReader.nextName();
            final boolean isInnerInnerNull = this.jsonReader.peek() == JsonToken.NULL;
            if (innerInnerName.equals("id") && !isInnerInnerNull) {
                id = this.jsonReader.nextLong();
            } else if (innerInnerName.equals("genres") && !isInnerInnerNull) {
                genres = this.jsonReader.nextString().split(",");
            } else if (innerInnerName.equals("picture") && !isInnerInnerNull) {
                pictureUrl = this.jsonReader.nextString();
            } else if (innerInnerName.equals("name") && !isInnerInnerNull) {
                name = this.jsonReader.nextString();
            } else if (innerInnerName.equals("description") && !isInnerInnerNull) {
                description = this.jsonReader.nextString();
            } else {
                this.jsonReader.skipValue();
            }
        }
        this.jsonReader.endObject();

        saveArtistObject(id, name, description, pictureUrl, genres);
    }

    private void readAlbum() throws IOException, SQLException {
        this.jsonReader.beginObject();
        Long id = 0L;
        Long artistId = 0L;
        String title = null;
        String typeStr = null;
        String pictureUrl = null;
        while( this.jsonReader.hasNext() ) {
            final String innerInnerName = this.jsonReader.nextName();
            final boolean isInnerInnerNull = this.jsonReader.peek() == JsonToken.NULL;
            if (innerInnerName.equals("id") && !isInnerInnerNull) {
                id = this.jsonReader.nextLong();
            } else if (innerInnerName.equals("artistId") && !isInnerInnerNull) {
                artistId = this.jsonReader.nextLong();
            } else if (innerInnerName.equals("title") && !isInnerInnerNull) {
                title = this.jsonReader.nextString();
            } else if (innerInnerName.equals("type") && !isInnerInnerNull) {
                typeStr = this.jsonReader.nextString();
            } else if (innerInnerName.equals("picture") && !isInnerInnerNull) {
                pictureUrl = this.jsonReader.nextString();
            } else {
                this.jsonReader.skipValue();
            }
        }
        this.jsonReader.endObject();

        saveAlbumObject(id, artistId, title, pictureUrl, typeStr);
    }

    private void saveArtistObject(Long id, String name, String description, String pictureUrl, String[] genres) throws SQLException {
        Artist artist = new Artist(id, name, description, pictureUrl);
        this.artistDao.createIfNotExists(artist);

        for (String genreString : genres) {
            Genre genre = new Genre(genreString);
            this.genreDao.createIfNotExists(genre);

            ArtistGenre artistGenre = new ArtistGenre(artist, genre);
            this.artistGenreDao.createIfNotExists(artistGenre);
        }
    }

    private void saveAlbumObject(Long id, Long artistId, String title, String pictureUrl, String typeStr) throws SQLException {
        Type type = new Type(typeStr);
        this.typeDao.createIfNotExists(type);

        Album album = new Album(id, title, type, pictureUrl);
        this.albumDao.createIfNotExists(album);

        Artist dummyArtist = new Artist(artistId, null,null, null);
        ArtistAlbum artistAlbum = new ArtistAlbum(dummyArtist, album);
        this.artistAlbumDao.createIfNotExists(artistAlbum);
    }

    private void prepareDbBatch() throws SQLException {
        this.albumDao = DaoFactory.getInstance().getAlbums();
        this.artistDao = DaoFactory.getInstance().getArtists();
        this.artistAlbumDao = DaoFactory.getInstance().getArtistAlbum();
        this.artistGenreDao = DaoFactory.getInstance().getArtistGenre();
        this.genreDao = DaoFactory.getInstance().getGenres();
        this.typeDao = DaoFactory.getInstance().getTypes();

        this.dbConn = artistDao.startThreadConnection();
        this.artistDao.setAutoCommit(dbConn, false);
    }

    private void finishDbBatch() throws SQLException {
        this.artistDao.commit(dbConn);
        this.artistDao.setAutoCommit(dbConn, true);
    }

    private void abortDbBatch() throws SQLException {
        this.artistDao.rollBack(dbConn);
        this.artistDao.setAutoCommit(dbConn, true);
    }
}
