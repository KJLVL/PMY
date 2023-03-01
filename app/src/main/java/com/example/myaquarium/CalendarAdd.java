package com.example.myaquarium;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.example.myaquarium.server.Requests;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CalendarAdd extends AppCompatActivity {
    private CalendarView calendar;
    private List<String> events = new ArrayList<>();
    private Requests requests = new Requests();
    private SimpleDateFormat simpleDateFormat;

    private Button save;
    private EditText event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_add);

        this.setToolbar();

        calendar = findViewById(R.id.calendar);
        try {
            calendar.setDate(new Date());
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }
        this.getEvents();
        String pattern = "yyyy-MM-dd";
        simpleDateFormat = new SimpleDateFormat(pattern);

        save = findViewById(R.id.save);
        event = findViewById(R.id.noteEditText);

        calendar.setOnDayClickListener(this::checkDate);

        save.setOnClickListener(v -> {
            if (!event.getText().toString().equals("")) {
                saveEvent(event.getText().toString());
            } else {
                Toast.makeText(
                        this,
                        "Введите заметку", Toast.LENGTH_SHORT
                ).show();
            }
        });

        TextView calculator = findViewById(R.id.service);
        TextView profile = findViewById(R.id.profile);
        TextView forum = findViewById(R.id.forum);

        calculator.setOnClickListener(view -> this.startActivity(new Intent(this, Service.class)));
        forum.setOnClickListener(view -> this.startActivity(new Intent(this, Forum.class)));
        profile.setOnClickListener(view -> this.startActivity(new Intent(this, Profile.class)));
    }

    private void saveEvent(String content) {

        List<NameValuePair> params = new ArrayList<>(List.of(
                new BasicNameValuePair("date", simpleDateFormat.format(calendar.getFirstSelectedDate().getTime())),
                new BasicNameValuePair("content", content)
            )
        );

        Runnable runnable = () -> {
            try {
                JSONArray message = requests.setRequest(requests.urlRequest + "user/saveCalendar", params);
                JSONObject object = new JSONObject(String.valueOf(message.getJSONObject(0)));
                if (object.optString("success").equals("1")) {
                    this.runOnUiThread(() -> {
                        Toast.makeText(
                                getApplicationContext(),
                                "Заметка была успешно добавлена", Toast.LENGTH_SHORT
                        ).show();
                        startActivity(new Intent(this, Calendar.class));
                    });
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void checkDate(EventDay eventDay) {
        Date date = eventDay.getCalendar().getTime();

        if (events.contains(simpleDateFormat.format(date))) {
            save.setEnabled(false);
            event.setEnabled(false);
            Toast.makeText(
                    this,
                    "Заметка на выбранный день существует", Toast.LENGTH_SHORT
            ).show();
        } else {
            save.setEnabled(true);
            event.setEnabled(true);
        }
    }

    private void getEvents() {
        Runnable runnable = () -> {
            try {
                JSONArray eventsList = requests.setRequest(requests.urlRequest + "user/getCalendar", new ArrayList<>());
                for (int i = 0; i < eventsList.length(); i++) {
                    JSONObject object = new JSONObject(String.valueOf(eventsList.getJSONObject(i)));
                    events.add(object.getString("date"));
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
        textView.setText(getApplicationContext().getString(R.string.service_calendar_add_text));

        toolbar.setNavigationOnClickListener(view -> {
            this.startActivity(new Intent(this, Calendar.class));
        });
    }
}