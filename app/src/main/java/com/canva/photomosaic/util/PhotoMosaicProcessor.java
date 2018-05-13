package com.canva.photomosaic.util;

import com.canva.photomosaic.models.BitmapWrapper;
import com.canva.photomosaic.models.PhotoMosaicRequest;
import com.canva.photomosaic.models.PhotoMosaicResponse;

import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.functions.Function;

public class PhotoMosaicProcessor {

    private String mImagePath;
    private BitmapUtil mBitmapUtil;
    private PhotoMosaicDownloader mPhotoMosaicDownloader;

    public PhotoMosaicProcessor(BitmapUtil bitmapUtil, PhotoMosaicDownloader photoMosaicDownloader) {
        mBitmapUtil = bitmapUtil;
        mPhotoMosaicDownloader = photoMosaicDownloader;
    }

    public void initImagePath(final String imagePath) {
        mImagePath = imagePath;
    }

    public boolean isPathEmpty() {
        return !(mImagePath != null && !mImagePath.isEmpty());
    }

    public BitmapWrapper getBitmapWrapperFromImagePath() {
        if (isPathEmpty()) {
            throw new IllegalStateException("Image Path not set!!");
        }
        return mBitmapUtil.loadBitmapFromMediaStore(mImagePath, false);
    }

    public Observable<BitmapWrapper> startMosaicProcessing(final int tileHeight, final int tileWidth) {

        if (isPathEmpty()) {
            throw new IllegalStateException("Image Path not set!!");
        }

        final BitmapWrapper bitmapWrapper = mBitmapUtil.loadBitmapFromMediaStore(mImagePath, true);
        return createTileRequests(bitmapWrapper, tileHeight, tileWidth)
                .map(new Function<PhotoMosaicResponse, BitmapWrapper>() {
                    @Override
                    public BitmapWrapper apply(final PhotoMosaicResponse photoMosaicResponse) throws Exception {

                        BitmapWrapper tileBitmapWrapper = photoMosaicResponse.getBitmapWrapper();
                        int bitmapWidth = tileBitmapWrapper.getWidth();
                        int bitmapHeight = tileBitmapWrapper.getHeight();
                        int[] pixels = new int[bitmapHeight * bitmapWidth];

                        tileBitmapWrapper.getPixels(pixels,
                                0,
                                bitmapWidth,
                                0,
                                0,
                                bitmapWidth,
                                bitmapHeight);

                        bitmapWrapper
                                .setPixels(pixels,
                                        0,
                                        bitmapWidth,
                                        photoMosaicResponse.getTileXPosition(),
                                        photoMosaicResponse.getTileYPosition(),
                                        bitmapWidth,
                                        bitmapHeight);
                        return bitmapWrapper;
                    }
                });
    }

    private Observable<PhotoMosaicResponse> createTileRequests(final BitmapWrapper bitmapWrapper, final int tileHeight, final int tileWidth) {
        return Observable.just(bitmapWrapper)
                .map(new Function<BitmapWrapper, Set<List<PhotoMosaicRequest>>>() {
                    public Set<List<PhotoMosaicRequest>> apply(final BitmapWrapper wrapper) throws Exception {
                        return mBitmapUtil.getMosaicRequestSet(wrapper, tileHeight, tileWidth);
                    }
                })
                .flatMapIterable(new Function<Set<List<PhotoMosaicRequest>>, Iterable<List<PhotoMosaicRequest>>>() {
                    @Override
                    public Iterable<List<PhotoMosaicRequest>> apply(final Set<List<PhotoMosaicRequest>> lists) throws Exception {
                        return lists;
                    }
                })
                .concatMap(new Function<List<PhotoMosaicRequest>, ObservableSource<PhotoMosaicResponse>>() {
                    @Override
                    public ObservableSource<PhotoMosaicResponse> apply(final List<PhotoMosaicRequest> photoMosaicRequests) throws Exception {
                        return Observable
                                .fromIterable(photoMosaicRequests)
                                .flatMapSingle(new Function<PhotoMosaicRequest, Single<PhotoMosaicResponse>>() {
                                    @Override
                                    public Single<PhotoMosaicResponse> apply(final PhotoMosaicRequest photoMosaicRequest) throws Exception {
                                        return getMosaic(photoMosaicRequest);
                                    }
                                });
                    }
                });
    }

    private Single<PhotoMosaicResponse> getMosaic(final PhotoMosaicRequest photoMosaicRequest) {
        return mPhotoMosaicDownloader.getMosaic(photoMosaicRequest);
    }
}
