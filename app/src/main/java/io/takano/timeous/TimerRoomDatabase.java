package io.takano.timeous;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Timer.class}, version = 1)
public abstract class TimerRoomDatabase extends RoomDatabase {

    public abstract TimerDao timerDao();

    private static volatile TimerRoomDatabase timerRoomInstance;

    static TimerRoomDatabase getDatabase(final Context context) {
        if (timerRoomInstance == null) {
            synchronized (TimerRoomDatabase.class) {
                if (timerRoomInstance == null) {
                    timerRoomInstance = Room.databaseBuilder(context.getApplicationContext(), TimerRoomDatabase.class, "timer_database").build();
                }
            }
        }
        return timerRoomInstance;
    }
}
