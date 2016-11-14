package com.nhancv.sample;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.nhancv.nchip.NChip;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    NChip chip;
    Button btAddFilter;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NChip.MAX_CHARACTER_COUNT = 20;
        chip = (NChip) findViewById(R.id.chipText);
        chip.setAutoSplitInActionKey(false);
        btAddFilter = (Button) findViewById(R.id.btAddFilter);

        String[] countries = {"india", "australia", "austria", "indonesia", "canada"};

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, countries);
        btAddFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> txtList = chip.getText();
                if (txtList.size() > 0) {
                    chip.splitText();
                }

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

                boolean t = false;
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (adapter.getItem(i).startsWith(editable.toString())) {
                        Log.e(TAG, "find: " + adapter.getItem(i));
                        t = true;
                        break;
                    }
                }
                if (!t) {
                    btAddFilter.setVisibility(View.VISIBLE);
                } else {
                    btAddFilter.setVisibility(View.GONE);
                }


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
                Log.d(TAG, "added txt= " + txt + " pos= " + String.valueOf(pos));

            }

            @Override
            public void onChipRemoved(int pos, String txt) {
                Log.d(TAG, "removed txt = " + txt + " pos= " + String.valueOf(pos));
            }
        });

        chip.setAdapter(adapter);
    }

    public void click(View v) {
        Log.e(TAG, "click: " + chip.getText().toString());

    }
}
