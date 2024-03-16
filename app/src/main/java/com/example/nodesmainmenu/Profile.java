package com.example.nodesmainmenu;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nodesmainmenu.databinding.ActivityProfileBinding;
import com.example.nodesmainmenu.ui.login.LoginActivity;

import java.util.Objects;

public class Profile extends AppCompatActivity {
    private static final boolean AUTO_HIDE = true;

    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler(Looper.myLooper());
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            if (Build.VERSION.SDK_INT >= 30) {
                mContentView.getWindowInsetsController().hide(
                        WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            } else {
                mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (AUTO_HIDE) {
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return false;
        }
    };
    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mVisible = true;
        mControlsView = binding.fullscreenContentControls;
        mContentView = binding.fullscreenContent;
        EditText et = findViewById(R.id.UserName);
        et.setText(FullscreenActivity.getUsername());
        et.setClickable(false);

        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void show() {
        if (Build.VERSION.SDK_INT >= 30) {
            mContentView.getWindowInsetsController().show(
                    WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        mVisible = true;
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void OpenMain(View view) {
        Intent intent = new Intent(this, FullscreenActivity.class);
        startActivity(intent);
        finish();
    }

    boolean state = true;
    public void ChangeName(View view){
        ConstraintLayout menu = findViewById(R.id.Name);
        EditText et = findViewById(R.id.editTextTextPersonName);
        EditText hat = findViewById(R.id.UserName);
        Button btn = findViewById(R.id.button4);
        if (state){
            menu.setVisibility(View.VISIBLE);
            et.setText(FullscreenActivity.getUsername());
            state = false;
            btn.setText("Сохранить");
        } else {
            menu.setVisibility(View.GONE);
            state = true;
            btn.setText("Изменить имя");
            if (!et.getText().toString().equals(FullscreenActivity.getUsername())){
                FullscreenActivity.setUsername(et.getText().toString());
                hat.setText(et.getText().toString());
            }
        }
    }

    boolean statePass = true;
    public void ChangePassword(View view){
        ConstraintLayout menu = findViewById(R.id.Password);
        Button btn = findViewById(R.id.button10);
        EditText oldPass = findViewById(R.id.oldPass);
        EditText newPass = findViewById(R.id.newPass);
        EditText confPass = findViewById(R.id.confirmPass);
        if (statePass){
            menu.setVisibility(View.VISIBLE);
            statePass = false;
            btn.setText("Сохранить");
        } else {
            menu.setVisibility(View.GONE);
            statePass = true;
            btn.setText("Сменить пароль");
            if (oldPass.getText().toString().equals(FullscreenActivity.getPassword())){
                if (newPass.getText().toString().equals(confPass.getText().toString())){
                    FullscreenActivity.setPassword(newPass.getText().toString());
                    Toast.makeText(this, "Пароль успешно изменен", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Неверный пароль", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void OpenAuth(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}