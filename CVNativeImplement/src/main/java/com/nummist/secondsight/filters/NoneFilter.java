package com.nummist.secondsight.filters;

import org.opencv.core.Mat;

public class NoneFilter implements Filter {
    
    @Override
    public void dispose() {
        // Do nothing.
    }
    
    @Override
    public void apply(final Mat src, final Mat dst) {
        // Do nothing.
    }
}
