package io.takano.timeous.timerGroups;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface TimerGroupDao {

    @Insert
    long insert(TimerGroup timerGroup);

    @Update
    void update(TimerGroup timerGroup);

    @Delete
    void delete(TimerGroup timerGroup);

    @Query("SELECT * FROM timer_groups")
    LiveData<List<TimerGroup>> getAllTimerGroups();

}
