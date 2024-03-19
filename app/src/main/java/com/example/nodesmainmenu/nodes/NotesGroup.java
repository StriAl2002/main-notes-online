package com.example.nodesmainmenu.nodes;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.EditText;
import android.widget.Toast;
import com.example.nodesmainmenu.R;
import com.example.nodesmainmenu.databinding.ActivityNotesGroupBinding;

import java.util.Objects;

public class NotesGroup extends AppCompatActivity {

    SharedPreferences sPref;

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
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = this::hide;
    private final View.OnTouchListener mDelayHideTouchListener = (view, motionEvent) -> {
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
    };
    private ActivityNotesGroupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String id = Objects.requireNonNull(getIntent().getExtras()).getString("key");
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
        binding = ActivityNotesGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mVisible = true;
        mControlsView = binding.fullscreenContentControls;
        mContentView = binding.fullscreenContent;
        load(mContentView);
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

    public void OpenGroup(View view) {
        save();
        finish();
    }

    private void save() {
        String id = Objects.requireNonNull(getIntent().getExtras()).getString("key");
        final EditText edit = findViewById(R.id.Rename);
        String myText = edit.getText().toString();
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(id, myText);
        ed.apply();
        Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show();
    }

    public void load(View view) {
        String id = Objects.requireNonNull(getIntent().getExtras()).getString("key");
        final EditText edit = findViewById(R.id.Rename);
        sPref = getPreferences(MODE_PRIVATE);
        String savedText;
        try {
            savedText = sPref.getString(id, "");
            edit.setText(savedText);
            Toast.makeText(this, "Успешно загружено", Toast.LENGTH_SHORT).show();
        } catch (Exception ignored){}
    }

    boolean state = true;
    public void menu(View view){
        String id = Objects.requireNonNull(getIntent().getExtras()).getString("key");
        ConstraintLayout menu = findViewById(R.id.menu);
        if (state){
            menu.setVisibility(View.VISIBLE);
            EditText et = findViewById(R.id.renametext);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String nodesCarrier = id + "groupnode";
            et.setText(preferences.getString(nodesCarrier, ""));
            state = false;
        } else {
            menu.setVisibility(View.GONE);
            state = true;
        }
    }

    public void delete(View view){
        String id = Objects.requireNonNull(getIntent().getExtras()).getString("key");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor ed =  preferences.edit();
        id += "groupnode";
        ed.putString(id, "deleted");
        ed.apply();
        Intent intent = new Intent(getApplicationContext(), FullscreenActivityGroup.class);
        startActivity(intent);
        finish();
    }

    public void rename(View view){
        String id = Objects.requireNonNull(getIntent().getExtras()).getString("key");
        EditText et = findViewById(R.id.renametext);
        String newName = et.getText().toString();
        if ((newName.length() < 20) && (!newName.contains("deleted"))){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor ed =  preferences.edit();
            String NodesCarrier = id + "groupnode";
            ed.putString(NodesCarrier, newName);
            ed.apply();
            Toast.makeText(this, "Имя заметки успешно изменено на " + newName, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Нарушение в новом имени заметки", Toast.LENGTH_SHORT).show();
        }
        menu(view);
    }

    public void users(View view){
        String id = Objects.requireNonNull(getIntent().getExtras()).getString("key");
        Intent intent = new Intent(getApplicationContext(), UserList.class);
        intent.putExtra("key", id);
        startActivity(intent);
    }
}
