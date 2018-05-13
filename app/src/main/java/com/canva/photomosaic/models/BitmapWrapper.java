package com.canva.photomosaic.models;

import android.graphics.Bitmap;

public class BitmapWrapper {

    private Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(final Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getHeight() {
        return bitmap.getHeight();
    }

    public int getWidth() {
        return bitmap.getWidth();
    }

    public int getPixel(final int pixelX, final int pixelY) {
        return bitmap.getPixel(pixelX,pixelY);
    }

    public void setPixels(final int[] pixels, final int offset, final int stride, final int x, final int y, final int bitmapWidth, final int bitmapHeight) {
        bitmap.setPixels(pixels, offset, stride, x, y, bitmapWidth, bitmapHeight);
    }

    public void getPixels(final int[] pixels, final int offset, final int stride, final int x, final int y, final int bitmapWidth, final int bitmapHeight) {
        bitmap.getPixels(pixels, offset, stride, x, y, bitmapWidth, bitmapHeight);
    }
}
