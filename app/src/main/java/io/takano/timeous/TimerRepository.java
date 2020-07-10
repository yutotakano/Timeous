package io.takano.timeous;

import android.app.Application;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.takano.timeous.timerGroups.TimerGroup;
import io.takano.timeous.timerGroups.TimerGroupDao;
import io.takano.timeous.timerGroups.TimerGroupDatabase;
import io.takano.timeous.timers.Timer;
import io.takano.timeous.timers.TimerDao;
import io.takano.timeous.timers.TimerDatabase;

/**
 * The role of this class is to provide a layer of abstraction for the database operations.
 * AsyncTasks are defined in this class.
 */
@SuppressWarnings("CanBeFinal")
public class TimerRepository {

    private final TimerDao timerDao;
    private final TimerGroupDao timerGroupDao;
    private LiveData<List<Timer>> allTimers;
    private LiveData<List<TimerGroup>> allTimerGroups;
    private MutableLiveData<Long> insertTimerGroupId = new MutableLiveData<>();

    public TimerRepository(Application application) {
        TimerGroupDatabase timerGroupDatabase = TimerGroupDatabase.getDatabase(application);
        timerGroupDao = timerGroupDatabase.timerGroupDao();
        allTimerGroups = timerGroupDao.getAllTimerGroups();

        TimerDatabase timerDatabase = TimerDatabase.getInstance(application);
        timerDao = timerDatabase.timerDao();
        allTimers = timerDao.getAllTimers();
    }

    public LiveData<Long> insertTimerGroup(TimerGroup timerGroup) {
        // return null and asynchronously set livedata to the id
        insertTimerGroupId.setValue(null);
        new InsertTimerGroupAsyncTask(timerGroupDao, this).execute(timerGroup);
        return insertTimerGroupId;
    }

    public void updateTimerGroup(TimerGroup timerGroup) {
        new UpdateTimerGroupAsyncTask(timerGroupDao).execute(timerGroup);
    }

    public void deleteTimerGroup(TimerGroup timerGroup) {
        new DeleteTimerGroupAsyncTask(timerGroupDao).execute(timerGroup);
    }

    public LiveData<List<TimerGroup>> getAllTimerGroups() {
        return allTimerGroups;
    }

    public LiveData<List<Timer>> getTimersInGroup(Long timerGroupId) {
        return timerDao.getTimersInGroup(timerGroupId);
    }

    public void deleteTimersInGroup(Long timerGroupId) {
        new DeleteTimersInGroupAsyncTask(timerDao).execute(timerGroupId);
    }

    public void insertTimer(Timer timer, Long timerGroupId) {
        timer.setGroupId(timerGroupId);
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
    private static class InsertTimerGroupAsyncTask extends AsyncTask<TimerGroup, Void, Long> {
        private final WeakReference<TimerRepository> timerRepositoryWeakReference;

        private final TimerGroupDao timerGroupDao;

        private InsertTimerGroupAsyncTask(TimerGroupDao timerGroupDao, TimerRepository repo) {
            this.timerRepositoryWeakReference = new WeakReference<>(repo);
            this.timerGroupDao = timerGroupDao;
        }

        @Override
        protected Long doInBackground(TimerGroup... timerGroups) {
            return timerGroupDao.insert(timerGroups[0]);
        }

        @Override
        protected void onPostExecute(Long id) {
            TimerRepository repo = timerRepositoryWeakReference.get();
            if (repo == null) return;
            repo.insertTimerGroupId.setValue(id);
        }
    }

    private static class UpdateTimerGroupAsyncTask extends AsyncTask<TimerGroup, Void, Void> {
        private final TimerGroupDao timerGroupDao;

        private UpdateTimerGroupAsyncTask(TimerGroupDao timerGroupDao) {
            this.timerGroupDao = timerGroupDao;
        }

        @Override
        protected Void doInBackground(TimerGroup... timerGroups) {
            timerGroupDao.update(timerGroups[0]);
            return null;
        }
    }

    private static class DeleteTimerGroupAsyncTask extends AsyncTask<TimerGroup, Void, Void> {
        private final TimerGroupDao timerGroupDao;

        private DeleteTimerGroupAsyncTask(TimerGroupDao timerGroupDao) {
            this.timerGroupDao = timerGroupDao;
        }

        @Override
        protected Void doInBackground(TimerGroup... timerGroups) {
            timerGroupDao.delete(timerGroups[0]);
            return null;
        }
    }

    private static class DeleteTimersInGroupAsyncTask extends AsyncTask<Long, Void, Void> {
        private final TimerDao timerDao;

        private DeleteTimersInGroupAsyncTask(TimerDao timerDao) {
            this.timerDao = timerDao;
        }

        @Override
        protected Void doInBackground(Long... timerGroupIds) {
            timerDao.deleteTimersInGroup(timerGroupIds[0]);
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
