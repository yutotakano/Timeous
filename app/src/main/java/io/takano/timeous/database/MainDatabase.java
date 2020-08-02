package io.takano.timeous.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Routine.class, Timer.class}, version = 1)
public abstract class MainDatabase extends RoomDatabase {

    private static MainDatabase instance; // We need to use the same instance everywhere

    public abstract RoutineDao routineDao();

    public abstract TimerDao timerDao();

    // synchronized means only one thread can access, thus no two instances are made
    public static synchronized MainDatabase getInstance(Context context) {
        if (instance == null) {
            // new cannot be used, so we use a builder
            // fallbackToDestructiveMigration means if version number is increased, we delete the DB
            // and recreate it
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    MainDatabase.class, "main_db")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        // control + o
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private final RoutineDao routineDao;
        private final TimerDao timerDao;

        private PopulateDbAsyncTask(MainDatabase db) {
            // this is possible because onCreate is called after database is created with Dao
            this.routineDao = db.routineDao();
            this.timerDao = db.timerDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            long id = routineDao.insert(new Routine("Example Routine"));
            timerDao.insert(new Timer(id, 1, "Timer 1", 10));
            timerDao.insert(new Timer(id, 2, "Timer 2", 30));
            timerDao.insert(new Timer(id, 3, "Timer 3", 60));
            return null;
        }
    }
}
