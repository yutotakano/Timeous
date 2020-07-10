package io.takano.timeous;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import io.takano.timeous.timerGroups.TimerGroup;
import io.takano.timeous.timers.Timer;

// A context is needed for the repository and its databases, but if we use Activity or View contexts,
// they will get destroyed when configuration changes occur (i.e. screen rotation)
// so, we use AndroidViewModel (not ViewModel) to get the unchanging Application context
public class TimerViewModel extends AndroidViewModel {

    private final TimerRepository timerRepository;
    private final LiveData<List<TimerGroup>> allTimerGroups;
    private final LiveData<List<Timer>> allTimers;

    public TimerViewModel(@NonNull Application application) {
        super(application);
        timerRepository = new TimerRepository(application);
        allTimerGroups = timerRepository.getAllTimerGroups();
        allTimers = timerRepository.getAllTimers();
    }

    // UI controller doesn't get access to repository, instead a wrapper API
    public LiveData<Long> insertTimerGroup(TimerGroup timerGroup) {
        return timerRepository.insertTimerGroup(timerGroup);
    }

    public void updateTimerGroup(TimerGroup timerGroup) {
        timerRepository.updateTimerGroup(timerGroup);
    }

    public void deleteTimerGroup(TimerGroup timerGroup) {
        timerRepository.deleteTimerGroup(timerGroup);
    }

    public LiveData<List<TimerGroup>> getAllTimerGroups() {
        return allTimerGroups;
    }

    public LiveData<List<Timer>> getTimersInGroup(Long timerGroupId) {
        return timerRepository.getTimersInGroup(timerGroupId);
    }

    public void deleteTimersInGroup(Long timerGroupId) {
        timerRepository.deleteTimersInGroup(timerGroupId);
    }

    public void insertTimer(Timer timer, Long timerGroupId) {
        timerRepository.insertTimer(timer, timerGroupId);
    }

    public void updateTimer(Timer timer) {
        timerRepository.updateTimer(timer);
    }

    public void deleteTimer(Timer timer) {
        timerRepository.deleteTimer(timer);
    }

    public LiveData<List<Timer>> getAllTimers() {
        return allTimers;
    }
}
