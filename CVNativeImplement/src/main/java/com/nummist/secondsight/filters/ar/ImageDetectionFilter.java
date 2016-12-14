package com.nummist.secondsight.filters.ar;

import java.io.IOException;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import android.content.Context;

import com.nummist.secondsight.adapters.CameraProjectionAdapter;

public final class ImageDetectionFilter implements ARFilter {
    
    // The address of the native object.
    private long mSelfAddr;
    
    // An adaptor that provides the camera's projection matrix.
    private final CameraProjectionAdapter mCameraProjectionAdapter;
    
    static {
        // Load the native library if it is not already loaded.
        System.loadLibrary("SecondSight");
    }
    
    public ImageDetectionFilter(final Context context,
            final int referenceImageResourceID,
            final CameraProjectionAdapter cameraProjectionAdapter,
            final double realSize)
                    throws IOException {
        final Mat referenceImageBGR = Utils.loadResource(context,
                referenceImageResourceID,
                Imgcodecs.CV_LOAD_IMAGE_COLOR);
        mSelfAddr = newSelf(referenceImageBGR.getNativeObjAddr(),
                realSize);
        mCameraProjectionAdapter = cameraProjectionAdapter;
    }
    
    @Override
    public void dispose() {
        deleteSelf(mSelfAddr);
        mSelfAddr = 0;
    }
    
    @Override
    protected void finalize() throws Throwable {
        dispose();
    }
    
    @Override
    public float[] getGLPose() {
        return getGLPose(mSelfAddr);
    }
    
    @Override
    public void apply(final Mat src, final Mat dst) {
        final Mat projection =
                mCameraProjectionAdapter.getProjectionCV();
        apply(mSelfAddr, src.getNativeObjAddr(),
                dst.getNativeObjAddr(),
                projection.getNativeObjAddr());
    }
    
    private static native long newSelf(long referenceImageBGRAddr,
            double realSize);
    private static native void deleteSelf(long selfAddr);
    private static native float[] getGLPose(long selfAddr);
    private static native void apply(long selfAddr, long srcAddr,
            long dstAddr, long projectionAddr);
}
