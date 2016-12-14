package com.nummist.secondsight.filters;

import org.opencv.core.Mat;

public class NoneFilter implements Filter {

    @Override
    public void apply(Mat src, Mat dst) {
        // Do nothing.
    }
}
