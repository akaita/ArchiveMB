package com.akaita.fda.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by mikel on 18/05/2015.
 */
@DatabaseTable(tableName = "album")
public class Album {
    public static final String ID_FIELD_NAME = "id";
    public static final String TYPE_ID_FIELD_NAME = "type_id";

    @DatabaseField(id = true, columnName = ID_FIELD_NAME)
    private long id;

    @DatabaseField
    private String title;

    @DatabaseField
    private String pictureUrl;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = TYPE_ID_FIELD_NAME)
    private Type type;

    Album() {
        // for ormlite
    }

    public Album(long id, String title, Type type, String pictureUrl) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.pictureUrl = pictureUrl;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public Type getType() {
        return type;
    }
}
