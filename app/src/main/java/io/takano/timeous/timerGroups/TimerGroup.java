package io.takano.timeous.timerGroups;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@SuppressWarnings("CanBeFinal")
@Entity(tableName = "timer_groups", indices = {@Index(value = "id", unique = true)})
public class TimerGroup {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    protected  Integer id;

    private String name;

    public TimerGroup(String name) {
        this.name = name;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
