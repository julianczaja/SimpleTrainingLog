package com.corrot.room.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.corrot.room.repository.ExercisesRepository;
import com.corrot.room.db.entity.Exercise;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ExerciseViewModel extends AndroidViewModel {

    private ExercisesRepository mExercisesRepository;
    private LiveData<List<Exercise>> mAllExercises;

    public ExerciseViewModel(Application application) {
        super(application);
        mExercisesRepository = new ExercisesRepository(application);
        mAllExercises = mExercisesRepository.getAllExercises();
    }

    public LiveData<List<Exercise>> getAllExercises() {
        return mAllExercises;
    }

    public void insertSingleExercise(Exercise exercise) {
        mExercisesRepository.insertSingleExercise(exercise);
    }

    public LiveData<List<Exercise>> getAllExercisesByWorkoutId(long id)
            throws ExecutionException, InterruptedException {
        return mExercisesRepository.getAllExercisesByWorkoutId(id);
    }

    public void updateSingleExercise(Exercise exercise) {
        mExercisesRepository.updateSingleExercise(exercise);
    }

    public void deleteSingleExercise(Exercise exercise) {
        mExercisesRepository.deleteSingleExercise(exercise);
    }
}