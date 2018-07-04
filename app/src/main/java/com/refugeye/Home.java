package com.refugeye;

import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import android.support.v7.app.AppCompatActivity;

import com.refugeye.data.model.Picto;
import com.refugeye.data.repository.PictoRepository;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Home extends AppCompatActivity {

    private DrawingView drawingView;
    private ListView listView;
    private EditText search;
    private SwipeView swipeView;
    private View successOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final PictoListAdapter pictoListAdapter = new PictoListAdapter(this);

        drawingView = findViewById(R.id.home_drawing_view);
        drawingView.setupDrawing();
        drawingView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {

                if (event.getAction() == DragEvent.ACTION_DROP) {

                    Picto selectedPicto = pictoListAdapter.getItem(pictoListAdapter.getSelectedPosition());
                    if (selectedPicto != null) {
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), selectedPicto.getResId());
                        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 2, bitmap.getHeight() * 2, false);
                        drawingView.addBitmap(bitmap, event.getX(), event.getY());
                    }
                }
                if (event.getAction() == DragEvent.ACTION_DRAG_STARTED) {
                    swipeView.close();
                }
                return true;
            }
        });

        swipeView = findViewById(R.id.sliding_pannel);

        listView = findViewById(R.id.home_picto_list);

        PictoRepository repository = new PictoRepository(this);
        pictoListAdapter.addAll(repository.getPictoList());
        listView.setAdapter(pictoListAdapter);


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
                drawingView.save(Home.this);
                successOverlay.setVisibility(View.VISIBLE);
                successOverlay.setAlpha(1.0f);
                successOverlay.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        successOverlay.animate().alpha(-1.0f).setDuration(800).start();
                    }
                }, 500);
            }
        });
        search = findViewById(R.id.home_search);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
                    }
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
                pictoListAdapter.filter(text);

            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
