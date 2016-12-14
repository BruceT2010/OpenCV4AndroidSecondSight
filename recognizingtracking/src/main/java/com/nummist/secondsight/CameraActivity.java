package com.nummist.secondsight;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.nummist.secondsight.filters.Filter;
import com.nummist.secondsight.filters.NoneFilter;
import com.nummist.secondsight.filters.ar.ImageDetectionFilter;
import com.nummist.secondsight.recognizingtracking.R;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.IOException;

// Use the deprecated Camera class.
@SuppressWarnings("deprecation")
public final class CameraActivity extends ActionBarActivity implements CvCameraViewListener2 {

    // A tag for log output.
    private static final String TAG = CameraActivity.class.getSimpleName();

    // The filters.
    private Filter[] mImageDetectionFilters;

    // The indices of the active filters.
    private int mImageDetectionFilterIndex;

    // The camera view.
    private CameraBridgeViewBase mCameraView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mCameraView = new JavaCameraView(this, 0);
        mCameraView.setCvCameraViewListener(this);
        setContentView(mCameraView);
        mCameraView.enableView();
    }

    @Override
    public void onPause() {
        if (mCameraView != null) {
            mCameraView.disableView();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initDebug();
    }

    @Override
    public void onDestroy() {
        if (mCameraView != null) {
            mCameraView.disableView();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activity_camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_next_image_detection_filter:
                mImageDetectionFilterIndex++;
                if (mImageDetectionFilterIndex == mImageDetectionFilters.length) {
                    mImageDetectionFilterIndex = 0;
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCameraViewStarted(final int width, final int height) {
        Filter starryNight = null;
        try {
            starryNight = new ImageDetectionFilter(CameraActivity.this, R.drawable.starry_night);
        } catch (IOException e) {
            e.printStackTrace();
        }


        Filter akbarHunting = null;
        try {
            akbarHunting = new ImageDetectionFilter(CameraActivity.this, R.drawable.akbar_hunting_with_cheetahs);
        } catch (IOException e) {
            Log.e(TAG, "Failed to load drawable: " + "akbar_hunting_with_cheetahs");
            e.printStackTrace();
        }

        mImageDetectionFilters = new Filter[]{
                new NoneFilter(),
                starryNight,
                akbarHunting
        };
    }

    @Override
    public void onCameraViewStopped() {
    }

    @Override
    public Mat onCameraFrame(final CvCameraViewFrame inputFrame) {
        final Mat rgba = inputFrame.rgba();

        if (mImageDetectionFilters != null) {
            mImageDetectionFilters[mImageDetectionFilterIndex].apply(rgba, rgba);
        }

        return rgba;
    }

}
