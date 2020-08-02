package io.takano.timeous;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import io.takano.timeous.database.Routine;
import io.takano.timeous.database.Timer;

// A context is needed for the repository and its databases, but if we use Activity or View contexts,
// they will get destroyed when configuration changes occur (i.e. screen rotation)
// so, we use AndroidViewModel (not ViewModel) to get the unchanging Application context
public class DataViewModel extends AndroidViewModel {

    private final DataRepository dataRepository;
    private final LiveData<List<Routine>> allRoutines;
    private final LiveData<List<Timer>> allTimers;

    public DataViewModel(@NonNull Application application) {
        super(application);
        dataRepository = new DataRepository(application);
        allRoutines = dataRepository.getAllRoutines();
        allTimers = dataRepository.getAllTimers();
    }

    // UI controller doesn't get access to repository, instead a wrapper API
    public LiveData<Long> insertRoutine(Routine routine) {
        return dataRepository.insertRoutine(routine);
    }

    public void updateRoutine(Routine routine) {
        dataRepository.updateRoutine(routine);
    }

    public void deleteRoutine(Routine routine) {
        dataRepository.deleteRoutine(routine);
    }

    public LiveData<List<Routine>> getAllRoutines() {
        return allRoutines;
    }

    public LiveData<List<Timer>> getTimersInRoutine(Long routineId) {
        return dataRepository.getTimersInRoutine(routineId);
    }

    public void deleteTimersInRoutine(Long routineId) {
        dataRepository.deleteTimersInRoutine(routineId);
    }

    public void insertTimer(Timer timer, Long timerGroupId) {
        dataRepository.insertTimer(timer, timerGroupId);
    }

    public void updateTimer(Timer timer) {
        dataRepository.updateTimer(timer);
    }

    public void deleteTimer(Timer timer) {
        dataRepository.deleteTimer(timer);
    }

    public LiveData<List<Timer>> getAllTimers() {
        return allTimers;
    }
}
