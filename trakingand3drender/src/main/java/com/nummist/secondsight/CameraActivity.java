package com.nummist.secondsight;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.nummist.secondsight.adapters.CameraProjectionAdapter;
import com.nummist.secondsight.filters.ar.ARFilter;
import com.nummist.secondsight.filters.ar.ImageDetectionFilter;
import com.nummist.secondsight.filters.ar.NoneARFilter;
import com.nummist.secondsight.trakingand3drender.R;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.IOException;
import java.util.List;

// Use the deprecated Camera class.
@SuppressWarnings("deprecation")
public final class CameraActivity extends ActionBarActivity implements CvCameraViewListener2 {

    // A tag for log output.
    private static final String TAG = CameraActivity.class.getSimpleName();

    private int mImageDetectionFilterIndex;
    private ARFilter[] mImageDetectionFilters;

    // The camera view.
    private CameraBridgeViewBase mCameraView;

    // An adapter between the video camera and projection matrix.
    private CameraProjectionAdapter mCameraProjectionAdapter;

    // The renderer for 3D augmentations.
    private ARCubeRenderer mARRenderer;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final FrameLayout layout = new FrameLayout(this);
        layout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        setContentView(layout);

        mCameraView = new JavaCameraView(this, 0);
        mCameraView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        layout.addView(mCameraView);

        GLSurfaceView glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        glSurfaceView.setZOrderOnTop(true);
        glSurfaceView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        layout.addView(glSurfaceView);

        mCameraProjectionAdapter = new CameraProjectionAdapter();

        mARRenderer = new ARCubeRenderer();
        mARRenderer.cameraProjectionAdapter = mCameraProjectionAdapter;

        mARRenderer.scale = 0.5f;
        glSurfaceView.setRenderer(mARRenderer);

        Camera camera;
        camera = Camera.open();
        Parameters parameters = camera.getParameters();
        camera.release();
        List<Size> mSupportedImageSizes = parameters.getSupportedPreviewSizes();
        final Size size = mSupportedImageSizes.get(0);
        mCameraProjectionAdapter.setCameraParameters(parameters, size);

        mCameraView.setMaxFrameSize(size.width, size.height);
        mCameraView.setCvCameraViewListener(this);
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
                mARRenderer.filter = mImageDetectionFilters[mImageDetectionFilterIndex];
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCameraViewStarted(final int width, final int height) {
        ARFilter starryNight = null;
        try {
            // Define The Starry Night to be 1.0 units tall.
            starryNight = new ImageDetectionFilter(CameraActivity.this,
                    R.drawable.starry_night,
                    mCameraProjectionAdapter, 1.0);
        } catch (IOException e) {
            Log.e(TAG, "Failed to load drawable: " + "starry_night");
            e.printStackTrace();
        }

        ARFilter akbarHunting = null;
        try {
            // Define Akbar Hunting with Cheetahs to be 1.0
            // units wide.
            akbarHunting = new ImageDetectionFilter(CameraActivity.this,
                    R.drawable.akbar_hunting_with_cheetahs,
                    mCameraProjectionAdapter, 1.0);
        } catch (IOException e) {
            Log.e(TAG, "Failed to load drawable: " + "akbar_hunting_with_cheetahs");
            e.printStackTrace();
        }

        mImageDetectionFilters = new ARFilter[] {
                new NoneARFilter(),
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

        // Apply the active filters.
        if (mImageDetectionFilters != null) {
            mImageDetectionFilters[mImageDetectionFilterIndex].apply(rgba, rgba);
        }

        return rgba;
    }

}
