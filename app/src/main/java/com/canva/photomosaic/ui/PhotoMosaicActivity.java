package com.canva.photomosaic.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.canva.photomosaic.R;
import com.canva.photomosaic.models.BitmapWrapper;
import com.canva.photomosaic.providers.SchedulersProvider;
import com.canva.photomosaic.util.BitmapUtil;
import com.canva.photomosaic.util.Logger;
import com.canva.photomosaic.util.PermissionChecker;
import com.canva.photomosaic.util.PhotoMosaicDownloader;
import com.canva.photomosaic.util.PhotoMosaicProcessor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class PhotoMosaicActivity extends AppCompatActivity implements PhotoMosaicContract.View {

    private static final int PICK_IMAGE_REQUEST_CODE = 1;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 2;

    private static final int TILE_HEIGHT = 32;
    private static final int TILE_WIDTH = 32;

    private PhotoMosaicContract.Presenter mPresenter;

    @BindView(R.id.parent_constraint_layout)
    View mParentLayout;

    @BindView(R.id.mosaic_image_view)
    ImageView mMosaicImageView;

    @BindView(R.id.start_mosaic_button)
    Button mStartMosaicButton;

    @BindView(R.id.select_image_button)
    Button mSelectImageButton;

    @BindView(R.id.progress_layout)
    ViewGroup mProgressLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mPresenter = injectDependencies();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onViewAttached();
    }

    @Override
    protected void onStop() {
        mPresenter.onViewDetached();
        super.onStop();
    }

    @OnClick(R.id.select_image_button)
    protected void onSelectImageButtonClick() {
        mPresenter.onSelectImageClicked();
    }

    @OnClick(R.id.start_mosaic_button)
    protected void onStartMosaicButtonClick() {
        mPresenter.startMosaicJobOnImage();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST_CODE && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                mPresenter.onImageSelected(selectedImageUri.toString());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        if (requestCode == PICK_IMAGE_REQUEST_CODE) {
            checkPermissionStatus(grantResults);
        }
    }

    private void checkPermissionStatus(@NonNull int[] grantResults) {
        boolean allAccepted = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                allAccepted = false;
                break;
            }
        }
        if (allAccepted) {
            mPresenter.onReadStoragePermissionGranted();
        }
    }

    //View Implementations
    @Override
    public void requestStorageAccessPermission() {
        String[] storagePermissions = {READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void launchGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    public void setImageBitmap(final BitmapWrapper bitmapWrapper) {
        mMosaicImageView.setImageBitmap(bitmapWrapper.getBitmap());
    }

    @Override
    public void setStartMosaicButtonEnabled() {
        mStartMosaicButton.setEnabled(true);
    }

    @Override
    public void showProgress() {
        mProgressLayout.setVisibility(View.VISIBLE);
        mStartMosaicButton.setEnabled(false);
        mSelectImageButton.setEnabled(false);
    }

    @Override
    public void hideProgress() {
        mProgressLayout.setVisibility(View.GONE);
        mStartMosaicButton.setEnabled(true);
        mSelectImageButton.setEnabled(true);
    }

    @Override
    public void showErrorFeedback() {
        Snackbar.make(mParentLayout, R.string.user_feedback_error, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void showProcessingFailedFeedback() {
        Snackbar.make(mParentLayout, R.string.user_feedback_processing_failed, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void showProcessingCompleteFeedback() {
        Snackbar.make(mParentLayout, R.string.user_feedback_processing_successful, Snackbar.LENGTH_LONG)
                .show();
    }

    private PhotoMosaicPresenter injectDependencies() {
        Logger logger = new Logger();

        BitmapUtil bitmapUtil = new BitmapUtil(this, logger);

        PhotoMosaicDownloader photoMosaicDownloader = new PhotoMosaicDownloader(bitmapUtil);

        PhotoMosaicProcessor photoMosaicProcessor = new PhotoMosaicProcessor(bitmapUtil, photoMosaicDownloader);

        PermissionChecker permissionChecker = new PermissionChecker(this);

        SchedulersProvider schedulerProvider = new SchedulersProvider();

        return new PhotoMosaicPresenter(photoMosaicProcessor, permissionChecker, schedulerProvider, this, TILE_HEIGHT, TILE_WIDTH);
    }

}
