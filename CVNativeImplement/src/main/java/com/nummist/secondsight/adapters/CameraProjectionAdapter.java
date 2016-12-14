package com.nummist.secondsight.adapters;

import org.opencv.core.CvType;
import org.opencv.core.MatOfDouble;

import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.opengl.Matrix;

// Use the deprecated Camera class.
@SuppressWarnings("deprecation")
public final class CameraProjectionAdapter {
    
    float mFOVY = 45f; // equivalent in 35mm photography: 28mm lens
    float mFOVX = 60f; // equivalent in 35mm photography: 28mm lens
    int mHeightPx = 480;
    int mWidthPx = 640;
    float mNear = 0.1f;
    float mFar = 10f;
    
    final float[] mProjectionGL = new float[16];
    boolean mProjectionDirtyGL = true;
    
    MatOfDouble mProjectionCV;
    boolean mProjectionDirtyCV = true;
    
    public void setCameraParameters(
            final Parameters cameraParameters,
            final Size imageSize) {
        mFOVY = cameraParameters.getVerticalViewAngle();
        mFOVX = cameraParameters.getHorizontalViewAngle();
        
        mHeightPx = imageSize.height;
        mWidthPx = imageSize.width;
        
        mProjectionDirtyGL = true;
        mProjectionDirtyCV = true;
    }
    
    public float getAspectRatio() {
        return (float)mWidthPx / (float)mHeightPx;
    }
    
    public void setClipDistances(float near, float far) {
        mNear = near;
        mFar = far;
        mProjectionDirtyGL = true;
    }
    
    public float[] getProjectionGL() {
        if (mProjectionDirtyGL) {
            final float right =
                    (float)Math.tan(0.5f * mFOVX * Math.PI / 180f) * mNear;
            // Calculate vertical bounds based on horizontal bounds
            // and the image's aspect ratio. Some aspect ratios will
            // be crop modes that do not use the full vertical FOV
            // reported by Camera.Paremeters.
            final float top = right / getAspectRatio();
            Matrix.frustumM(mProjectionGL, 0,
                    -right, right, -top, top, mNear, mFar);
            mProjectionDirtyGL = false;
        }
        return mProjectionGL;
    }
    
    public MatOfDouble getProjectionCV() {
        if (mProjectionDirtyCV) {
            if (mProjectionCV == null) {
                mProjectionCV = new MatOfDouble();
                mProjectionCV.create(3, 3, CvType.CV_64FC1);
            }
            
            // Note that the FOV, image size, and focal length have
            // the following relationship:
            // diagonalFOV = 2 * atan(0.5 * diagonalPx / focalLengthPx)
            
            // Solving for the focal length:
            // focalLengthPx = 0.5 * diagonalPx / tan(0.5 * diagonalFOV)
            
            // Note that tan(0.5 * diagonalFOV) is the hypotenuse of
            // tan(0.5 * fovX) and tan(0.5 * fovY). Thus:
            // focalLengthPx = 0.5 * diagonalPx /
            //         sqrt((tan(0.5 * fovX))^2 + (tan(0.5 * fovY)^2))
            
            // Calculate focal length using the aspect ratio of the
            // FOV values reported by Camera.Parameters. This is not
            // necessarily the same as the image's current aspect
            // ratio, which might be a crop mode.
            final float fovAspectRatio = mFOVX / mFOVY;
            final double diagonalPx = Math.sqrt(
                    (Math.pow(mWidthPx, 2.0) +
                    Math.pow(mWidthPx / fovAspectRatio, 2.0)));
            final double focalLengthPx = 0.5 * diagonalPx / Math.sqrt(
                    Math.pow(Math.tan(0.5 * mFOVX * Math.PI / 180f), 2.0) +
                    Math.pow(Math.tan(0.5 * mFOVY * Math.PI / 180f), 2.0));
            
            mProjectionCV.put(0, 0, focalLengthPx);
            mProjectionCV.put(0, 1, 0.0);
            mProjectionCV.put(0, 2, 0.5 * mWidthPx);
            mProjectionCV.put(1, 0, 0.0);
            mProjectionCV.put(1, 1, focalLengthPx);
            mProjectionCV.put(1, 2, 0.5 * mHeightPx);
            mProjectionCV.put(2, 0, 0.0);
            mProjectionCV.put(2, 1, 0.0);
            mProjectionCV.put(2, 2, 1.0);
        }
        return mProjectionCV;
    }
}
