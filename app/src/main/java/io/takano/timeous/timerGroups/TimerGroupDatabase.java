package io.takano.timeous.timerGroups;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {TimerGroup.class}, version = 1)
public abstract class TimerGroupDatabase extends RoomDatabase {

    private static TimerGroupDatabase instance;

    public abstract TimerGroupDao timerGroupDao();

    public static synchronized TimerGroupDatabase getDatabase(final Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    TimerGroupDatabase.class, "timer_group_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private final TimerGroupDao timerGroupDao;

        private PopulateDbAsyncTask(TimerGroupDatabase db) {
            this.timerGroupDao = db.timerGroupDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            timerGroupDao.insert(new TimerGroup("Group 1"));
            return null;
        }
    }
}
