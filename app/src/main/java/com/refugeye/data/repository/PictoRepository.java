package com.refugeye.data.repository;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import android.support.annotation.Nullable;

import com.google.gson.Gson;

import com.refugeye.R;
import com.refugeye.data.gson.PictoJSON;
import com.refugeye.data.model.Picto;

public class PictoRepository {

    private static final String ICON_NAME_PATTERN = "all_icons(.*)";

    private Context context;

    private List<Picto> pictoList = null;

    public PictoRepository(Context context) {
        this.context = context;
    }

    @Nullable
    public List<Picto> getPictoList() {
        if (pictoList == null) {
            try {
                // Listing drawables
                Map<String, Integer> drawableMap = new HashMap<>();Field[] drawablesFields = com.refugeye.R.drawable.class.getFields();
                for (Field field : drawablesFields) {
                    String name = field.getName();
                    if (name.matches(ICON_NAME_PATTERN)) {
                        try {
                            drawableMap.put(name, field.getInt(null));
                        } catch (IllegalAccessException exception) {
                            Log.w(getClass().getSimpleName(), "Illegal access to property", exception);
                        }
                    }
                }

                // Reading json configuration file
                InputStream rawInput = context.getResources().openRawResource(R.raw.icon_list);
                Reader reader = new InputStreamReader(rawInput, "UTF-8");
                PictoJSON[] pictoJSONArray = new Gson().fromJson(reader, PictoJSON[].class);

                // Populating picto list
                ArrayList<Picto> newPictoList = new ArrayList<>();
                for (PictoJSON pictoJSON : pictoJSONArray) {
                    if (drawableMap.containsKey(pictoJSON.getResId())) {

                        // Log
                        String names = "";
                        for (String name : pictoJSON.getNames()) {
                            if (!"".equals(names)) {
                                names += ",";
                            }
                            names += name;
                        }
                        Log.e(getClass().getSimpleName(), pictoJSON.getResId()+": "+names);

                        Picto picto = new Picto(drawableMap.get(pictoJSON.getResId()), pictoJSON.getNames());
                        newPictoList.add(picto);
                    }
                }
                pictoList = newPictoList;
            } catch (UnsupportedEncodingException exception) {
                Log.w(getClass().getSimpleName(), "UTF-8 is not a supported encoding", exception);
            }
        }
        return pictoList;
    }

    @Nullable
    public List<Picto> findWithNameContaining(@Nullable final String containing) {

        // If containing is null, return the entire list
        if (containing == null) {
            return getPictoList();
        }

        List<Picto> pictoList = getPictoList();
        if (pictoList == null) {
            return null;
        }

        List<Picto> filteredPictoList = new ArrayList<>();

        for (Picto picto : pictoList) {
            List<String> nameList = picto.getNames();

            boolean thisPictoWasAdded = false;
            int index = 0;
            while (!thisPictoWasAdded && index < nameList.size()) {
                if (nameList.get(index).toLowerCase(Locale.getDefault()).contains(containing.toLowerCase(Locale.getDefault()))) {
                    filteredPictoList.add(picto);
                    thisPictoWasAdded = true;
                }
                index++;
            }
        }
        return filteredPictoList;
    }
}
