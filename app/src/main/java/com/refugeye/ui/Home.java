package com.refugeye.ui;

import java.util.List;

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

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.refugeye.R;
import com.refugeye.viewmodels.HomeViewModel;
import com.refugeye.widget.SwipeView;
import com.refugeye.data.model.Picto;
import com.refugeye.helper.BitmapHelper;
import com.refugeye.ui.about.About;
import com.refugeye.ui.pictoList.PictoListAdapter;
import com.refugeye.widget.DrawingView;

public class Home extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL = 0;

    //region UI
    private DrawingView drawingView;

    private View infoButton;
    private View clearCanvasButton;
    private View saveCanvasButton;
    private View successOverlay;

    private SwipeView swipeView;
    private EditText search;
    private RecyclerView pictoRecycler;
    private PictoListAdapter pictoListAdapter = new PictoListAdapter();
    //endregion

    HomeViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.activity_home);
        drawingView = findViewById(R.id.home_drawing_view);

        infoButton = findViewById(R.id.home_info);
        clearCanvasButton = findViewById(R.id.home_clear_canvas);
        saveCanvasButton = findViewById(R.id.home_save_canvas);
        successOverlay = findViewById(R.id.success_overlay);

        swipeView = findViewById(R.id.sliding_pannel);
        search = findViewById(R.id.home_search);
        pictoRecycler = findViewById(R.id.home_picto_recycler);
        pictoRecycler.setAdapter(pictoListAdapter);

        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        viewModel.getSearchText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String searchText) {
                if (!search.getText().toString().equals(searchText)) {
                    search.clearFocus();
                    search.setText(searchText);
                }
            }
        });
        viewModel.getPictoList().observe(this, new Observer<List<Picto>>() {
            @Override
            public void onChanged(@Nullable List<Picto> pictos) {
                pictoListAdapter.setPictoList(pictos);
                pictoListAdapter.notifyDataSetChanged();
            }
        });
        viewModel.getDrawingBitmap().observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(@Nullable Bitmap bitmap) {
                drawingView.restoreDrawing(bitmap);
            }
        });
        viewModel.isSwipeViewOpened().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isSwipeViewOpened) {
                boolean opened = true;
                if (Boolean.FALSE.equals(isSwipeViewOpened)) {
                    opened = false;
                }
                swipeView.setOpened(opened);
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().add(R.id.drawer_layout, new About()).commit();
            }
        });

        clearCanvasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setDrawingBitmap(null);
            }
        });

        saveCanvasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToSaveToGallery();
            }
        });

        swipeView.setOnOpenedChangeListener(new SwipeView.OnOpenedChangeListener() {
            @Override
            public void onOpenedChange(boolean opened) {
                viewModel.setSwipeViewOpened(opened);
            }
        });

        drawingView.setOnDrawingChangeListener(new DrawingView.OnDrawingChangeListener() {
            @Override
            public void onDrawingChange(Bitmap drawing) {
                viewModel.setDrawingBitmap(drawing);
            }
        });

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
                    viewModel.setSearchText(null);
                }
                if (event.getAction() == DragEvent.ACTION_DRAG_STARTED) {
                    viewModel.setSwipeViewOpened(false);
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
                    viewModel.setSearchText(search.getText().toString());
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
                if (search.hasFocus()) {
                    viewModel.setSearchText(search.getText().toString());
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
