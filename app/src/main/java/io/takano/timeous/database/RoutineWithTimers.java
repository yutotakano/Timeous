package io.takano.timeous.database;

import java.io.Serializable;
import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

/**
 * Simple POJO class that will be embedded in table:routines if the one-to-many query is called
 */
public class RoutineWithTimers implements Serializable {
    @Embedded
    public Routine routine;

    @Relation(
            parentColumn = "id",
            entityColumn = "routine_id"
    )
    public List<Timer> timers;
}
