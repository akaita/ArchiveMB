package com.akaita.fda.database.objects;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by mikel on 18/05/2015.
 */
@DatabaseTable(tableName = "type")
public class Type {
    @DatabaseField(id = true)
    private String name;

    @ForeignCollectionField
    private ForeignCollection<Album> albums;

    Type() {
        // for ormlite
    }

    public Type(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
