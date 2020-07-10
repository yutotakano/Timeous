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
    protected Long id;

    @ColumnInfo(name = "group_id")
    private Long groupId;

    private Integer order;

    private String name;

    @NonNull
    private Integer seconds;

    public Timer(Long groupId, Integer order, String name, Integer seconds) {
        this.groupId = groupId;
        this.order = order;
        this.name = name;
        this.seconds = seconds;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @NonNull
    public Long getId() {
        return id;
    }

    public Long getGroupId() {
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
