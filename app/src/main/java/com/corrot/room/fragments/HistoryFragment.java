package com.corrot.room.fragments;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.corrot.room.CalendarCallback;
import com.corrot.room.R;
import com.corrot.room.adapters.WorkoutsListAdapter;
import com.corrot.room.db.entity.Workout;
import com.corrot.room.utils.EventDecorator;
import com.corrot.room.viewmodel.WorkoutViewModel;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryFragment extends Fragment implements OnDateSelectedListener {

    private WorkoutViewModel mWorkoutViewModel;
    private MaterialCalendarView mCalendarView;
    private WorkoutsListAdapter mWorkoutListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_history, container, false);
        mWorkoutViewModel = ViewModelProviders.of(this).get(WorkoutViewModel.class);

        final RecyclerView recyclerView = view.findViewById(R.id.fragment_history_recycler_view);
        mCalendarView = view.findViewById(R.id.fragment_history_calendar_view);
        mCalendarView.setOnDateChangedListener(this);
        mCalendarView.setDateSelected(CalendarDay.today(), true);

        mWorkoutListAdapter = new WorkoutsListAdapter(this.getContext(), this.getActivity());

        recyclerView.setAdapter(mWorkoutListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setItemAnimator(null);

        mWorkoutViewModel.getAllWorkouts().observe(this, this::setCalendarEvents);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                Workout w = mWorkoutListAdapter.getWorkoutAt(viewHolder.getAdapterPosition());
                mWorkoutViewModel.deleteWorkout(w);
                Toast.makeText(view.getContext(),
                        "Workout deleted!", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);
        return view;
    }

    private void setCalendarEvents(List<Workout> workouts) {
        // TODO: Optimize it to get only part of all workouts (Maybe only workouts from this month?)
        mCalendarView.removeDecorators();
        new setCalendarEventsAsync(workouts, days -> {
            EventDecorator decorator = new EventDecorator(ContextCompat.getColor(getContext(),
                R.color.colorAccent), days);
            mCalendarView.addDecorator(decorator);
            mCalendarView.invalidateDecorators();
        }).execute();
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget,
                               @NonNull CalendarDay date, boolean selected) {

        Date d = new Date(date.getDate().atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000);

        mWorkoutViewModel.getWorkoutsByDate(d, workouts ->
                mWorkoutListAdapter.setWorkouts(workouts));
    }


    private static class setCalendarEventsAsync extends AsyncTask<Void, Void, List<CalendarDay>> {

        private final List<Workout> workouts;
        private final CalendarCallback callback;

        setCalendarEventsAsync(List<Workout> workouts, CalendarCallback callback) {
            this.workouts = workouts;
            this.callback = callback;
        }

        @Override
        protected List<CalendarDay> doInBackground(Void... voids) {
            List<CalendarDay> days = new ArrayList<>();
            for (Workout w : workouts) {
                LocalDate date = Instant.ofEpochMilli(w.workoutDate.getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                days.add(CalendarDay.from(date));
            }
            return days;
        }

        @Override
        protected void onPostExecute(List<CalendarDay> calendarDays) {
            callback.onSuccess(calendarDays);
        }
    }
}
