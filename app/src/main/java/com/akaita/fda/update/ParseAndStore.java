package com.akaita.fda.update;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import com.akaita.fda.database.objects.Album;
import com.akaita.fda.database.objects.Artist;
import com.akaita.fda.database.objects.ArtistAlbum;
import com.akaita.fda.database.objects.ArtistGenre;
import com.akaita.fda.database.DaoFactory;
import com.akaita.fda.database.objects.Genre;
import com.akaita.fda.database.objects.Type;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.DatabaseConnection;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

/**
 * Created by mikel on 19/05/2015.
 */
public class ParseAndStore {
    private JsonReader mJsonReader;
    private DatabaseConnection mDbConn;
    private Dao<Album, Long> mAlbumDao;
    private Dao<ArtistAlbum, Integer> mArtistAlbumDao;
    private Dao<ArtistGenre, Integer> mArtistGenreDao;
    private Dao<Artist, Long> mArtistDao;
    private Dao<Genre, String> mGenreDao;
    private Dao<Type, String> mTypeDao;

    public ParseAndStore(InputStreamReader streamReader) {
        this.mJsonReader = new JsonReader(streamReader);
    }

    public void artistAndAlbums() throws SQLException {
        /* origin sample
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
            Log.d(getClass().toString(), "Download and parse database: START");
            this.mJsonReader.beginObject();
            while( this.mJsonReader.hasNext() ){
                final String objName = this.mJsonReader.nextName();
                final boolean isNull = this.mJsonReader.peek() == JsonToken.NULL;
                if( objName.equals( "artists" ) && !isNull ) {
                    readArtistArray();
                } else if ( objName.equals( "albums" ) && !isNull ) {
                    readAlbumArray();
                } else {
                    this.mJsonReader.skipValue();
                }
            }
            this.mJsonReader.endObject();
            Log.d(getClass().toString(), "Download and parse database: FINISH");
        } catch (IOException e) {
            Log.d(getClass().toString(), e.getMessage());
            abortDbBatch();
        }

        finishDbBatch();
    }

    private void readArtistArray() throws IOException, SQLException {
        this.mJsonReader.beginArray();
        while( this.mJsonReader.hasNext() ) {
            readArtist();
        }
        this.mJsonReader.endArray();
    }

    private void readAlbumArray() throws IOException, SQLException {
        this.mJsonReader.beginArray();
        while( this.mJsonReader.hasNext() ) {
            readAlbum();
        }
        this.mJsonReader.endArray();
    }

    private void readArtist() throws IOException, SQLException {
        this.mJsonReader.beginObject();
        Long id = 0L;
        String[] genres = null;
        String pictureUrl = null;
        String name = null;
        String description = null;
        while( this.mJsonReader.hasNext() ) {
            final String innerInnerName = this.mJsonReader.nextName();
            final boolean isInnerInnerNull = this.mJsonReader.peek() == JsonToken.NULL;
            if (innerInnerName.equals("id") && !isInnerInnerNull) {
                id = this.mJsonReader.nextLong();
            } else if (innerInnerName.equals("genres") && !isInnerInnerNull) {
                genres = this.mJsonReader.nextString().split(",");
            } else if (innerInnerName.equals("picture") && !isInnerInnerNull) {
                pictureUrl = this.mJsonReader.nextString().trim();
            } else if (innerInnerName.equals("name") && !isInnerInnerNull) {
                name = this.mJsonReader.nextString().trim();
            } else if (innerInnerName.equals("description") && !isInnerInnerNull) {
                description = this.mJsonReader.nextString().trim();
            } else {
                this.mJsonReader.skipValue();
            }
        }
        this.mJsonReader.endObject();

        saveArtistObject(id, name, description, pictureUrl, genres);
    }

    private void readAlbum() throws IOException, SQLException {
        this.mJsonReader.beginObject();
        Long id = 0L;
        Long artistId = 0L;
        String title = null;
        String typeStr = null;
        String pictureUrl = null;
        while( this.mJsonReader.hasNext() ) {
            final String innerInnerName = this.mJsonReader.nextName();
            final boolean isInnerInnerNull = this.mJsonReader.peek() == JsonToken.NULL;
            if (innerInnerName.equals("id") && !isInnerInnerNull) {
                id = this.mJsonReader.nextLong();
            } else if (innerInnerName.equals("artistId") && !isInnerInnerNull) {
                artistId = this.mJsonReader.nextLong();
            } else if (innerInnerName.equals("title") && !isInnerInnerNull) {
                title = this.mJsonReader.nextString().trim();
            } else if (innerInnerName.equals("type") && !isInnerInnerNull) {
                typeStr = this.mJsonReader.nextString().trim();
            } else if (innerInnerName.equals("picture") && !isInnerInnerNull) {
                pictureUrl = this.mJsonReader.nextString().trim();
            } else {
                this.mJsonReader.skipValue();
            }
        }
        this.mJsonReader.endObject();

        saveAlbumObject(id, artistId, title, pictureUrl, typeStr);
    }

    private void saveArtistObject(Long id, String name, String description, String pictureUrl, String[] genres) throws SQLException {
        Artist artist = new Artist(id, name, description, pictureUrl);
        this.mArtistDao.createIfNotExists(artist);

        for (String genreString : genres) {
            Genre genre = new Genre(genreString.trim());
            this.mGenreDao.createIfNotExists(genre);

            ArtistGenre artistGenre = new ArtistGenre(artist, genre);
            this.mArtistGenreDao.createIfNotExists(artistGenre);
        }
    }

    private void saveAlbumObject(Long id, Long artistId, String title, String pictureUrl, String typeStr) throws SQLException {
        Type type = new Type(typeStr);
        this.mTypeDao.createIfNotExists(type);

        Album album = new Album(id, title, type, pictureUrl);
        this.mAlbumDao.createIfNotExists(album);

        Artist dummyArtist = new Artist(artistId, null,null, null);
        ArtistAlbum artistAlbum = new ArtistAlbum(dummyArtist, album);
        this.mArtistAlbumDao.createIfNotExists(artistAlbum);
    }

    private void prepareDbBatch() throws SQLException {
        Log.d(getClass().toString(), "Batch database update: START");
        this.mAlbumDao = DaoFactory.getInstance().getAlbumDao();
        this.mArtistDao = DaoFactory.getInstance().getArtistDao();
        this.mArtistAlbumDao = DaoFactory.getInstance().getArtistAlbumDao();
        this.mArtistGenreDao = DaoFactory.getInstance().getArtistGenreDao();
        this.mGenreDao = DaoFactory.getInstance().getGenreDao();
        this.mTypeDao = DaoFactory.getInstance().getTypeDao();

        this.mDbConn = mArtistDao.startThreadConnection();
        this.mArtistDao.setAutoCommit(mDbConn, false);
    }

    private void finishDbBatch() throws SQLException {
        Log.d(getClass().toString(), "Batch database update: FINISH");
        this.mArtistDao.commit(mDbConn);
        this.mArtistDao.setAutoCommit(mDbConn, true);
    }

    private void abortDbBatch() throws SQLException {
        Log.d(getClass().toString(), "Batch database update: ABORT");
        this.mArtistDao.rollBack(mDbConn);
        this.mArtistDao.setAutoCommit(mDbConn, true);
    }
}
