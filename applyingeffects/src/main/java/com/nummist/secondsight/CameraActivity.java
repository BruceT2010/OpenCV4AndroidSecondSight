package com.nummist.secondsight;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.nummist.secondsight.applyingeffects.R;
import com.nummist.secondsight.filters.Filter;
import com.nummist.secondsight.filters.NoneFilter;
import com.nummist.secondsight.filters.convolution.StrokeEdgesFilter;
import com.nummist.secondsight.filters.curve.CrossProcessCurveFilter;
import com.nummist.secondsight.filters.curve.PortraCurveFilter;
import com.nummist.secondsight.filters.curve.ProviaCurveFilter;
import com.nummist.secondsight.filters.curve.VelviaCurveFilter;
import com.nummist.secondsight.filters.mixer.RecolorCMVFilter;
import com.nummist.secondsight.filters.mixer.RecolorRCFilter;
import com.nummist.secondsight.filters.mixer.RecolorRGVFilter;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

// Use the deprecated Camera class.
@SuppressWarnings("deprecation")
public class CameraActivity extends ActionBarActivity implements CvCameraViewListener2 {

    // A tag for log output.
    private static String TAG = CameraActivity.class.getSimpleName();

    // The filters.
    private Filter[] mCurveFilters;
    private Filter[] mMixerFilters;
    private Filter[] mConvolutionFilters;

    // The indices of the active filters.
    private int mCurveFilterIndex;
    private int mMixerFilterIndex;
    private int mConvolutionFilterIndex;

    // The camera view.
    private CameraBridgeViewBase mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_next_curve_filter:
                mCurveFilterIndex++;
                if (mCurveFilterIndex == mCurveFilters.length) {
                    mCurveFilterIndex = 0;
                }
                return true;
            case R.id.menu_next_mixer_filter:
                mMixerFilterIndex++;
                if (mMixerFilterIndex == mMixerFilters.length) {
                    mMixerFilterIndex = 0;
                }
                return true;
            case R.id.menu_next_convolution_filter:
                mConvolutionFilterIndex++;
                if (mConvolutionFilterIndex == mConvolutionFilters.length) {
                    mConvolutionFilterIndex = 0;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mCurveFilters = new Filter[]{
                new NoneFilter(),
                new PortraCurveFilter(),
                new ProviaCurveFilter(),
                new VelviaCurveFilter(),
                new CrossProcessCurveFilter()
        };
        mMixerFilters = new Filter[]{
                new NoneFilter(),
                new RecolorRCFilter(),
                new RecolorRGVFilter(),
                new RecolorCMVFilter()
        };
        mConvolutionFilters = new Filter[]{
                new NoneFilter(),
                new StrokeEdgesFilter()
        };
    }

    @Override
    public void onCameraViewStopped() {
    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        Mat rgba = inputFrame.rgba();

        // Apply the active filters.
        mCurveFilters[mCurveFilterIndex].apply(rgba, rgba);
        mMixerFilters[mMixerFilterIndex].apply(rgba, rgba);
        mConvolutionFilters[mConvolutionFilterIndex].apply(rgba, rgba);
        return rgba;
    }

}