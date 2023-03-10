package com.example.mykeyboard;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.content.Intent;
import android.graphics.Color;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyInputMethodService extends InputMethodService  {
    public static MyKeyboardView customKeyboardView; // This works
    private Keyboard keyboard;
    private Keyboard keyboard_numbers;
    private Keyboard keyboard_caps;
    private boolean caps = false;
    private boolean caps_clicked_twice = false;
    private boolean isKeyboardVisible = false;

    char code;  // this is the char that will take the letter that the user has pressed
    String sentence = ""; // the sentence the user will ask the chatgpt bot
    @Override
    public View onCreateInputView() {
        // Inflate the layout file that contains the custom keyboard and button views
        View view = getLayoutInflater().inflate(R.layout.input_view_layout, null);

        // this is the newly added line
        customKeyboardView = new MyKeyboardView(this, null);

        //making the keyboard
        customKeyboardView = view.findViewById(R.id.custom_keyboard_view);
        keyboard = new Keyboard(this, R.xml.keys_layout);
        keyboard_numbers = new Keyboard(this,R.xml.numbers_layout);
        keyboard_caps = new Keyboard(this, R.xml.keys_caps_layout);

        // Set the custom keyboard as the keyboard for the custom keyboard view
        customKeyboardView.setKeyboard(keyboard);
        customKeyboardView.setPreviewEnabled(true);

        customKeyboardView.setOnKeyboardActionListener(new KeyboardView.OnKeyboardActionListener() {
            @Override
            public void onPress(int primaryCode) {

            }

            @Override
            public void onRelease(int primaryCode) {

            }

            @Override
            public void onKey(int primaryCode, int[] keyCodes) {
                InputConnection inputConnection = getCurrentInputConnection();
                if (inputConnection != null) {
                    switch(primaryCode) {
                        case Keyboard.KEYCODE_DELETE :
                            CharSequence selectedText = inputConnection.getSelectedText(0);
                            if (TextUtils.isEmpty(selectedText)) {
                                inputConnection.deleteSurroundingText(1, 0);
                            } else {
                                inputConnection.commitText("", 1);
                            }
                            break;

                        case Keyboard.KEYCODE_SHIFT:
                            if (caps && !caps_clicked_twice){
                                //Log.d("Value of Caps after",String.valueOf(caps));
                                caps = !caps;
                                caps_clicked_twice = true;
                                customKeyboardView.setKeyboard(keyboard_caps);
                            }
                            else if(!caps){
                                caps = true;
                            }
                            //keyboard.setShifted(caps);
                            if (caps) {
                                if(caps_clicked_twice){
                                    caps_clicked_twice = !caps_clicked_twice;
                                    caps = !caps;
                                    customKeyboardView.setKeyboard(keyboard);
                                }else {
                                    customKeyboardView.setKeyboard(keyboard_caps);
                                }
                            }else if(!caps_clicked_twice){
                                customKeyboardView.setKeyboard(keyboard);
                            }

                            customKeyboardView.invalidateAllKeys();
                            break;

                        case Keyboard.KEYCODE_DONE:

                            String text_typed_by_user="";

                            if (inputConnection != null) {
                                CharSequence charBeforeCursor = inputConnection.getTextBeforeCursor(1000, 0);
                                CharSequence charAfterCursor = inputConnection.getTextAfterCursor(1000, 0);
                                text_typed_by_user = charBeforeCursor.toString() + charAfterCursor.toString();
                            }

                            if (!text_typed_by_user.isEmpty()){

                                EditorInfo ei = getCurrentInputEditorInfo();
                                int imeOptions = ei.imeOptions & (EditorInfo.IME_MASK_ACTION | EditorInfo.IME_FLAG_NO_ENTER_ACTION);

                                if ((imeOptions & EditorInfo.IME_ACTION_SEARCH) != 0) {
                                    // Send the text (e.g. in a chat app)
                                    getCurrentInputConnection().performEditorAction(EditorInfo.IME_ACTION_SEARCH);
                                }  else {
                                    // Insert a newline character
                                    inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                                }
                            }

                            break;
                        default :
                            //making char code global to see if we can use elsewhere
                            code = (char) primaryCode;
                            if(Character.isLetter(code) && caps && !caps_clicked_twice){
                                caps = !caps;
                                //Log.d("Value of Caps",String.valueOf(caps));
                                code = Character.toUpperCase(code);
                                customKeyboardView.setKeyboard(keyboard);
                            }else if(caps_clicked_twice){
                                code = Character.toUpperCase(code);
                            }
                            //Log.d("After Pressing any letter ", String.valueOf(code));

                            if(primaryCode == 16){inputConnection.commitText("~", 1);}
                            if(primaryCode == 17){inputConnection.commitText("|", 1);}
                            if(primaryCode == 18){inputConnection.commitText("^", 1);}
                            if(primaryCode == 19){inputConnection.commitText("{", 1);}
                            if(primaryCode == 20){inputConnection.commitText("}", 1);}
                            if(primaryCode == 21){inputConnection.commitText("?", 1);}
                            if(primaryCode == 22){inputConnection.commitText("[", 1);}
                            if(primaryCode == 23){inputConnection.commitText("]", 1);}
                            if(primaryCode == 24){inputConnection.commitText("<", 1);}
                            if(primaryCode == 25){inputConnection.commitText(">", 1);}

                            if(primaryCode == 502){
                                //Log.d("It Pressed 123 Button ", "Yes");
                                customKeyboardView.setKeyboard(keyboard_numbers);
                            }else if (primaryCode == 505) {
                                //Log.d("It Pressed ABC Button ", "Yes");
                                caps = false;
                                caps_clicked_twice = false;
                                customKeyboardView.setKeyboard(keyboard);
                            }else {
                                //customKeyboardView.setPreviewEnabled(false);
                                // adding the code to sentence
                                sentence = sentence + String.valueOf(code);
                                Log.d("Sentence made my typing ", sentence);
                                inputConnection.commitText(String.valueOf(code), 1);
                            }
                    }
                }
            }
            @Override
            public void onText(CharSequence text) {

            }
            @Override
            public void swipeLeft() {

            }
            @Override
            public void swipeRight() {

            }
            @Override
            public void swipeDown() {

            }
            @Override
            public void swipeUp() {

            }
        });

        return view;
    } // onCreateInputViewEnds Here
    @Override
    public void onWindowHidden() {
        super.onWindowHidden();
        isKeyboardVisible = false;

    }

    @Override
    public void onWindowShown() {
        super.onWindowShown();
        isKeyboardVisible = true;
    }

}   // End of class MyInputMethodService
