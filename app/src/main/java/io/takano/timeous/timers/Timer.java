package io.takano.timeous.timers;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "timers", indices = {@Index(value = "id", unique = true)})
public class Timer {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    protected Integer id;

    @ColumnInfo(name = "group_id")
    private Integer groupId;

    private Integer order;

    private String name;

    @NonNull
    private Integer seconds;

    public Timer(Integer groupId, Integer order, String name, Integer seconds) {
        this.groupId = groupId;
        this.order = order;
        this.name = name;
        this.seconds = seconds;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public Integer getOrder() {
        return order;
    }

    public String getName() {
        return name;
    }

    @NonNull
    public Integer getSeconds() {
        return seconds;
    }
}
