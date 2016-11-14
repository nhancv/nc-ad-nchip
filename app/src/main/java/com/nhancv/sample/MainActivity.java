package com.nhancv.sample;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.nhancv.nchip.NChip;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    NChip chip;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NChip.MAX_CHARACTER_COUNT = 20;
        chip = (NChip) findViewById(R.id.chipText);

        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: " + chip.getText().toString());
            }
        });
        chip.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(TAG, "onItemClick: " + i);
            }
        });
        chip.addLayoutTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.e(TAG, "afterTextChanged: " + editable.toString());

            }
        });
        chip.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.e(TAG, "onFocusChange: " + String.valueOf(b));
            }
        });
        chip.setOnChipItemChangeListener(new NChip.ChipItemChangeListener() {
            @Override
            public void onChipAdded(int pos, String txt) {
                Log.d(txt, String.valueOf(pos));

            }

            @Override
            public void onChipRemoved(int pos, String txt) {
                Log.d(txt, String.valueOf(pos));
            }
        });


        String[] countries = {"india", "australia", "austria", "indonesia", "canada"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countries);
        chip.setAdapter(adapter);
    }

    public void click(View v) {
        Log.e(TAG, "click: " + chip.getText().toString());

    }
}
