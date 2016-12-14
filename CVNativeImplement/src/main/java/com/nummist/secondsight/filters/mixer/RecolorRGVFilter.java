package com.nummist.secondsight.filters.mixer;

import org.opencv.core.Mat;

import com.nummist.secondsight.filters.Filter;

public final class RecolorRGVFilter implements Filter {
    
    private long mSelfAddr;
    
    static {
        // Load the native library if it is not already loaded.
        System.loadLibrary("SecondSight");
    }
    
    public RecolorRGVFilter() {
        mSelfAddr = newSelf();
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
    public void apply(final Mat src, final Mat dst) {
        apply(mSelfAddr, src.getNativeObjAddr(),
                dst.getNativeObjAddr());
    }
    
    private static native long newSelf();
    private static native void deleteSelf(long selfAddr);
    private static native void apply(long selfAddr, long srcAddr,
            long dstAddr);
}
