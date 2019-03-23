package com.corrot.room.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.corrot.room.db.dao.ExerciseDAO;
import com.corrot.room.db.dao.WorkoutDAO;
import com.corrot.room.db.entity.Exercise;
import com.corrot.room.db.entity.Workout;

@Database(entities =  {Workout.class, Exercise.class},
          version = 1,
          exportSchema = false)
public abstract class WorkoutsDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "db_workouts";

    public abstract WorkoutDAO workoutDAO();
    public abstract ExerciseDAO exerciseDAO();

    private static WorkoutsDatabase INSTANCE;

    public static WorkoutsDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (WorkoutsDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        WorkoutsDatabase.class, DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}