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

    @ColumnInfo(name = "routine_id")
    private Long routineId;

    private Integer order;

    private String name;

    @NonNull
    private Integer seconds;

    public Timer(Long routineId, Integer order, String name, Integer seconds) {
        this.routineId = routineId;
        this.order = order;
        this.name = name;
        this.seconds = seconds;
    }

    public void setRoutineId(Long routineId) {
        this.routineId = routineId;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSeconds(@NonNull Integer seconds) {
        this.seconds = seconds;
    }

    @NonNull
    public Long getId() {
        return id;
    }

    public Long getRoutineId() {
        return routineId;
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
