package com.refugeye.data.repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import android.support.annotation.Nullable;

public class BitmapRepository {

    private static final String TAG = "BitmapRepository";
    private static final String FILE_NAME = "bitmap.png";

    private Context context;

    public BitmapRepository(Context context) {
        this.context = context;
    }

    /**
     * Save bitmap
     *
     * @param source Bitmap
     *
     * @return File
     */
    public File save(@Nullable Bitmap source) {
        if (source == null) {
            return null;
        }
        try {
            File file = new File(context.getFilesDir(), FILE_NAME);
            if ((file.exists() && file.delete()) || file.createNewFile()) {
                FileOutputStream out = new FileOutputStream(file);
                source.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.close();
                return file;
            }
        } catch (IOException exception) {
            Log.e(TAG, "Could not save to file " + FILE_NAME, exception);
        }
        return null;
    }

    /**
     * Load bitmap
     *
     * @return Bitmap
     */
    public Bitmap load() {
        File file = new File(context.getFilesDir(), FILE_NAME);
        if (file.exists()) {
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        return null;
    }

    /**
     * Delete bitmap
     *
     * @return boolean
     */
    public boolean delete() {
        File file = new File(context.getFilesDir(), FILE_NAME);
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }
}
