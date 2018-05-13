package com.canva.photomosaic.ui;

import com.canva.photomosaic.models.BitmapWrapper;

public interface PhotoMosaicContract {

    interface Presenter {
        void onViewAttached();

        void onViewDetached();

        void startMosaicJobOnImage();

        void onImageSelected(String imagePath);

        void onSelectImageClicked();

        void onReadStoragePermissionGranted();
    }

    interface View {
        void showProgress();

        void hideProgress();

        void requestStorageAccessPermission();

        void launchGallery();

        void setImageBitmap(BitmapWrapper bitmap);

        void setStartMosaicButtonEnabled();

        void showErrorFeedback();

        void showProcessingFailedFeedback();

        void showProcessingCompleteFeedback();
    }

}
