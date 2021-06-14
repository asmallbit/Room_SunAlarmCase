package com.fro.room_sunalarmcase;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import 	androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ShowStoredData extends AppCompatActivity{

    ListView mListView;
    DBHelper mDBHelper;
    List<ALineofDB> everyone;
    ArrayAdapter lineAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_data);

        mListView = findViewById(R.id.data_view);

        mDBHelper = new DBHelper(this);

        showDB();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ALineofDB clickedLine = (ALineofDB) adapterView.getItemAtPosition(i);
                mDBHelper.deleteOne(clickedLine);
                showDB();
                Toast.makeText(ShowStoredData.this, "Delete: " + clickedLine.deleteItem(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showDB(){
        try {
            everyone = mDBHelper.getEveryone();
            lineAdapter = new ArrayAdapter<ALineofDB>(this, android.R.layout.simple_list_item_1, everyone);
            mListView.setAdapter(lineAdapter);
        } catch (Exception e){
            Log.d("DATABASE", "The databease is not created or get some error", e);
        }
    }

}
