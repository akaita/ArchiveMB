package com.akaita.fda.database;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

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
}
