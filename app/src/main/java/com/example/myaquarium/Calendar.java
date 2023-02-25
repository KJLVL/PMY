package com.example.myaquarium;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.example.myaquarium.server.Requests;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Calendar extends AppCompatActivity {
    private CalendarView calendarView;
    private List<String> events = new ArrayList<>();
    private Map<String, String> contents = new HashMap<>();
    private Requests requests = new Requests();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        this.setToolbar();

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        this.getEvents();

        try {
            calendarView.setDate(new Date());
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(v -> addNote());

        calendarView.setOnDayClickListener(this::previewNote);

        TextView calculator = findViewById(R.id.service);
        TextView profile = findViewById(R.id.profile);
        TextView forum = findViewById(R.id.forum);

        calculator.setOnClickListener(view -> this.startActivity(new Intent(this, Service.class)));
        forum.setOnClickListener(view -> this.startActivity(new Intent(this, Forum.class)));
        profile.setOnClickListener(view -> this.startActivity(new Intent(this, Profile.class)));
    }

    private void getEvents() {
        Runnable runnable = () -> {
            try {
                JSONArray eventsList = requests.setRequest(requests.urlRequest + "user/getCalendar", new ArrayList<>());
                for (int i = 0; i < eventsList.length(); i++) {
                    JSONObject object = new JSONObject(String.valueOf(eventsList.getJSONObject(i)));
                    events.add(object.getString("date"));
                    contents.put(object.getString("date"), object.getString("content"));
                }
                calendarView.post(this::setEvents);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void setEvents() {
        List<EventDay> allEvents = new ArrayList<>();

        for (String date: events) {
            String[] dateTime = date.split("-");
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.set(
                    Integer.parseInt(dateTime[0]),
                    Integer.parseInt(dateTime[1]) - 1,
                    Integer.parseInt(dateTime[2])
            );
            allEvents.add(new EventDay(calendar, R.drawable.calendar_events));
        }
        calendarView.setEvents(allEvents);
    }

    private void addNote() {
        startActivity(new Intent(this, CalendarAdd.class));
    }

    private void previewNote(EventDay eventDay) {
        Date date = eventDay.getCalendar().getTime();
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        if (contents.containsKey(simpleDateFormat.format(date))) {
            String eventText = contents.get(simpleDateFormat.format(date));

            LayoutInflater inflater = LayoutInflater.from(this);
            View window = inflater.inflate(R.layout.calendar_window, null);
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setView(window);

            EditText event = window.findViewById(R.id.event);
            event.setText(eventText);

            dialog.setNegativeButton("Закрыть", (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            dialog.setPositiveButton("Сохранить", (dialogInterface, i) -> {
                if (!event.getText().toString().equals(eventText)) {
                    this.saveEvent(simpleDateFormat.format(date), event.getText().toString());
                } else {
                    Toast.makeText(
                            this,
                            "Нет изменений для сохранения", Toast.LENGTH_SHORT
                    ).show();
                }
            });
            dialog.show();
        }
    }

    private void saveEvent(String date, String content) {
        List<NameValuePair> params = new ArrayList<>(List.of(
                new BasicNameValuePair("date", date),
                new BasicNameValuePair("content", content)
            )
        );

        Runnable runnable = () -> {
            try {
                JSONArray message = requests.setRequest(requests.urlRequest + "user/updateCalendar", params);
                JSONObject object = new JSONObject(String.valueOf(message.getJSONObject(0)));
                if (object.optString("success").equals("1")) {
                    this.runOnUiThread(() -> {
                        Toast.makeText(
                                getApplicationContext(),
                                "Заметка была успешно обновлена", Toast.LENGTH_SHORT
                        ).show();
                        this.getEvents();
                    });
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView textView = findViewById(R.id.title);
        textView.setText(getApplicationContext().getString(R.string.calendar_text));

        toolbar.setNavigationOnClickListener(view -> {
            this.startActivity(new Intent(this, Service.class));
        });
    }
}