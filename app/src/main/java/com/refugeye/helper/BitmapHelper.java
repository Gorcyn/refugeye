package com.refugeye.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.provider.MediaStore;
import android.util.Log;

import android.support.annotation.Nullable;

public class BitmapHelper {

    private static final String TAG = "BitmapHelper";

    /**
     * Trim bitmap by cropping around first used pixels
     *
     * @param source Bitmap
     *
     * @return Bitmap
     */
    public static Bitmap trimBitmap(Bitmap source) {

        int firstX = 0, firstY = 0;
        int lastX = source.getWidth();
        int lastY = source.getHeight();
        int[] pixels = new int[source.getWidth() * source.getHeight()];
        source.getPixels(pixels, 0, source.getWidth(), 0, 0, source.getWidth(), source.getHeight());
        loop:
        for (int x = 0; x < source.getWidth(); x++) {
            for (int y = 0; y < source.getHeight(); y++) {
                if (pixels[x + (y * source.getWidth())] != Color.TRANSPARENT) {
                    firstX = x;
                    break loop;
                }
            }
        }
        loop:
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = firstX; x < source.getWidth(); x++) {
                if (pixels[x + (y * source.getWidth())] != Color.TRANSPARENT) {
                    firstY = y;
                    break loop;
                }
            }
        }
        loop:
        for (int x = source.getWidth() - 1; x >= firstX; x--) {
            for (int y = source.getHeight() - 1; y >= firstY; y--) {
                if (pixels[x + (y * source.getWidth())] != Color.TRANSPARENT) {
                    lastX = x;
                    break loop;
                }
            }
        }
        loop:
        for (int y = source.getHeight() - 1; y >= firstY; y--) {
            for (int x = source.getWidth() - 1; x >= firstX; x--) {
                if (pixels[x + (y * source.getWidth())] != Color.TRANSPARENT) {
                    lastY = y;
                    break loop;
                }
            }
        }
        Log.i(TAG, "x: " + firstX + "; y: " + firstY + "; width: " + (lastX - firstX) + "; height: " + (lastY - firstY));
        Bitmap trimmed = Bitmap.createBitmap(source, firstX, firstY, lastX - firstX, lastY - firstY);
        source.recycle();
        return trimmed;
    }

    /**
     * Resize bitmap
     *
     * @param source Bitmap
     * @param maxWidth float
     * @param maxHeight float
     *
     * @return Bitmap
     */
    public static Bitmap resizeBitmap(Bitmap source, int maxWidth, int maxHeight) {

        if (maxWidth <= 0 || maxHeight <= 0) {
            return source;
        }

        float ratioBitmap = (float) source.getWidth() / (float) source.getHeight();
        float ratioMax = (float) maxWidth / (float) maxHeight;

        int finalWidth = maxWidth;
        int finalHeight = maxHeight;
        if (ratioMax > ratioBitmap) {
            finalWidth = (int) ((float) maxHeight * ratioBitmap);
        } else {
            finalHeight = (int) ((float) maxWidth / ratioBitmap);
        }
        Bitmap resized = Bitmap.createScaledBitmap(source, finalWidth, finalHeight, true);
        source.recycle();
        return resized;
    }

    /**
     * Save bitmap to file
     *
     * @param source Bitmap
     *
     * @return File
     */
    @Nullable
    private static File saveToFile(Context context, Bitmap source, String fileName) {
        try {
            File file = new File(context.getFilesDir(), fileName);
            if ((file.exists() && file.delete()) || file.createNewFile()) {
                FileOutputStream out = new FileOutputStream(file);
                source.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.close();
                Log.d(TAG, "saveToFile: " + file.getAbsolutePath());
                return file;
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * Save bitmap to gallery
     *
     * @param context Context
     * @param source Bitmap
     */
    public static void saveToGallery(Context context, Bitmap source) {

        // Add bitmap to white background
        Bitmap bitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(source, new Matrix(), null);

        // Define file name
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault());
        Date date = new Date();

        String fileName = "Refugeye-" + simpleDateFormat.format(date) + ".png";

        // Save to file
        File file = saveToFile(context, source, fileName);
        if (file != null && file.exists()) {
            String title = "Generated by Refugeye";
            String desc = "Saved on: " + simpleDateFormat.format(date);
            MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, title, desc);
        }
    }
}
