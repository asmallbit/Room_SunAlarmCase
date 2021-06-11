package com.fro.room_sunalarmcase;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import 	androidx.appcompat.app.AppCompatActivity;
import com.fro.room_sunalarmcase.R;
import com.fro.room_sunalarmcase.DBHelper;

import java.lang.reflect.Array;
import java.util.List;

public class ShowStoredData extends AppCompatActivity{

    ListView mListView;
    DBHelper mDBHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_data);

        mListView = findViewById(R.id.data_view);

        mDBHelper = new DBHelper(this);
        try {
            List<ALineofDB> everyone = mDBHelper.getEveryone();

            ArrayAdapter lineAdapter = new ArrayAdapter<ALineofDB>(this, android.R.layout.simple_list_item_1, everyone);
            mListView.setAdapter(lineAdapter);
        } catch (Exception e){
            Log.d("DATABASE", "The databease is not created or get some error", e);
        }


    }
}
