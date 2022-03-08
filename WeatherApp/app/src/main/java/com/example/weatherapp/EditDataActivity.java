package com.example.weatherapp;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
public class EditDataActivity extends AppCompatActivity {
    private static final String TAG = "EditDataActivity";
    private Button btnDelete;
    private TextView editable_item;
    WeatherDatabaseHelper mDatabaseHelper;
    private String selectedName;
    private int selectedID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_data_layout);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        editable_item = (TextView) findViewById(R.id.editable_item);
        mDatabaseHelper = new WeatherDatabaseHelper(this);
        Intent receivedIntent = getIntent();
        selectedID = receivedIntent.getIntExtra("id",-1);
        selectedName = receivedIntent.getStringExtra("name");
        editable_item.setText(selectedName);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseHelper.deleteName(selectedID,selectedName);
                editable_item.setText("");
                //toastMessage("removed from database");
            }}); }}
  //  private void toastMessage(String message){
       // Toast.makeText(this,message,Toast.LENGTH_SHORT).show(); }}
