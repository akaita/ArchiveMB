package com.akaita.fda.database.objects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by mikel on 18/05/2015.
 */
@DatabaseTable(tableName = "property")
public class Property {
    public static final String ID_FIELD_NAME = "id";

    @DatabaseField(id = true, columnName = ID_FIELD_NAME)
    private String name;

    @DatabaseField
    private String value;

    Property(){
        // for ormlite
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public Property (String name, String value) {
        this.name = name;
        this.value = value;
    }
}
