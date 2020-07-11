package io.takano.timeous;

import android.app.Application;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.takano.timeous.routines.Routine;
import io.takano.timeous.routines.RoutineDao;
import io.takano.timeous.routines.RoutineDatabase;
import io.takano.timeous.timers.Timer;
import io.takano.timeous.timers.TimerDao;
import io.takano.timeous.timers.TimerDatabase;

/**
 * The role of this class is to provide a layer of abstraction for the database operations.
 * AsyncTasks are defined in this class.
 */
@SuppressWarnings("CanBeFinal")
public class DataRepository {

    private final TimerDao timerDao;
    private final RoutineDao routineDao;
    private LiveData<List<Timer>> allTimers;
    private LiveData<List<Routine>> allRoutines;
    private MutableLiveData<Long> insertRoutineResultId = new MutableLiveData<>();

    public DataRepository(Application application) {
        RoutineDatabase routineDatabase = RoutineDatabase.getDatabase(application);
        routineDao = routineDatabase.timerGroupDao();
        allRoutines = routineDao.getAllRoutines();

        TimerDatabase timerDatabase = TimerDatabase.getInstance(application);
        timerDao = timerDatabase.timerDao();
        allTimers = timerDao.getAllTimers();
    }

    public LiveData<Long> insertRoutine(Routine routine) {
        // return null and asynchronously set LiveData to the id
        insertRoutineResultId.setValue(null);
        new InsertRoutineAsyncTask(routineDao, this).execute(routine);
        return insertRoutineResultId;
    }

    public void updateRoutine(Routine routine) {
        new UpdateRoutineAsyncTask(routineDao).execute(routine);
    }

    public void deleteRoutine(Routine routine) {
        new DeleteRoutineAsyncTask(routineDao).execute(routine);
    }

    public LiveData<List<Routine>> getAllRoutines() {
        return allRoutines;
    }

    public LiveData<List<Timer>> getTimersInRoutine(Long routineId) {
        return timerDao.getTimersInRoutine(routineId);
    }

    public void deleteTimersInRoutine(Long routineId) {
        new DeleteTimersInRoutineAsyncTask(timerDao).execute(routineId);
    }

    public void insertTimer(Timer timer, Long routineId) {
        timer.setRoutineId(routineId);
        new InsertTimerAsyncTask(timerDao).execute(timer);
    }

    public void updateTimer(Timer timer) {
        new UpdateTimerAsyncTask(timerDao).execute(timer);
    }

    public void deleteTimer(Timer timer) {
        new DeleteTimerAsyncTask(timerDao).execute(timer);
    }

    public LiveData<List<Timer>> getAllTimers() {
        return allTimers;
    }

    // static because it shouldn't have reference to the Repository
    // if not, memory leak could occur
    private static class InsertRoutineAsyncTask extends AsyncTask<Routine, Void, Long> {
        private final WeakReference<DataRepository> timerRepositoryWeakReference;

        private final RoutineDao routineDao;

        private InsertRoutineAsyncTask(RoutineDao routineDao, DataRepository repo) {
            this.timerRepositoryWeakReference = new WeakReference<>(repo);
            this.routineDao = routineDao;
        }

        @Override
        protected Long doInBackground(Routine... routines) {
            return routineDao.insert(routines[0]);
        }

        @Override
        protected void onPostExecute(Long id) {
            DataRepository repo = timerRepositoryWeakReference.get();
            if (repo == null) return;
            repo.insertRoutineResultId.setValue(id);
        }
    }

    private static class UpdateRoutineAsyncTask extends AsyncTask<Routine, Void, Void> {
        private final RoutineDao routineDao;

        private UpdateRoutineAsyncTask(RoutineDao routineDao) {
            this.routineDao = routineDao;
        }

        @Override
        protected Void doInBackground(Routine... routines) {
            routineDao.update(routines[0]);
            return null;
        }
    }

    private static class DeleteRoutineAsyncTask extends AsyncTask<Routine, Void, Void> {
        private final RoutineDao routineDao;

        private DeleteRoutineAsyncTask(RoutineDao routineDao) {
            this.routineDao = routineDao;
        }

        @Override
        protected Void doInBackground(Routine... routines) {
            routineDao.delete(routines[0]);
            return null;
        }
    }

    private static class DeleteTimersInRoutineAsyncTask extends AsyncTask<Long, Void, Void> {
        private final TimerDao timerDao;

        private DeleteTimersInRoutineAsyncTask(TimerDao timerDao) {
            this.timerDao = timerDao;
        }

        @Override
        protected Void doInBackground(Long... routineIds) {
            timerDao.deleteTimersInRoutine(routineIds[0]);
            return null;
        }
    }

    private static class InsertTimerAsyncTask extends AsyncTask<Timer, Void, Void> {
        private final TimerDao timerDao;

        private InsertTimerAsyncTask(TimerDao timerDao) {
            this.timerDao = timerDao;
        }

        @Override
        protected Void doInBackground(Timer... timers) {
            timerDao.insert(timers[0]);
            return null;
        }
    }

    private static class UpdateTimerAsyncTask extends AsyncTask<Timer, Void, Void> {
        private final TimerDao timerDao;

        private UpdateTimerAsyncTask(TimerDao timerDao) {
            this.timerDao = timerDao;
        }

        @Override
        protected Void doInBackground(Timer... timers) {
            timerDao.update(timers[0]);
            return null;
        }
    }

    private static class DeleteTimerAsyncTask extends AsyncTask<Timer, Void, Void> {
        private final TimerDao timerDao;

        private DeleteTimerAsyncTask(TimerDao timerDao) {
            this.timerDao = timerDao;
        }

        @Override
        protected Void doInBackground(Timer... timers) {
            timerDao.delete(timers[0]);
            return null;
        }
    }

}
