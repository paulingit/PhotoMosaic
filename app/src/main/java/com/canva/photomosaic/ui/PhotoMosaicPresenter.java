package com.canva.photomosaic.ui;

import android.support.annotation.NonNull;

import com.canva.photomosaic.models.BitmapWrapper;
import com.canva.photomosaic.util.PhotoMosaicProcessor;
import com.canva.photomosaic.util.PermissionChecker;
import com.canva.photomosaic.providers.SchedulersProvider;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;


public class PhotoMosaicPresenter implements PhotoMosaicContract.Presenter {

    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private PhotoMosaicProcessor mPhotoMosaicProcessor;
    private final PermissionChecker mPermissionChecker;
    private final SchedulersProvider mSchedulerProvider;
    private final PhotoMosaicContract.View mView;
    private int mTileHeight;
    private int mTileWidth;

    private Observable<BitmapWrapper> mosaicProcessorCache;

    public PhotoMosaicPresenter(PhotoMosaicProcessor photoMosaicProcessor,
                                PermissionChecker permissionChecker,
                                SchedulersProvider schedulerProvider,
                                PhotoMosaicContract.View view,
                                int tileHeight,
                                int tileWidth) {
        mPhotoMosaicProcessor = photoMosaicProcessor;
        mPermissionChecker = permissionChecker;
        mView = view;
        mSchedulerProvider = schedulerProvider;
        mTileHeight = tileHeight;
        mTileWidth = tileWidth;
    }

    @Override
    public void onViewAttached() {
        if(mosaicProcessorCache!=null){
            Disposable disposable = mosaicProcessorCache.subscribeWith(getMosaicDisposableObserver());
            mCompositeDisposable.add(disposable);
        }
    }

    @Override
    public void onViewDetached() {
        mCompositeDisposable.clear();
    }

    @Override
    public void onImageSelected(final String imagePath) {
        mView.hideProgress();
        mPhotoMosaicProcessor.initImagePath(imagePath);
        mView.setImageBitmap(mPhotoMosaicProcessor.getBitmapWrapperFromImagePath());
        mView.setStartMosaicButtonEnabled();

    }

    @Override
    public void onSelectImageClicked() {
        if (!mPermissionChecker.hasStorageReadPermission()) {
            mView.requestStorageAccessPermission();
        } else {
            mView.launchGallery();
        }
    }

    @Override
    public void onReadStoragePermissionGranted() {
        mView.launchGallery();
    }


    @Override
    public void startMosaicJobOnImage() {
        if (!mPhotoMosaicProcessor.isPathEmpty()) {
            mView.showProgress();
            mosaicProcessorCache = mPhotoMosaicProcessor.startMosaicProcessing(mTileHeight, mTileWidth)
                    .subscribeOn(mSchedulerProvider.io())
                    .observeOn(mSchedulerProvider.mainUiThread())
                    .cache();
            Disposable disposable = mosaicProcessorCache
                    .subscribeWith(getMosaicDisposableObserver());

            mCompositeDisposable.add(disposable);
        } else {
            //Ask the user to select an image.
            mView.showErrorFeedback();
        }

    }

    @NonNull
    private DisposableObserver<BitmapWrapper> getMosaicDisposableObserver() {
        return new DisposableObserver<BitmapWrapper>() {

            @Override
            public void onNext(final BitmapWrapper bitmapWrapper) {
                mView.setImageBitmap(bitmapWrapper);
            }

            @Override
            public void onError(final Throwable e) {
                mView.hideProgress();
                mView.showProcessingFailedFeedback();
                mosaicProcessorCache = null;
            }

            @Override
            public void onComplete() {
                mView.hideProgress();
                mView.showProcessingCompleteFeedback();
                mosaicProcessorCache = null;
            }

        };
    }


}
