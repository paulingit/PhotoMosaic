package com.canva.photomosaic.util;

import com.canva.photomosaic.models.BitmapWrapper;
import com.canva.photomosaic.models.PhotoMosaicRequest;
import com.canva.photomosaic.models.PhotoMosaicResponse;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Single;

public class PhotoMosaicDownloader {

    private static final String MOSAIC_SERVER = "http://192.168.1.6:8765/color/%d/%d/%s";

    private BitmapUtil mBitmapUtil;

    public PhotoMosaicDownloader(BitmapUtil bitmapUtil) {
        mBitmapUtil = bitmapUtil;
    }

    public Single<PhotoMosaicResponse> getMosaic(final PhotoMosaicRequest photoMosaicRequest) {

        return Single.fromCallable(new Callable<PhotoMosaicResponse>() {
            @Override
            public PhotoMosaicResponse call() throws Exception {

                BitmapWrapper bitmapWrapper = mBitmapUtil.getBitmap(MOSAIC_SERVER, photoMosaicRequest.getTileWidth(), photoMosaicRequest.getTileHeight(), photoMosaicRequest.getAverageColor());

                PhotoMosaicResponse photoMosaicResponse = new PhotoMosaicResponse();
                photoMosaicResponse.setBitmapWrapper(bitmapWrapper);
                photoMosaicResponse.setTileXPosition(photoMosaicRequest.getTileXCoordinate());
                photoMosaicResponse.setTileYPosition(photoMosaicRequest.getTileYCoordinate());
                return photoMosaicResponse;
            }
        });

    }
}
