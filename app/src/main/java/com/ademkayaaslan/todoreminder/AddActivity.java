package com.ademkayaaslan.todoreminder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import static java.lang.Integer.parseInt;

public class AddActivity extends AppCompatActivity {
    EditText nameText;
    EditText noteText;
    SQLiteDatabase database;
    private int mYear, mMonth, mDay, mHour, mMinute;
    TextView dateText;
    TextView timeText;
    Calendar alarmCalender;
    private AlarmManager alarmMgr;
    private PendingIntent pendingIntent;
    Button save;
    Button delete;
    String nameFromAdd;
    Calendar calendar;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        nameText = findViewById(R.id.nameText);
        noteText = findViewById(R.id.noteText);
        dateText = findViewById(R.id.dateText);
        timeText = findViewById(R.id.timeText);
        save = findViewById(R.id.save);
        delete = findViewById(R.id.delete);

        alarmCalender = Calendar.getInstance();
        alarmCalender.setTimeInMillis(System.currentTimeMillis());

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if (info == null) {
            id = (int) System.currentTimeMillis();
        }

        if (info != null && info.matches("CreatedNote")) {
            save.setVisibility(View.INVISIBLE);
            delete.setVisibility(View.VISIBLE);
            nameFromAdd = intent.getStringExtra("name");
            String note = intent.getStringExtra("note");
            nameText.setText(nameFromAdd);
            noteText.setText(note);
            id = intent.getIntExtra("id",1000);
        }
        System.out.println("id:" + id);


    }

    public void date (View view) {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,-1);



        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


                        alarmCalender.set(Calendar.YEAR,year);
                        alarmCalender.set(Calendar.MONTH,monthOfYear);
                        alarmCalender.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        if (alarmCalender.compareTo(calendar) <= 0) {
                            Toast.makeText(AddActivity.this, "Past date selected!", Toast.LENGTH_SHORT).show();
                        } else {
                            dateText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void time (View view) {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        alarmCalender.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        alarmCalender.set(Calendar.MINUTE,minute);

                        if(alarmCalender.compareTo(c) <= 0){

                            alarmCalender.add(Calendar.DATE, 1);
                        }

                        timeText.setText(hourOfDay + ":" + minute);

                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    public void alarm () {
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        alarmMgr.set(AlarmManager.RTC_WAKEUP, alarmCalender.getTimeInMillis(),  pendingIntent);
    }

    public  void save (View view) {
        alarm();

        try {
            String name = nameText.getText().toString();
            String note = noteText.getText().toString();
            database = this.openOrCreateDatabase("noteDatabase", MODE_PRIVATE, null);
                database.execSQL("CREATE TABLE IF NOT EXISTS notedatabase(name VARCHAR, note VARCHAR, id INTEGER)");
/*
            String sqlString = "INSERT INTO notedatabase (name, note, id) VALUES (?,?,?)";
            SQLiteStatement sqLiteStatement =database.compileStatement(sqlString);
            sqLiteStatement.bindString(1,name);
            sqLiteStatement.bindString(2, note);
            sqLiteStatement.execute(); */
            database.execSQL("INSERT INTO notedatabase (name, note, id) VALUES (?,?,?)", new Object[] {name, note, id});
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

    }

    public void delete (View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("nameFromAdd", nameFromAdd);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}
