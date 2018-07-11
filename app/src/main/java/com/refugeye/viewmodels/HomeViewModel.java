package com.refugeye.viewmodels;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.annotation.Nullable;

import com.refugeye.data.model.Picto;
import com.refugeye.data.repository.BitmapRepository;
import com.refugeye.data.repository.PictoRepository;

public class HomeViewModel extends AndroidViewModel implements LifecycleObserver {

    private static final String TAG = "HomeViewModel";

    private static final String STATE_SEARCH = "STATE_SEARCH";
    private static final String STATE_SWIPE_OPENED = "STATE_SWIPE_OPENED";
    private static final String STATE_DRAWING_FILE = "STATE_DRAWING_FILE";

    private final SharedPreferences preferences;
    private final PictoRepository pictoRepository;
    private final BitmapRepository bitmapRepository;

    public HomeViewModel(Application application) {
        super(application);
        preferences = application.getSharedPreferences("HomeViewModel", Context.MODE_PRIVATE);
        pictoRepository = new PictoRepository(application);
        bitmapRepository = new BitmapRepository(application);
    }

    //region Data
    private MutableLiveData<List<Picto>> pictoList;
    private MutableLiveData<String> searchText = new MutableLiveData<>();
    private MutableLiveData<Bitmap> drawingBitmap = new MutableLiveData<>();
    private MutableLiveData<Boolean> swipeViewOpened = new MutableLiveData<>();
    //endregion

    public MutableLiveData<List<Picto>> getPictoList() {
        if (pictoList == null) {
            pictoList = new MutableLiveData<>();
            pictoList.postValue(pictoRepository.getPictoList());
        }
        return pictoList;
    }

    public MutableLiveData<String> getSearchText() {
        return searchText;
    }

    public void setSearchText(@Nullable String searchText) {
        this.searchText.postValue(searchText);

        List<Picto> pictoList = null;
        // If input is empty, present nothing until something is typed
        if (searchText != null && !searchText.isEmpty()) {
            pictoList = pictoRepository.findWithNameContaining(searchText);
        }

        // But present everything if search input is empty and keyboard away
        if (searchText == null) {
            pictoList = pictoRepository.getPictoList();
        }

        if (pictoList == null) {
            pictoList = new ArrayList<>();
        }
        this.pictoList.postValue(pictoList);
    }

    public LiveData<Bitmap> getDrawingBitmap() {
        return drawingBitmap;
    }

    public void setDrawingBitmap(@Nullable Bitmap drawingBitmap) {
        if (this.drawingBitmap.getValue() != drawingBitmap) {
            this.drawingBitmap.postValue(drawingBitmap);
        }
    }

    public LiveData<Boolean> isSwipeViewOpened() {
        return swipeViewOpened;
    }

    public void setSwipeViewOpened(boolean swipeViewOpened) {
        if (this.swipeViewOpened.getValue() != Boolean.valueOf(swipeViewOpened)) {
            this.swipeViewOpened.postValue(swipeViewOpened);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onMoveToBackground() {
        Log.d(TAG, "Moving to background…");
        boolean opened = swipeViewOpened.getValue() != null ? swipeViewOpened.getValue() : true;
        File file = bitmapRepository.save(drawingBitmap.getValue());
        preferences.edit()
                .putString(STATE_SEARCH, searchText.getValue())
                .putBoolean(STATE_SWIPE_OPENED, opened)
                .putString(STATE_DRAWING_FILE, file != null ? file.getAbsolutePath() : null)
                .apply();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onMoveToForeground() {
        Log.d(TAG, "Returning to foreground…");
        setSearchText(preferences.getString(STATE_SEARCH, null));
        setSwipeViewOpened(preferences.getBoolean(STATE_SWIPE_OPENED, true));

        String path = preferences.getString(STATE_DRAWING_FILE, null);
        Log.d(TAG, "file: " + path);
        if (path != null) {
            setDrawingBitmap(bitmapRepository.load());
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "Destroying…");
        bitmapRepository.delete();
        preferences.edit()
                .clear()
                .apply();
    }
}
