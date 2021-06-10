package com.fro.room_sunalarmcase;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.ListView;
import 	androidx.appcompat.app.AppCompatActivity;
import com.fro.room_sunalarmcase.R;
import com.fro.room_sunalarmcase.DBHelper;

public class ShowStoredData extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_data);

    }
}
