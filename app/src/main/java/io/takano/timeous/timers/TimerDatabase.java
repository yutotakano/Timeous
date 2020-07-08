package io.takano.timeous.timers;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Timer.class}, version = 1)
public abstract class TimerDatabase extends RoomDatabase {

    private static TimerDatabase instance; // We need to use the same instance everywhere

    public abstract TimerDao timerDao();

    // synchronized means only one thread can access, thus no two instances are made
    public static synchronized TimerDatabase getInstance(Context context) {
        if (instance == null) {
            // new cannot be used, so we use a builder
            // fallbackToDestructiveMigration means if version number is increased, we delete the DB
            // and recreate it
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    TimerDatabase.class, "timer_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        // control + o
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private TimerDao timerDao;

        private PopulateDbAsyncTask(TimerDatabase db) {
            // this is possible because onCreate is called after database is created with Dao
            this.timerDao = db.timerDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            timerDao.insert(new Timer(1, 1, "test", 450));
            timerDao.insert(new Timer(1, 2, "test a", 150));
            timerDao.insert(new Timer(1, 3, "test b", 60));
            return null;
        }
    }
}
