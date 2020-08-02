package io.takano.timeous.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

@Dao
public interface RoutineDao {

    @Insert
    long insert(Routine routine);

    @Update
    void update(Routine routine);

    @Delete
    void delete(Routine routine);

    @Transaction
    @Query("SELECT * FROM routines")
    LiveData<List<RoutineWithTimers>> getAllRoutines();

}
