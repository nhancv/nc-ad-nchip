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
import android.widget.Button;

import com.nhancv.nchip.NChip;

import java.util.ArrayList;
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

        List<Obj> list = new ArrayList<>();
        list.add(new Obj(1, "test1"));
        list.add(new Obj(3, "test2"));

        final ArrayAdapter<Obj> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        btAddFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> txtList = chip.getText();
                if (txtList.size() > 0) {
                    chip.splitText();
                    adapter.add(new Obj(33, txtList.get(txtList.size() - 1)));
                }

            }
        });
        chip.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(TAG, "onItemClick: " + ((Obj) adapterView.getAdapter().getItem(i)).show());
                adapter.remove((Obj) adapterView.getAdapter().getItem(i));
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
                    if (adapter.getItem(i).name.startsWith(editable.toString())) {
                        Log.e(TAG, "find: " + adapter.getItem(i).show());
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
