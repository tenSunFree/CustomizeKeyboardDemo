package com.example.administrator.customizekeyboarddemo;

import android.app.Instrumentation;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ziyeyouhu.library.KeyboardTouchListener;
import com.ziyeyouhu.library.KeyboardUtil;

public class MainActivity extends AppCompatActivity {

    public static String currentEdittextNameString;
    public static boolean isItCurrentlyTheFirstViewLinearLayout;

    private LinearLayout rootViewLinearLayout, theFirstViewLinearLayout;
    private ScrollView theSecondViewScrollView;
    private KeyboardUtil keyboardUtil;
    private EditText userNameEditText, passwordEditText, currentEditText;
    private TextView currentTextView;
    private Button returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isItCurrentlyTheFirstViewLinearLayout = true;

        rootViewLinearLayout = findViewById(R.id.rootViewLinearLayout);
        theFirstViewLinearLayout = findViewById(R.id.theFirstViewLinearLayout);
        theSecondViewScrollView = findViewById(R.id.theSecondViewScrollView);
        userNameEditText = findViewById(R.id.userNameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        currentEditText = findViewById(R.id.currentEditText);
        returnButton = findViewById(R.id.returnButton);

        initMoveKeyBoard();
        initTextView();
        initReturnButton();
    }

    /** 自動調整字體大小
     * 使用support的代码调用, 开启自动调整大小的功能
     * 粒度模式的设置参数, 定义字体调整的最大值和最小值以及每次字体调整的步长, 在调整字体大小的时候, 系统会根据最大值和最小值以及步长来调整字体大小并选择一个最佳字体大小
     * setAutoSizeTextTypeUniformWithConfiguration(被设置的TextView, 自动缩放的最小字号, 自动缩放的最大字号, 参数二与参数三所用的单位)
     */
    private void initTextView() {
        currentTextView = findViewById(R.id.currentTextView);
        TextViewCompat.setAutoSizeTextTypeWithDefaults(currentTextView, TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(currentTextView, 5, 100, 1, TypedValue.COMPLEX_UNIT_SP);
    }

    /**
     * 確定的按鈕
     */
    private void initReturnButton() {
        returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /** 模拟返回键功能, Instrumentation用于应用系统交互监听的类, 但是不能在UI线程中使用, 所以必须使用线程, 否则报错 */
                if (keyboardUtil.isShow) {
                    new Thread() {
                        public void run() {
                            try {
                                Instrumentation inst = new Instrumentation();
                                inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
                whetherToSwitchToCustomizedKeyboardMode(false);
            }
        });
    }

    /**
     * 初始化客製化鍵盤相關
     */
    private void initMoveKeyBoard() {
        keyboardUtil = new KeyboardUtil(this, rootViewLinearLayout, theSecondViewScrollView);
        keyboardUtil.setKeyBoardStateChangeListener(new KeyBoardStateListener());                   // monitor the KeyBarod state
        keyboardUtil.setInputOverListener(new inputOverListener());                                 // monitor the finish or next Key
        currentEditText.setOnTouchListener(new KeyboardTouchListener(keyboardUtil, KeyboardUtil.INPUTTYPE_ABC, -1));
        userNameEditText.setOnTouchListener(new KeyboardTouchListener(keyboardUtil, KeyboardUtil.INPUTTYPE_ABC, -1));
        passwordEditText.setOnTouchListener(new KeyboardTouchListener(keyboardUtil, KeyboardUtil.INPUTTYPE_ABC, -1));

        /** 重寫4個EditText的點擊方法, 讓它們被點擊之後 可以切換到自定義鍵盤的相關畫面 */
        userNameEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    currentEdittextNameString = "userNameEditText";
                    whetherToSwitchToCustomizedKeyboardMode(true);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            CustomTouchEvent.setMoveToRight2(1, currentEditText);
                        }
                    }, 200);
                }
                return true;
            }
        });
        passwordEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    currentEdittextNameString = "passwordEditText";
                    whetherToSwitchToCustomizedKeyboardMode(true);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            CustomTouchEvent.setMoveToRight2(1, currentEditText);
                        }
                    }, 200);
                }
                return true;
            }
        });
    }

    /**
     * 關於一般畫面與客製化鍵盤畫面的交互功能
     */
    public void whetherToSwitchToCustomizedKeyboardMode(boolean whether) {
        if (whether == true) {
            theFirstViewLinearLayout.setVisibility(View.GONE);
            theSecondViewScrollView.setVisibility(View.VISIBLE);
            isItCurrentlyTheFirstViewLinearLayout = false;
            switch (currentEdittextNameString) {
                case "userNameEditText":
                    currentTextView.setText("Username");
                    currentEditText.setText("");
                    break;
                case "passwordEditText":
                    currentTextView.setText("Password");
                    currentEditText.setText("");
                    break;
            }
        } else {
            theFirstViewLinearLayout.setVisibility(View.VISIBLE);
            theSecondViewScrollView.setVisibility(View.GONE);
            isItCurrentlyTheFirstViewLinearLayout = true;
            switch (currentEdittextNameString) {
                case "userNameEditText":
                    userNameEditText.setText(currentEditText.getText().toString());
                    break;
                case "passwordEditText":
                    passwordEditText.setText(currentEditText.getText().toString());
                    break;
            }
        }
    }

    /**
     * 按下返回鍵時, 如果客製化鍵盤是顯示狀態 就把它隱藏, 如果是客製化鍵盤的畫面時 就提示點擊確定按鈕
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (keyboardUtil.isShow) {
                keyboardUtil.hideSystemKeyBoard();
                keyboardUtil.hideAllKeyBoard();
                keyboardUtil.hideKeyboardLayout();
            } else {
                if (isItCurrentlyTheFirstViewLinearLayout == true) {
                    return super.onKeyDown(keyCode, event);
                } else {
                    Toast.makeText(this, "請點擊 [確定]", Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        } else
            return super.onKeyDown(keyCode, event);
    }

    class KeyBoardStateListener implements KeyboardUtil.KeyBoardStateChangeListener {
        @Override
        public void KeyBoardStateChange(int state, EditText editText) {
        }
    }

    class inputOverListener implements KeyboardUtil.InputFinishListener {
        @Override
        public void inputHasOver(int onclickType, EditText editText) {
        }
    }
}
