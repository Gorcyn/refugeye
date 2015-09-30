package com.refugeye;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Home extends AppCompatActivity {

    public DrawingView drawingView;
    public ListView listView;
    private EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        drawingView = (DrawingView) findViewById(R.id.home_drawing_view);
        drawingView.setupDrawing();
        drawingView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });



        listView = (ListView) findViewById(R.id.home_picto_list);

        final PictoListAdapter pictoListAdapter = new PictoListAdapter(this);

        pictoListAdapter.add(new Picto(R.drawable.camping1, new String[] {"camping", "camping"}));
        pictoListAdapter.add(new Picto(R.drawable.noun_5, new String[]{"24"}));
        pictoListAdapter.add(new Picto(R.drawable.noun_47, new String[]{"24"}));
        pictoListAdapter.add(new Picto(R.drawable.noun_359_cc, new String[]{"plane", "avion"}));
        pictoListAdapter.add(new Picto(R.drawable.noun_601_cc, new String[]{"plane", "avion"}));
        pictoListAdapter.add(new Picto(R.drawable.noun_620, new String[]{"plane", "avion"}));
        pictoListAdapter.add(new Picto(R.drawable.noun_626, new String[]{"plane", "avion"}));
        pictoListAdapter.add(new Picto(R.drawable.noun_859_cc, new String[]{"barcode", "code-barre"}));
        pictoListAdapter.add(new Picto(R.drawable.noun_1475, new String[]{"umbrella", "parapluie"}));
        pictoListAdapter.add(new Picto(R.drawable.noun_2138_cc, new String[]{"car", "voiture"}));
        pictoListAdapter.add(new Picto(R.drawable.noun_2219, new String[]{"boat", "bateau"}));
        pictoListAdapter.add(new Picto(R.drawable.noun_2380, new String[]{"box", "boite"}));
        pictoListAdapter.add(new Picto(R.drawable.noun_2735, new String[]{"boxes", "boites"}));
        pictoListAdapter.add(new Picto(R.drawable.noun_3012, new String[]{"boxes", "boites"}));
        pictoListAdapter.add(new Picto(R.drawable.noun_3953_cc, new String[]{"call", "appel"}));
        pictoListAdapter.add(new Picto(R.drawable.noun_4398, new String[]{"call", "appel"}));
        pictoListAdapter.add(new Picto(R.drawable.noun_4403, new String[]{"time", "heure"}));
        pictoListAdapter.add(new Picto(R.drawable.noun_5280, new String[]{"clipboard", "presse-papier"}));

        listView.setAdapter(pictoListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), pictoListAdapter.getItem(position).resId);
                drawingView.addBitmap(bitmap);
            }
        });

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

        findViewById(R.id.home_save_canvas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.save(Home.this);
            }
        });
        search = (EditText) findViewById(R.id.home_search);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
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
