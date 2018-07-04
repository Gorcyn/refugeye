package com.refugeye;

import java.util.Arrays;
import java.util.List;

import android.support.annotation.DrawableRes;

public class Picto {

    private int resId;
    private List<String> names;

    public Picto(int resId, String[] namesArray) {
        this.resId = resId;
        this.names = Arrays.asList(namesArray);
    }

    /**
     * Return Picto resource id
     * @return @DrawableRes int
     */
    public @DrawableRes int getResId() {
        return resId;
    }

    /**
     * Return Picto names
     * @return List<String>
     */
    public List<String> getNames() {
        return names;
    }
}
