package io.takano.timeous.database;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@SuppressWarnings("CanBeFinal")
@Entity(tableName = "routines", indices = {@Index(value = "id", unique = true)})
public class Routine implements Serializable {

    @SuppressWarnings("NotNullFieldNotInitialized")
    @NonNull
    @PrimaryKey(autoGenerate = true)
    protected Long id;

    private String name;

    public Routine(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
