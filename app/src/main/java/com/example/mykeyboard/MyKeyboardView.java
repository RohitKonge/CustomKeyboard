package com.example.mykeyboard;

import android.content.Context;
import android.graphics.Canvas;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.InputConnection;

public class MyKeyboardView extends KeyboardView{

    public MyKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}
