package com.nhancv.nchip;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;

import java.lang.reflect.Method;


public class ChipOnFocusChangeListener implements View.OnFocusChangeListener {

    private NChip NChip;
    private EditText editText;
    private Drawable editTextDrawable, chipLayoutDrawable;
    private View.OnFocusChangeListener focusChangeListener;

    public ChipOnFocusChangeListener(NChip NChip, EditText editText,
                                     Drawable editTextDrawable, Drawable chipLayoutDrawable,
                                     View.OnFocusChangeListener focusChangeListener) {
        this.NChip = NChip;
        this.editText = editText;
        this.editTextDrawable = editTextDrawable;
        this.chipLayoutDrawable = chipLayoutDrawable;
        this.focusChangeListener = focusChangeListener;
    }

    @Override
    public void onFocusChange(View view, final boolean b) {

        if(focusChangeListener != null){
            focusChangeListener.onFocusChange(view, b);
        }

        if(NChip.getWidth() < 1){
            ViewTreeObserver vto = NChip.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < 16) {
                        NChip.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        NChip.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    changeBackground(b);
                }
            });
        }else{
            changeBackground(b);
        }
    }

    void changeBackground(boolean b){
        try{

            if(chipLayoutDrawable != null && chipLayoutDrawable instanceof StateListDrawable){

                StateListDrawable stateListDrawable = (StateListDrawable) chipLayoutDrawable;
                Method getStateDrawable = StateListDrawable.class.getMethod("getStateDrawable", int.class);
                //int[] currentState = stateListDrawable.getState();
                //Method getStateDrawableIndex = StateListDrawable.class.getMethod("getStateDrawableIndex", int[].class);
                int stateEnabled = 1;
                int statePressed = 1;

                for (int i = 0; i < 4; i++){
                    try{
                        Drawable drawable = (Drawable) getStateDrawable.invoke(stateListDrawable, i);
                        if(drawable != null){
                            if (stateListDrawable.getCurrent() == drawable.getCurrent()){
                                stateEnabled = i;
                            }else {
                                statePressed = i;
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                if(b){
                    Drawable drawable = (Drawable) getStateDrawable.invoke(stateListDrawable, statePressed);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                        NChip.setBackground(drawable);
                    } else{
                        NChip.setBackgroundDrawable(drawable);
                    }
                    NChip.requestFocus();
                }else {
                    Drawable drawable = (Drawable) getStateDrawable.invoke(stateListDrawable, stateEnabled);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                        NChip.setBackground(drawable);
                    } else{
                        NChip.setBackgroundDrawable(drawable);
                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
