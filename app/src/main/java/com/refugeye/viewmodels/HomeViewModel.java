package com.refugeye.viewmodels;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.graphics.Bitmap;

import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.Nullable;

import com.refugeye.data.model.Picto;
import com.refugeye.data.repository.PictoRepository;

public class HomeViewModel extends AndroidViewModel {

    private final PictoRepository pictoRepository;

    public HomeViewModel(Application application) {
        super(application);
        pictoRepository = new PictoRepository(application);
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
}
