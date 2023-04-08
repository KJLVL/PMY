package com.example.myaquarium;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.example.myaquarium.service.Navigation;
import com.example.myaquarium.service.Requests;
import com.example.myaquarium.service.UserData;
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

public class Calendar extends AppCompatActivity {
    private CalendarView calendarView;

    private final Map<String, String> contents = new HashMap<>();
    private final Requests requests = new Requests();
    private List<String> events = new ArrayList<>();
    private List<NameValuePair> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Navigation.setToolbar(
                this,
                getApplicationContext().getString(R.string.service_text),
                Service.class
        );
        Navigation.setMenuNavigation(this);

        this.params = new ArrayList<>(List.of(
                new BasicNameValuePair("id", UserData.getUserData(this))
            )
        );

        calendarView = findViewById(R.id.calendarView);
        this.getEvents();

        try {
            calendarView.setDate(new Date());
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(v -> addNote());

        calendarView.setOnDayClickListener(this::previewNote);
    }

    private void getEvents() {
        events = new ArrayList<>();
        Runnable runnable = () -> {
            try {
                JSONArray eventsList = requests.setRequest(requests.urlRequest + "user/getCalendar", this.params);
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
            window.setBackgroundColor(getResources().getColor(R.color.ripple));
            AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
            dialog.setView(window);

            EditText event = window.findViewById(R.id.event);
            event.setText(eventText);

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
            dialog.setNegativeButton("Удалить", (dialogInterface, i) ->
                    this.deleteEvent(simpleDateFormat.format(date)));
            dialog.show();
        }
    }

    private void saveEvent(String date, String content) {
        List<NameValuePair> params = new ArrayList<>(List.of(
                new BasicNameValuePair("date", date),
                new BasicNameValuePair("content", content),
                new BasicNameValuePair("id", UserData.getUserData(this))
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

    private void deleteEvent(String date) {
        List<NameValuePair> params = new ArrayList<>(List.of(
                new BasicNameValuePair("date", date),
                new BasicNameValuePair("id", UserData.getUserData(this))
            )
        );

        Runnable runnable = () -> {
            try {
                JSONArray message = requests.setRequest(requests.urlRequest + "user/deleteCalendar", params);
                JSONObject object = new JSONObject(String.valueOf(message.getJSONObject(0)));
                if (object.optString("success").equals("1")) {
                    this.runOnUiThread(() -> {
                        Toast.makeText(
                                getApplicationContext(),
                                "Заметка была успешно удалена", Toast.LENGTH_SHORT
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
}