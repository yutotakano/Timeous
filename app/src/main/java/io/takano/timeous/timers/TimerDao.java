package io.takano.timeous.timers;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface TimerDao {

    @Insert
    void insert(Timer timer);

    @Update
    void update(Timer timer);

    @Delete
    void delete(Timer timer);

    @Query("DELETE FROM timers WHERE routine_id=:routineId")
    void deleteTimersInRoutine(Long routineId);

    @Query("SELECT * FROM timers WHERE routine_id=:routineId")
    LiveData<List<Timer>> getTimersInRoutine(Long routineId);

    @Query("SELECT * FROM timers")
    LiveData<List<Timer>> getAllTimers();

}
