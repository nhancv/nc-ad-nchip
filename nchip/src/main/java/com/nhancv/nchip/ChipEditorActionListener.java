package com.nhancv.nchip;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;


public class ChipEditorActionListener implements EditText.OnEditorActionListener {

    private EditText editText;
    private String chipSplitFlag;

    public ChipEditorActionListener(EditText editText, String chipSplitFlag) {
        this.editText = editText;
        this.chipSplitFlag = chipSplitFlag;
    }

    public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
        try {
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (editText.getText() != null && editText.getText().toString().length() > 0) {
                    editText.setText(editText.getText().toString() + chipSplitFlag);
                }
            }
        } catch (Exception e) {
        }
        return true;
    }
}
