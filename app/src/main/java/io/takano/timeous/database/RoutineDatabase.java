package io.takano.timeous.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Routine.class}, version = 1)
public abstract class RoutineDatabase extends RoomDatabase {

    private static RoutineDatabase instance;

    public abstract RoutineDao timerGroupDao();

    public static synchronized RoutineDatabase getDatabase(final Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    RoutineDatabase.class, "routines_database")
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
        private final RoutineDao routineDao;

        private PopulateDbAsyncTask(RoutineDatabase db) {
            this.routineDao = db.timerGroupDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            routineDao.insert(new Routine("Routine 1"));
            return null;
        }
    }
}
