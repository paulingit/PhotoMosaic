package com.canva.photomosaic.models;

public class PhotoMosaicResponse {
    private BitmapWrapper mBitmapWrapper;
    private int mTileXPosition;
    private int mTileYPosition;

    public BitmapWrapper getBitmapWrapper() {
        return mBitmapWrapper;
    }

    public void setBitmapWrapper(final BitmapWrapper bitmapWrapper) {
        mBitmapWrapper = bitmapWrapper;
    }

    public void setTileXPosition(final int tileXPosition) {
        mTileXPosition = tileXPosition;
    }

    public int getTileXPosition() {
        return mTileXPosition;
    }

    public void setTileYPosition(final int tileYPosition) {
        mTileYPosition = tileYPosition;
    }

    public int getTileYPosition() {
        return mTileYPosition;
    }

    @Override
    public String toString() {
        return "PhotoMosaicResponse : " +"/n"+
                "mBitmap.height=" + mBitmapWrapper.getHeight() +"/n"+
                "mBitmap.width=" + mBitmapWrapper.getWidth() +"/n"+
                "mTileXPosition=" + mTileXPosition +"/n"+
                "mTileYPosition=" + mTileYPosition;
    }
}
