package com.example.myaquarium;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.example.myaquarium.service.Navigation;
import com.example.myaquarium.service.Requests;
import com.example.myaquarium.service.UserData;

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

public class CalendarAdd extends AppCompatActivity {
    private CalendarView calendar;
    private final List<String> events = new ArrayList<>();
    private final Requests requests = new Requests();
    private SimpleDateFormat simpleDateFormat;

    private Button save;
    private EditText event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_add);
        Navigation.setToolbar(
                this,
                getApplicationContext().getString(R.string.service_calendar_add_text),
                Calendar.class
        );
        Navigation.setMenuNavigation(this);

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
    }

    private void saveEvent(String content) {

        List<NameValuePair> params = new ArrayList<>(List.of(
                new BasicNameValuePair("date", simpleDateFormat.format(calendar.getFirstSelectedDate().getTime())),
                new BasicNameValuePair("content", content),
                new BasicNameValuePair("id", UserData.getUserData(this))
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
        List<NameValuePair> params = new ArrayList<>(List.of(
                new BasicNameValuePair("id", UserData.getUserData(this))
            )
        );
        Runnable runnable = () -> {
            try {
                JSONArray eventsList = requests.setRequest(requests.urlRequest + "user/getCalendar", params);
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

}