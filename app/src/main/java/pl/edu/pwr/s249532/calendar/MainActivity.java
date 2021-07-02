package pl.edu.pwr.s249532.calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabaseEvents dbHandler;
    private EditText editText;
    private CalendarView calendarView;
    private String dateSelected;
    private SQLiteDatabase dbSQLite;
    private ListView listView;
    private SimpleCursorAdapter simpleCursorAdapter;
    private Cursor cursorMain;

    private String chosenEventToDelete;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editTextEvent);
        calendarView = findViewById(R.id.calendarView);
        listView =  findViewById(R.id.listView1);

        dateSelected= Integer.toString(Calendar.getInstance().get(Calendar.YEAR)) +
                Integer.toString(Calendar.getInstance().get(Calendar.MONTH)) +
                Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                dateSelected= Integer.toString(year) + Integer.toString(month) + Integer.toString(dayOfMonth);
                ReadDatabaseToListView();
            }
        });

        try {
            dbHandler = new SQLiteDatabaseEvents(this, "CalendarDB", null, 1);
            dbSQLite = dbHandler.getWritableDatabase();
            dbSQLite.execSQL("CREATE TABLE EventCalendar(_id INTEGER PRIMARY KEY, Date TEXT, Event TEXT);");
        }
        catch (Exception e){
            e.printStackTrace();
        }


        cursorMain = getAllRows();

        String[] colToGetData = new String[]{"Event"};

        int[] itemViews_to_place_data = new int[] {R.id.eventRow};


        simpleCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.list_view_rows, cursorMain, colToGetData,
                itemViews_to_place_data, 0);

        listView.setAdapter(simpleCursorAdapter);


        }

    public void InsertIntoDatabase(View view){
        ContentValues contentValues = new ContentValues();
        if(!editText.getText().toString().equals("")) {

            contentValues.put("Date", dateSelected);
            contentValues.put("Event", editText.getText().toString());
            dbSQLite.insert("EventCalendar", null, contentValues);
            editText.setText("");
            ReadDatabaseToListView();
        }
        else{
            Toast annotation = Toast.makeText(getApplicationContext(),"Event can't be empty!",Toast.LENGTH_SHORT);
            annotation.show();
        }
    }


    public Cursor getAllRows(){
        String query = "SELECT * FROM EventCalendar WHERE Date ="+ dateSelected;
        return dbSQLite.rawQuery(query, null);

    }

    public void ReadDatabaseToListView(){

        listView.setAdapter(null);

        cursorMain = getAllRows();

        String[] colToGetData = new String[]{"Event"};

        int[] itemViews_to_place_data = new int[] {R.id.eventRow};


        simpleCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.list_view_rows, cursorMain, colToGetData,
                itemViews_to_place_data, 0);

        listView.setAdapter(simpleCursorAdapter);
    }

    public void ButtonDeleteEvent(View view){
        LinearLayout lvParentRow = (LinearLayout)view.getParent();

        TextView child = (TextView)lvParentRow.getChildAt(1);

        chosenEventToDelete = child.getText().toString();

        RemoveEventFromDatabase(chosenEventToDelete);


    }

    public void RemoveEventFromDatabase(String textEventToDelete){

        try {
            //dbSQLite.delete("EventCalendar","Event = Rower", null);
            dbSQLite.execSQL("DELETE FROM EventCalendar WHERE Event = '"+ textEventToDelete +"' AND Date = '" + dateSelected +"';");
            Toast infoOnDel = Toast.makeText(getApplicationContext(),"Event " + chosenEventToDelete +" is removed!",Toast.LENGTH_SHORT);
            infoOnDel.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        ReadDatabaseToListView();
    }
}