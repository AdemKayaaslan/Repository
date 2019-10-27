package com.ademkayaaslan.todoreminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> nameArray;
    ArrayList<String> noteArray;
    ArrayList<Integer> idArray;
    ArrayAdapter arrayAdapter;
    String nameFromAdd;
    int id;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_alarm,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.add_alarm) {
            Intent intent = new Intent(getApplicationContext(),AddActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);

        Intent intent = getIntent();
        nameFromAdd = intent.getStringExtra("nameFromAdd");
        id = intent.getIntExtra("id",1000);

        nameArray = new ArrayList<>();
        noteArray = new ArrayList<>();
        idArray = new ArrayList<>();



        arrayAdapter= new ArrayAdapter(this, android.R.layout.simple_list_item_1, nameArray);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                intent.putExtra("info", "CreatedNote");
                intent.putExtra("name",nameArray.get(i));
                intent.putExtra("note", noteArray.get(i));
                intent.putExtra("id",idArray.get(i));
                startActivity(intent);
            }
        });

        getData();
    }

    public void getData () {
        SQLiteDatabase database = this.openOrCreateDatabase("noteDatabase", MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS notedatabase (name VARCHAR, note VARCHAR, id INTEGER, date DATE, time TIME)");

        Cursor cursor =database.rawQuery("SELECT * FROM notedatabase", null);

        int nameIx = cursor.getColumnIndex("name");
        int noteIx = cursor.getColumnIndex("note");
        int idIx = cursor.getColumnIndex("id");


        while (cursor.moveToNext()) {
            nameArray.add(cursor.getString(nameIx));
            noteArray.add(cursor.getString(noteIx));
            idArray.add(cursor.getInt(idIx));
            arrayAdapter.notifyDataSetChanged();
        }
        cursor.close();

        if (nameFromAdd != null) {
            try {
                database.execSQL("DELETE  FROM notedatabase WHERE name = ?", new Object[]{nameFromAdd});
                Intent intentP = new Intent(getBaseContext(), AlarmReceiver.class);
                AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intentP, PendingIntent.FLAG_CANCEL_CURRENT);
                alarmMgr.cancel(pendingIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}
