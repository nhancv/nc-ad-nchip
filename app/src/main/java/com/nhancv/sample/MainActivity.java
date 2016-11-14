package com.nhancv.sample;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.nhancv.nchip.NChip;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    NChip<Obj> chip;
    Button btAddFilter, btGetList;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NChip.MAX_CHARACTER_COUNT = 20;
        chip = (NChip<Obj>) findViewById(R.id.chipText);
        chip.setAutoSplitInActionKey(false);

        btAddFilter = (Button) findViewById(R.id.btAddFilter);
        btGetList = (Button) findViewById(R.id.btGetList);

        btAddFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random ran = new Random();
                Obj obj = new Obj(ran.nextInt(), "test-" + chip.getCurrentText());
                chip.addObj(obj);
            }
        });

        btGetList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Obj> objList = chip.getObjList();
                for (Obj obj : objList) {
                    Log.e(TAG, "onClick: " + obj.show());
                }
            }
        });

        List<Obj> list = new ArrayList<>();
        list.add(new Obj(1, "test1"));
        list.add(new Obj(3, "test2"));

        final ArrayAdapter<Obj> adapter = new ArrayAdapter<>(chip.getContext(), android.R.layout.simple_list_item_1, list);
        chip.setAdapter(adapter);
        chip.addObj(list.get(0));

        chip.setOnChipItemChangeListener(new NChip.ChipItemChangeListener() {
            @Override
            public void onChipAdded(int pos, Object data) {
                btAddFilter.setVisibility(View.GONE);
                if (data != null && data instanceof Obj)
                    Log.e(TAG, "onChipAdded: " + ((Obj) data).show());
            }

            @Override
            public void onChipRemoved(int pos, Object data) {
                btAddFilter.setVisibility(View.GONE);
                if (data != null && data instanceof Obj)
                    Log.e(TAG, "onChipRemoved: " + ((Obj) data).show());
            }
        });
        chip.addLayoutTextChangedListener(new NChipTextChange(new NChipTextChange.Doing() {
            @Override
            public void todo(Editable editable) {
                boolean t = false;
                if (!chip.hasChanged(editable.toString()) && editable.toString().trim().length() > 0) {
                    for (int i = 0; i < adapter.getCount(); i++) {
                        if (adapter.getItem(i).toString().startsWith(editable.toString())) {
                            t = true;
                            break;
                        }
                    }
                } else {
                    t = true;
                }
                if (!t) {
                    btAddFilter.setVisibility(View.VISIBLE);
                } else {
                    btAddFilter.setVisibility(View.GONE);
                }

            }
        }));
    }

}
