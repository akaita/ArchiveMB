package com.akaita.fda.database;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by mikel on 18/05/2015.
 */
@DatabaseTable(tableName = "property")
public class Property {
    public final static String ID_FIELD_NAME = "id";

    @DatabaseField(id = true, columnName = ID_FIELD_NAME)
    public String name;

    @DatabaseField
    public String value;

    Property(){
        // for ormlite
    }

    public Property (String name, String value) {
        this.name = name;
        this.value = value;
    }
}
