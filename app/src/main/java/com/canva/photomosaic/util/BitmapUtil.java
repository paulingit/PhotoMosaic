package com.canva.photomosaic.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.canva.photomosaic.models.BitmapWrapper;
import com.canva.photomosaic.models.PhotoMosaicRequest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BitmapUtil {

    private static final int MAX_HEIGHT = 500;
    private static final int MAX_WIDTH = 500;

    private static final String TAG = "BitmapUtil";

    private final Context mContext;
    private Logger mLogger;

    public BitmapUtil(Context context, Logger logger) {
        mContext = context;
        mLogger = logger;
    }

    public BitmapWrapper getBitmap(@NonNull String mosaicUrl, int width, int height, @ColorInt int color) throws Exception {

//        TODO Analyse why server response is not available.
//        Fetch bitmap from the server.
//        String requestUrl = String.format(Locale.getDefault(), mosaicUrl, width, height, color);
//        URL url = new URL(requestUrl);
//        Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(color);

        BitmapWrapper bitmapWrapper = new BitmapWrapper();
        bitmapWrapper.setBitmap(bitmap);

        return bitmapWrapper;
    }

    /**
     * Return an immutable/mutable Bitmap from a given image path
     * @param imagePath Location of image to get from Media Store
     */
    @Nullable
    public BitmapWrapper loadBitmapFromMediaStore(@NonNull String imagePath, boolean mutable) {
        BitmapWrapper bitmapWrapper = null;
        try {
            Bitmap result = decodeBitmap(imagePath, MAX_WIDTH, MAX_HEIGHT);
            if (mutable) {
                result = result.copy(Bitmap.Config.ARGB_8888, true);
            }
            bitmapWrapper = new BitmapWrapper();
            bitmapWrapper.setBitmap(result);
        } catch (IOException exception) {
            mLogger.e("Failed to load raw image from media store : "+ exception.getMessage());
        }
        return bitmapWrapper;
    }


    public Set<List<PhotoMosaicRequest>> getMosaicRequestSet(final BitmapWrapper bitmapWrapper, int tileHeight, int tileWidth) {
        Set<List<PhotoMosaicRequest>> mosaicRequestSet = new LinkedHashSet<>();

        int wrapperHeight = bitmapWrapper.getHeight();
        int wrapperWidth = bitmapWrapper.getWidth();

        int tileCountX = (int) Math.ceil((double) wrapperWidth / (double) tileWidth);
        int tileCountY = (int) Math.ceil((double) wrapperHeight / (double) tileHeight);

        int widthOfTheLastTile = wrapperWidth % tileWidth;
        int heightOfTheLastTile = wrapperHeight % tileHeight;

        for (int rowIndex = 0; (rowIndex < tileCountY); rowIndex++) {

            //List which holds Mosaic Request for all individual (32*32) tile in a single row.
            List<PhotoMosaicRequest> mosaicRequestList = new ArrayList<>();

            for (int columnIndex = 0; (columnIndex < tileCountX); columnIndex++) {

                int redColor = 0;
                int blueColor = 0;
                int greenColor = 0;
                int tilePixelCount = 0;
                int averageTileColor = 0;

                int currentTileHeight = tileHeight;
                int currentTileWidth = tileWidth;

                if (rowIndex == tileCountY - 1 && heightOfTheLastTile != 0) {
                    currentTileHeight = heightOfTheLastTile;
                }

                if (columnIndex == tileCountX - 1 && widthOfTheLastTile != 0) {
                    currentTileWidth = widthOfTheLastTile;
                }

                //Get the average color from a single tile
                for (int pixelY = rowIndex * tileHeight; pixelY < (rowIndex * tileHeight) + currentTileHeight; pixelY++) {
                    for (int pixelX = columnIndex * tileWidth; pixelX < (columnIndex * tileWidth) + currentTileWidth; pixelX++) {

                        int pixelColor = bitmapWrapper.getPixel(pixelX, pixelY);
                        redColor += Color.red(pixelColor);
                        blueColor += Color.blue(pixelColor);
                        greenColor += Color.green(pixelColor);
                        tilePixelCount++;

                    }
                }

                redColor = redColor / tilePixelCount;
                greenColor = greenColor / tilePixelCount;
                blueColor = blueColor / tilePixelCount;

                averageTileColor = Color.rgb(redColor, greenColor, blueColor);

                PhotoMosaicRequest photoMosaicRequest = new PhotoMosaicRequest();
                photoMosaicRequest.setAverageColor(averageTileColor);
                photoMosaicRequest.setTileHeight(currentTileHeight);
                photoMosaicRequest.setTileWidth(currentTileWidth);
                photoMosaicRequest.setTileXCoordinate(columnIndex * tileWidth);
                photoMosaicRequest.setTileYCoordinate(rowIndex * tileHeight);

                mosaicRequestList.add(photoMosaicRequest);
            }

            mosaicRequestSet.add(mosaicRequestList);
        }
        return mosaicRequestSet;
    }

    private Bitmap decodeBitmap(@NonNull String imagePath,
                                int reqWidth, int reqHeight) throws IOException {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        Uri imageUri = Uri.parse(imagePath);
        getBitmapFromUri(options, imageUri);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return modifyOrientation(options, imageUri);
    }


    private Bitmap getBitmapFromUri(final BitmapFactory.Options options, final Uri imageUri) throws IOException {

        InputStream inputStream = mContext.getContentResolver().openInputStream(imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        inputStream.close();
        return bitmap;

    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private Bitmap modifyOrientation(final BitmapFactory.Options options, final Uri imageUri) throws IOException {

        Bitmap bitmap = getBitmapFromUri(options, imageUri);
        String mImagePath = getImagePathFromUri(imageUri);
        int rotate = 0;
        ExifInterface exif = new ExifInterface(mImagePath);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private String getImagePathFromUri(final Uri imageUri) {

        String[] filePath = {MediaStore.Images.Media.DATA};
        Cursor cursor = mContext.getContentResolver().query(imageUri, filePath, null, null, null);
        cursor.moveToFirst();
        String string = cursor.getString(cursor.getColumnIndex(filePath[0]));
        cursor.close();
        return string;

    }

}
