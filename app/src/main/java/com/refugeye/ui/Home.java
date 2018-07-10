package com.refugeye.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.refugeye.R;
import com.refugeye.widget.SwipeView;
import com.refugeye.data.model.Picto;
import com.refugeye.data.repository.PictoRepository;
import com.refugeye.helper.BitmapHelper;
import com.refugeye.ui.about.About;
import com.refugeye.ui.pictoList.PictoListAdapter;
import com.refugeye.widget.DrawingView;

public class Home extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL = 0;

    private DrawingView drawingView;
    private EditText search;
    private SwipeView swipeView;
    private View successOverlay;

    private PictoRepository repository;

    private RecyclerView pictoRecycler;
    private PictoListAdapter pictoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.activity_home);

        repository = new PictoRepository(this);
        pictoListAdapter = new PictoListAdapter();

        drawingView = findViewById(R.id.home_drawing_view);
        drawingView.setupDrawing();

        swipeView = findViewById(R.id.sliding_pannel);

        pictoRecycler = findViewById(R.id.home_picto_recycler);

        List<Picto> pictoList = repository.getPictoList();
        if (pictoList != null) {
            pictoListAdapter.setPictoList(pictoList);
        }
        pictoRecycler.setAdapter(pictoListAdapter);

        findViewById(R.id.home_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().add(R.id.drawer_layout, new About()).commit();
            }
        });

        findViewById(R.id.home_clear_canvas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.reset();
            }
        });

        successOverlay = findViewById(R.id.success_overlay);

        findViewById(R.id.home_save_canvas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToSaveToGallery();
            }
        });
        search = findViewById(R.id.home_search);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        drawingView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {

                if (event.getAction() == DragEvent.ACTION_DROP) {

                    Picto selectedPicto = pictoListAdapter.getSelectedItem();
                    if (selectedPicto != null) {
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), selectedPicto.getResId());
                        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 2, bitmap.getHeight() * 2, false);
                        drawingView.addBitmap(bitmap, event.getX(), event.getY());
                    }
                    search.setText(null);
                    filterPictoList(search.getText().toString(), false);
                }
                if (event.getAction() == DragEvent.ACTION_DRAG_STARTED) {
                    swipeView.close(0);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
                    }
                    search.clearFocus();
                }
                return true;
            }
        });

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
                    }
                    search.clearFocus();
                    filterPictoList(search.getText().toString(), false);
                    return true;
                }
                return false;
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = search.getText().toString().toLowerCase(Locale.getDefault());
                filterPictoList(text, true);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        swipeView.onPause();
        drawingView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        swipeView.onResume();
        drawingView.onResume();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /**
     * Filter picto list
     *
     * @param search String
     * @param userIsTyping boolean
     */
    private void filterPictoList(String search, boolean userIsTyping) {
        List<Picto> pictoList = null;
        // If input is empty, present nothing until something is typed
        if (!search.isEmpty()) {
            pictoList = repository.findWithNameContaining(search);
        }

        // But present everything if search input is empty and keyboard away
        if (!userIsTyping && pictoList == null) {
            pictoList = repository.getPictoList();
        }

        if (pictoList == null) {
            pictoList = new ArrayList<>();
        }
        pictoListAdapter.setPictoList(pictoList);
        pictoListAdapter.notifyDataSetChanged();
    }

    private void saveToGallery() {
        BitmapHelper.saveToGallery(Home.this, drawingView.getBitmap());

        successOverlay.setVisibility(View.VISIBLE);
        successOverlay.setAlpha(1.0f);
        successOverlay.postDelayed(new Runnable() {
            @Override
            public void run() {
                successOverlay.animate().alpha(-1.0f).setDuration(800).start();
            }
        }, 500);
    }

    private void tryToSaveToGallery() {
        String writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(this, writePermission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{writePermission}, PERMISSION_REQUEST_WRITE_EXTERNAL);
        } else {
            saveToGallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_EXTERNAL:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveToGallery();
                }
                break;
        }
    }
}
