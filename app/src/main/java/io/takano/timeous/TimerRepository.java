package io.takano.timeous;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;
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
public class TimerRepository {

    private TimerDao timerDao;
    private TimerGroupDao timerGroupDao;
    private LiveData<List<Timer>> allTimers;
    private LiveData<List<TimerGroup>> allTimerGroups;

    public TimerRepository(Application application) {
        TimerGroupDatabase timerGroupDatabase = TimerGroupDatabase.getDatabase(application);
        timerGroupDao = timerGroupDatabase.timerGroupDao();
        allTimerGroups = timerGroupDao.getAllTimerGroups();

        TimerDatabase timerDatabase = TimerDatabase.getInstance(application);
        timerDao = timerDatabase.timerDao();
        allTimers = timerDao.getAllTimers();
    }

    public void insertTimerGroup(TimerGroup timerGroup) {
        new InsertTimerGroupAsyncTask(timerGroupDao).execute(timerGroup);
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

    public LiveData<List<Timer>> getTimersInGroup(TimerGroup timerGroup) {
        return timerDao.getTimersInGroup(timerGroup.getId());
    }

    public void deleteTimersInGroup(TimerGroup timerGroup) {
        new DeleteTimersInGroupAsyncTask(timerDao).execute(timerGroup);
    }

    public void insertTimer(Timer timer) {
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
    private static class InsertTimerGroupAsyncTask extends AsyncTask<TimerGroup, Void, Void> {
        private TimerGroupDao timerGroupDao;

        private InsertTimerGroupAsyncTask(TimerGroupDao timerGroupDao) {
            this.timerGroupDao = timerGroupDao;
        }

        @Override
        protected Void doInBackground(TimerGroup... timerGroups) {
            timerGroupDao.insert(timerGroups[0]);
            return null;
        }
    }

    private static class UpdateTimerGroupAsyncTask extends AsyncTask<TimerGroup, Void, Void> {
        private TimerGroupDao timerGroupDao;

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
        private TimerGroupDao timerGroupDao;

        private DeleteTimerGroupAsyncTask(TimerGroupDao timerGroupDao) {
            this.timerGroupDao = timerGroupDao;
        }

        @Override
        protected Void doInBackground(TimerGroup... timerGroups) {
            timerGroupDao.delete(timerGroups[0]);
            return null;
        }
    }

    private static class InsertTimerAsyncTask extends AsyncTask<Timer, Void, Void> {
        private TimerDao timerDao;

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
        private TimerDao timerDao;

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
        private TimerDao timerDao;

        private DeleteTimerAsyncTask(TimerDao timerDao) {
            this.timerDao = timerDao;
        }

        @Override
        protected Void doInBackground(Timer... timers) {
            timerDao.delete(timers[0]);
            return null;
        }
    }

    private static class DeleteTimersInGroupAsyncTask extends AsyncTask<TimerGroup, Void, Void> {
        private TimerDao timerDao;

        private DeleteTimersInGroupAsyncTask(TimerDao timerDao) {
            this.timerDao = timerDao;
        }

        @Override
        protected Void doInBackground(TimerGroup... timerGroups) {
            timerDao.deleteTimersInGroup(timerGroups[0].getId());
            return null;
        }
    }

}
